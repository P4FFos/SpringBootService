package com.podcastservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.podcastservice.dto.episode.EpisodeSaveDto;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;
import com.podcastservice.repository.EpisodeRepository;
import com.podcastservice.repository.PodcastRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EpisodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    private Podcast podcast;

    @BeforeEach
    void setUp() {
        episodeRepository.deleteAll();
        podcastRepository.deleteAll();
        podcast = podcastRepository.save(new Podcast("Test Podcast", "Test Host"));
    }

    @Test
    void getEpisodes_ShouldReturnList() throws Exception {
        Episode e1 = new Episode("Ep 1", LocalDate.now(), 100);
        e1.setPodcast(podcast);
        episodeRepository.save(e1);

        mockMvc.perform(get("/api/episodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Ep 1")));
    }

    @Test
    void createEpisode_ShouldCreateAndReturnId() throws Exception {
        EpisodeSaveDto dto = new EpisodeSaveDto("New Episode", LocalDate.now(), 120, java.util.List.of("tag1"), "Guest",
                podcast.getId());

        mockMvc.perform(post("/api/episodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateEpisode_ShouldUpdateDetails() throws Exception {
        Episode episode = new Episode("Old Title", LocalDate.now(), 100);
        episode.setPodcast(podcast);
        episode = episodeRepository.save(episode);

        EpisodeSaveDto dto = new EpisodeSaveDto("New Title", LocalDate.now(), 150, java.util.List.of("tag2"),
                "New Guest", podcast.getId());

        mockMvc.perform(put("/api/episodes/{id}", episode.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OK")));

        Episode updated = episodeRepository.findById(episode.getId()).orElseThrow();
        assert updated.getTitle().equals("New Title");
        assert updated.getDuration() == 150;
    }

    @Test
    void deleteEpisode_ShouldDelete() throws Exception {
        Episode episode = new Episode("To Delete", LocalDate.now(), 100);
        episode.setPodcast(podcast);
        episode = episodeRepository.save(episode);

        mockMvc.perform(delete("/api/episodes/{id}", episode.getId()))
                .andExpect(status().isOk());

        assert episodeRepository.findById(episode.getId()).isEmpty();
    }
}
