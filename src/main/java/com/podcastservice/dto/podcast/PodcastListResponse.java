package com.podcastservice.dto.podcast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// podcast list and total episode count
public class PodcastListResponse {

    private List<PodcastListItemDto> list = new ArrayList<>();

    private long episodes;

    public PodcastListResponse(List<PodcastListItemDto> list, long episodes) {
        this.list = list != null ? new ArrayList<>(list) : new ArrayList<>();
        this.episodes = episodes;
    }
}
