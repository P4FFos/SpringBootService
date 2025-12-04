package com.podcastservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.podcastservice.entity.Podcast;

@Repository
public interface PodcastRepository extends JpaRepository<Podcast, Integer>, JpaSpecificationExecutor<Podcast> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
