package com.podcastservice.mapper;

import org.springframework.stereotype.Component;

import com.podcastservice.dto.episode.EpisodeDetailsDto;
import com.podcastservice.dto.episode.EpisodeSaveDto;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;

@Component
// map episode DTO to entities
public class EpisodeMapper {

    public Episode toEntity(EpisodeSaveDto dto, Podcast podcast) {
        Episode episode = new Episode();
        episode.setId(null);
        episode.setTitle(dto.getTitle());
        episode.setReleaseDate(dto.getReleaseDate());
        episode.setDuration(dto.getDuration());
        episode.setTags(dto.getTags());
        episode.setGuest(dto.getGuest());
        episode.setPodcast(podcast);
        return episode;
    }

    public void updateEntity(Episode episode, EpisodeSaveDto dto, Podcast podcast) {
        episode.setTitle(dto.getTitle());
        episode.setReleaseDate(dto.getReleaseDate());
        episode.setDuration(dto.getDuration());
        episode.setTags(dto.getTags());
        episode.setGuest(dto.getGuest());
        episode.setPodcast(podcast);
    }

    public EpisodeDetailsDto toDetails(Episode episode) {
        EpisodeDetailsDto dto = new EpisodeDetailsDto();
        dto.setId(episode.getId());
        dto.setTitle(episode.getTitle());
        dto.setReleaseDate(episode.getReleaseDate());
        dto.setDuration(episode.getDuration());
        dto.setTags(episode.getTags());
        dto.setGuest(episode.getGuest());
        if (episode.getPodcast() != null) {
            dto.setPodcastId(episode.getPodcast().getId());
            dto.setPodcastName(episode.getPodcast().getName());
        }
        return dto;
    }
}
