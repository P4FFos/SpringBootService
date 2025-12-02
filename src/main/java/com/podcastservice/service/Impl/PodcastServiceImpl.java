package com.podcastservice.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.podcastservice.dto.podcast.PodcastDetailsDto;
import com.podcastservice.dto.podcast.PodcastSaveDto;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;
import com.podcastservice.exception.DuplicateResourceException;
import com.podcastservice.exception.NotFoundException;
import com.podcastservice.mapper.PodcastMapper;
import com.podcastservice.repository.EpisodeRepository;
import com.podcastservice.repository.PodcastRepository;
import com.podcastservice.service.PodcastService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PodcastServiceImpl implements PodcastService {

    private final PodcastRepository podcastRepository;
    private final PodcastMapper podcastMapper;
    private final EpisodeRepository episodeRepository;

    @Override
    public int createPodcast(PodcastSaveDto dto) {
        checkUniqueName(dto.getName());
        Podcast podcast = podcastMapper.toEntity(dto);
        attachEpisodes(podcast, dto.getEpisodesIds());
        return podcastRepository.save(podcast).getId();
    }

    @Override
    public PodcastDetailsDto getPodcast(int id) {
        Podcast podcast = getOrThrow(id);
        return podcastMapper.toDetails(podcast);
    }

    @Override
    public void updatePodcast(int id, PodcastSaveDto dto) {
        Podcast podcast = getOrThrow(id);
        checkUniqueName(dto.getName(), id);
        podcastMapper.updateEntity(podcast, dto);
        attachEpisodes(podcast, dto.getEpisodesIds());
        podcastRepository.save(podcast);
    }

    @Override
    public void deletePodcast(int id) {
        Podcast podcast = getOrThrow(id);
        podcastRepository.delete(podcast);
    }

    @Override
    public Podcast getOrThrow(int id) {
        return podcastRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Podcast with id %s not found (404)".formatted(id)));
    }

    private void checkUniqueName(String name) {
        if (podcastRepository.existsByName(name)) {
            throw new DuplicateResourceException("Podcast with name %s already exists".formatted(name));
        }
    }

    private void checkUniqueName(String name, int excludeId) {
        if (podcastRepository.existsByNameAndIdNot(name, excludeId)) {
            throw new DuplicateResourceException("Podcast with name %s already exists".formatted(name));
        }
    }

    private void attachEpisodes(Podcast podcast, List<Integer> episodeIds) {
        if (episodeIds == null || episodeIds.isEmpty()) {
            podcast.setEpisodes(new ArrayList<>());
            return;
        }
        List<Episode> episodes = episodeRepository.findAllById(episodeIds);
        if (episodes.size() != episodeIds.size()) {
            throw new NotFoundException("Some episodes not found for ids " + episodeIds);
        }
        episodes.forEach(e -> e.setPodcast(podcast));
        podcast.setEpisodes(episodes);
    }
}
