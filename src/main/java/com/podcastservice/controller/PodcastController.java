package com.podcastservice.controller;

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
import com.podcastservice.dto.podcast.PodcastDetailsDto;
import com.podcastservice.dto.podcast.PodcastSaveDto;
import com.podcastservice.service.PodcastService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/podcasts")
@RequiredArgsConstructor
public class PodcastController {

    private final PodcastService podcastService;

    // POST api/podcasts/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createPodcast(@Valid @RequestBody PodcastSaveDto dto) {
        int id = podcastService.createPodcast(dto);
        return new RestResponse(String.valueOf(id));
    }

    // GET /api/podcasts/{id}
    @GetMapping("/{id}")
    public PodcastDetailsDto getPodcast(@PathVariable int id) {
        return podcastService.getPodcast(id);
    }

    // PUT /api/podcasts/{id}
    @PutMapping("/{id}")
    public RestResponse updatePodcast(@PathVariable int id, @Valid @RequestBody PodcastSaveDto dto) {
        podcastService.updatePodcast(id, dto);
        return new RestResponse("OK");
    }

    // DELETE /api/podcasts/{id}
    @DeleteMapping("/{id}")
    public void deletePodcast(@PathVariable int id) {
        podcastService.deletePodcast(id);
    }

    // TODO: POST /api/podcasts/_list
    // TODO: POST /api/podcasts/_report
    // TODO: POST /api/podcasts/upload
}
