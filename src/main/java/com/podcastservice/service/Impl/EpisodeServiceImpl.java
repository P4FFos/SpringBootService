package com.podcastservice.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.podcastservice.dto.episode.EpisodeDetailsDto;
import com.podcastservice.dto.episode.EpisodeSaveDto;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;
import com.podcastservice.exception.DuplicateResourceException;
import com.podcastservice.exception.NotFoundException;
import com.podcastservice.mapper.EpisodeMapper;
import com.podcastservice.repository.EpisodeRepository;
import com.podcastservice.service.EpisodeService;
import com.podcastservice.service.PodcastService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeMapper episodeMapper;
    private final PodcastService podcastService;

    @Override
    public List<EpisodeDetailsDto> getEpisodes() {
        return episodeRepository.findAll()
                .stream()
                .map(episodeMapper::toDetails)
                .collect(Collectors.toList());
    }

    @Override
    public int createEpisode(EpisodeSaveDto dto) {
        Podcast podcast = podcastService.getOrThrow(dto.getPodcastId());
        checkUniqueTitle(dto.getTitle(), podcast.getId(), null);
        Episode episode = episodeMapper.toEntity(dto, podcast);
        return episodeRepository.save(episode).getId();
    }

    @Override
    public void updateEpisode(int id, EpisodeSaveDto dto) {
        Episode episode = getOrThrow(id);
        Podcast podcast = podcastService.getOrThrow(dto.getPodcastId());
        checkUniqueTitle(dto.getTitle(), podcast.getId(), id);
        episodeMapper.updateEntity(episode, dto, podcast);
        episodeRepository.save(episode);
    }

    @Override
    public void deleteEpisode(int id) {
        Episode episode = getOrThrow(id);
        episodeRepository.delete(episode);
    }

    @Override
    public Episode getOrThrow(int id) {
        return episodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Episode with id %s not found".formatted(id)));
    }

    private void checkUniqueTitle(String title, Integer podcastId, Integer excludeId) {
        boolean exists;
        if (excludeId == null) {
            exists = episodeRepository.existsByTitleAndPodcastId(title, podcastId);
        } else {
            exists = episodeRepository.existsByTitleAndIdNotAndPodcastId(title, excludeId, podcastId);
        }
        if (exists) {
            throw new DuplicateResourceException("Episode with title %s already exists for podcast %s".formatted(title, podcastId));
        }
    }
}
