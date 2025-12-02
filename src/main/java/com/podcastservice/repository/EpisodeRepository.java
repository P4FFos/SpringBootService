package com.podcastservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.podcastservice.entity.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Integer>, JpaSpecificationExecutor<Episode> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Integer id);

    boolean existsByTitleAndPodcastId(String title, Integer podcastId);

    boolean existsByTitleAndIdNotAndPodcastId(String title, Integer id, Integer podcastId);
}
