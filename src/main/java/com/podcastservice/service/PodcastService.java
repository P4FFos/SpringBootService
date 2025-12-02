package com.podcastservice.service;

import com.podcastservice.dto.podcast.PodcastDetailsDto;
import com.podcastservice.dto.podcast.PodcastSaveDto;
import com.podcastservice.entity.Podcast;

public interface PodcastService {

    int createPodcast(PodcastSaveDto dto);

    PodcastDetailsDto getPodcast(int id);

    void updatePodcast(int id, PodcastSaveDto dto);

    void deletePodcast(int id);

    Podcast getOrThrow(int id);

}
