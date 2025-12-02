package com.podcastservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.podcastservice.entity.Podcast;

public interface PodcastRepository extends JpaRepository<Podcast, Integer>, JpaSpecificationExecutor<Podcast> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
