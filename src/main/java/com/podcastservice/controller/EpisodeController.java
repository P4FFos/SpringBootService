package com.podcastservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.podcastservice.dto.RestResponse;
import com.podcastservice.dto.episode.EpisodeDetailsDto;
import com.podcastservice.dto.episode.EpisodeSaveDto;
import com.podcastservice.service.EpisodeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    // GET /api/episodes
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EpisodeDetailsDto> getEpisodes() {
        return episodeService.getEpisodes();
    }

    // POST /api/episodes/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createEpisode(@Valid @RequestBody EpisodeSaveDto dto) {
        int id = episodeService.createEpisode(dto);
        return new RestResponse(String.valueOf(id));
    }

    // PUT /api/episodes/{id}
    @PutMapping("/{id}")
    public RestResponse updateEpisode(@PathVariable int id, @Valid @RequestBody EpisodeSaveDto dto) {
        episodeService.updateEpisode(id, dto);
        return new RestResponse("OK");
    }

    // DELETE /api/episodes/{id}
    @DeleteMapping("/{id}")
    public void deleteEpisode(@PathVariable int id) {
        episodeService.deleteEpisode(id);
    }

}
