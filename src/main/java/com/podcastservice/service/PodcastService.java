package com.podcastservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.podcastservice.dto.podcast.PodcastDetailsDto;
import com.podcastservice.dto.podcast.PodcastFilterRequest;
import com.podcastservice.dto.podcast.PodcastImportResponse;
import com.podcastservice.dto.podcast.PodcastListResponse;
import com.podcastservice.dto.podcast.PodcastReportResult;
import com.podcastservice.dto.podcast.PodcastSaveDto;
import com.podcastservice.entity.Podcast;

// business logic for podcasts
public interface PodcastService {

    int createPodcast(PodcastSaveDto dto);

    PodcastDetailsDto getPodcast(int id);

    void updatePodcast(int id, PodcastSaveDto dto);

    void deletePodcast(int id);

    Podcast getOrThrow(int id);

    PodcastListResponse listPodcasts(PodcastFilterRequest request);

    PodcastReportResult exportReport(PodcastFilterRequest request);

    PodcastImportResponse uploadPodcasts(MultipartFile file);

}
