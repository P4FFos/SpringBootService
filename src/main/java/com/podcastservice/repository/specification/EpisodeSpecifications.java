package com.podcastservice.repository.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.podcastservice.dto.podcast.PodcastFilterRequest;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class EpisodeSpecifications {

    private EpisodeSpecifications() {
    }

    // build dynamic predicates for episode filtering
    public static Specification<Episode> byFilter(PodcastFilterRequest filter) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            Join<Episode, Podcast> podcastJoin = root.join("podcast", JoinType.INNER);

            if (filter.getEpisodeId() != null) {
                predicates.add(cb.equal(root.get("id"), filter.getEpisodeId()));
            }

            if (filter.getReleaseDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("releaseDate"), filter.getReleaseDateFrom()));
            }

            if (filter.getReleaseDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"), filter.getReleaseDateTo()));
            }

            if (StringUtils.hasText(filter.getGuest())) {
                predicates.add(cb.like(cb.lower(root.get("guest")), wrapLike(filter.getGuest())));
            }

            if (StringUtils.hasText(filter.getHost())) {
                predicates.add(cb.like(cb.lower(podcastJoin.get("host")), wrapLike(filter.getHost())));
            }

            if (StringUtils.hasText(filter.getTag())) {
                Join<Episode, String> tags = root.join("tags", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(tags), filter.getTag().toLowerCase()));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    public static Specification<Episode> forPodcasts(Collection<Integer> podcastIds) {
        return (root, query, cb) -> root.get("podcast").get("id").in(podcastIds);
    }

    public static Specification<Episode> forPodcast(Integer podcastId) {
        return (root, query, cb) -> cb.equal(root.get("podcast").get("id"), podcastId);
    }

    private static String wrapLike(String value) {
        return "%" + value.toLowerCase() + "%";
    }
}
