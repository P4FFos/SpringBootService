package com.podcastservice.service;

import java.util.List;

import com.podcastservice.dto.episode.EpisodeDetailsDto;
import com.podcastservice.dto.episode.EpisodeSaveDto;
import com.podcastservice.entity.Episode;

// business logic for episodes
public interface EpisodeService {

    List<EpisodeDetailsDto> getEpisodes();

    int createEpisode(EpisodeSaveDto dto);

    void updateEpisode(int id, EpisodeSaveDto dto);

    void deleteEpisode(int id);

    Episode getOrThrow(int id);
}
