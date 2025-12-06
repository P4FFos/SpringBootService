package com.podcastservice.mapper;

import org.springframework.stereotype.Component;

import com.podcastservice.dto.podcast.PodcastDetailsDto;
import com.podcastservice.dto.podcast.PodcastSaveDto;
import com.podcastservice.entity.Podcast;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// map podcast DTO to entities
public class PodcastMapper {

    private final EpisodeMapper episodeMapper;

    public Podcast toEntity(PodcastSaveDto dto) {
        Podcast podcast = new Podcast();
        podcast.setId(null);
        podcast.setName(dto.getName());
        podcast.setHost(dto.getHost());
        return podcast;
    }

    public void updateEntity(Podcast podcast, PodcastSaveDto dto) {
        podcast.setName(dto.getName());
        podcast.setHost(dto.getHost());
    }

    public PodcastDetailsDto toDetails(Podcast podcast) {
        PodcastDetailsDto dto = new PodcastDetailsDto();
        dto.setId(podcast.getId());
        dto.setName(podcast.getName());
        dto.setHost(podcast.getHost());
        dto.setEpisodes(podcast.getEpisodes().stream().map(episodeMapper::toDetails).toList());
        return dto;
    }
}
