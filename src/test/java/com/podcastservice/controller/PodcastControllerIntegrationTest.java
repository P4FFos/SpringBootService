package com.podcastservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.podcastservice.dto.podcast.PodcastFilterRequest;
import com.podcastservice.dto.podcast.PodcastSaveDto;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;
import com.podcastservice.repository.EpisodeRepository;
import com.podcastservice.repository.PodcastRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PodcastControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @BeforeEach
    void setUp() {
        episodeRepository.deleteAll();
        podcastRepository.deleteAll();
    }

    @Test
    void createPodcast_ShouldCreateAndReturnId() throws Exception {
        PodcastSaveDto dto = new PodcastSaveDto("New Podcast", "Host Name", null);

        mockMvc.perform(post("/api/podcasts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getPodcast_ShouldReturnDetails() throws Exception {
        Podcast podcast = podcastRepository.save(new Podcast("My Podcast", "Me"));
        Episode episode = new Episode("Ep 1", LocalDate.now(), 100);
        podcast.addEpisode(episode);
        podcastRepository.save(podcast);

        mockMvc.perform(get("/api/podcasts/{id}", podcast.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("My Podcast")))
                .andExpect(jsonPath("$.host", is("Me")))
                .andExpect(jsonPath("$.episodes", hasSize(1)))
                .andExpect(jsonPath("$.episodes[0].title", is("Ep 1")));
    }

    @Test
    void updatePodcast_ShouldUpdateDetails() throws Exception {
        Podcast podcast = podcastRepository.save(new Podcast("Old Name", "Old Host"));

        PodcastSaveDto dto = new PodcastSaveDto("New Name", "New Host", null);

        mockMvc.perform(put("/api/podcasts/{id}", podcast.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OK")));

        Podcast updated = podcastRepository.findById(podcast.getId()).orElseThrow();
        assert updated.getName().equals("New Name");
        assert updated.getHost().equals("New Host");
    }

    @Test
    void deletePodcast_ShouldDelete() throws Exception {
        Podcast podcast = podcastRepository.save(new Podcast("To Delete", "Host"));

        mockMvc.perform(delete("/api/podcasts/{id}", podcast.getId()))
                .andExpect(status().isOk());

        assert podcastRepository.findById(podcast.getId()).isEmpty();
    }

    @Test
    void listPodcasts_ShouldReturnFilteredList() throws Exception {
        podcastRepository.save(new Podcast("Tech Talk", "Alice"));
        podcastRepository.save(new Podcast("Life Talk", "Bob"));

        PodcastFilterRequest request = new PodcastFilterRequest();
        request.setHost("Alice");

        mockMvc.perform(post("/api/podcasts/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list", hasSize(1)))
                .andExpect(jsonPath("$.list[0].name", is("Tech Talk")))
                .andExpect(jsonPath("$.episodes", is(0)));
    }

    @Test
    void buildReport_ShouldReturnCsv() throws Exception {
        Podcast p1 = podcastRepository.save(new Podcast("Tech Talk", "Alice"));
        Episode e1 = new Episode("Ep 1", LocalDate.now(), 100);
        p1.addEpisode(e1);
        podcastRepository.save(p1);

        PodcastFilterRequest request = new PodcastFilterRequest();

        mockMvc.perform(post("/api/podcasts/_report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"podcasts_report.csv\""))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void upload_ShouldImportPodcasts() throws Exception {
        String jsonContent = """
                [
                  {
                    "name": "Imported Podcast",
                    "host": "Imported Host",
                    "episodes": [
                      {
                        "title": "Ep 1",
                        "releaseDate": "2023-01-01",
                        "duration": 100,
                        "tags": ["tag1"],
                        "guest": "Guest"
                      }
                    ]
                  }
                ]
                """;
        MockMultipartFile file = new MockMultipartFile("file", "import.json", MediaType.APPLICATION_JSON_VALUE,
                jsonContent.getBytes());

        mockMvc.perform(multipart("/api/podcasts/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", is(1)))
                .andExpect(jsonPath("$.failed", is(0)));

        assert podcastRepository.existsByName("Imported Podcast");
    }
}
