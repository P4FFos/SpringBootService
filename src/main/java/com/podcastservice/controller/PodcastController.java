package com.podcastservice.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.podcastservice.dto.RestResponse;
import com.podcastservice.dto.podcast.PodcastFilterRequest;
import com.podcastservice.dto.podcast.PodcastDetailsDto;
import com.podcastservice.dto.podcast.PodcastImportResponse;
import com.podcastservice.dto.podcast.PodcastListResponse;
import com.podcastservice.dto.podcast.PodcastReportResult;
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

    // POST /api/podcasts/_list
    @PostMapping("/_list")
    public PodcastListResponse listPodcasts(@Valid @RequestBody PodcastFilterRequest request) {
        return podcastService.listPodcasts(request);
    }

    // POST /api/podcasts/_report
    @PostMapping("/_report")
    public ResponseEntity<byte[]> buildReport(@Valid @RequestBody PodcastFilterRequest request) {
        PodcastReportResult report = podcastService.exportReport(request);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(report.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(report.getFileName()))
                .body(report.getContent());
    }

    // POST /api/podcasts/upload
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PodcastImportResponse upload(@RequestParam("file") MultipartFile file) {
        return podcastService.uploadPodcasts(file);
    }
}
