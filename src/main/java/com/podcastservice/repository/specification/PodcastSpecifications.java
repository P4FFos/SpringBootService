package com.podcastservice.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.podcastservice.dto.podcast.PodcastFilterRequest;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class PodcastSpecifications {

    private PodcastSpecifications() {
    }

    // build dynamic predicates for podcast listing filtered by host or attributes
    public static Specification<Podcast> byFilter(PodcastFilterRequest filter) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getHost())) {
                predicates.add(cb.like(cb.lower(root.get("host")), wrapLike(filter.getHost())));
            }

            boolean needsEpisodeJoin = filter.getEpisodeId() != null
                    || filter.getReleaseDateFrom() != null
                    || filter.getReleaseDateTo() != null
                    || StringUtils.hasText(filter.getGuest())
                    || StringUtils.hasText(filter.getTag());

            Join<Podcast, Episode> episodesJoin = null;
            if (needsEpisodeJoin) {
                episodesJoin = root.join("episodes", JoinType.INNER);
            }

            if (episodesJoin != null) {
                if (filter.getEpisodeId() != null) {
                    predicates.add(cb.equal(episodesJoin.get("id"), filter.getEpisodeId()));
                }
                if (filter.getReleaseDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(episodesJoin.get("releaseDate"), filter.getReleaseDateFrom()));
                }
                if (filter.getReleaseDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(episodesJoin.get("releaseDate"), filter.getReleaseDateTo()));
                }
                if (StringUtils.hasText(filter.getGuest())) {
                    predicates.add(cb.like(cb.lower(episodesJoin.get("guest")), wrapLike(filter.getGuest())));
                }
                if (StringUtils.hasText(filter.getTag())) {
                    Join<Episode, String> tags = episodesJoin.join("tags", JoinType.INNER);
                    predicates.add(cb.equal(cb.lower(tags), filter.getTag().toLowerCase()));
                }
            }

            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    private static String wrapLike(String value) {
        return "%" + value.toLowerCase() + "%";
    }
}
