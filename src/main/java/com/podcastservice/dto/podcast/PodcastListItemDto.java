package com.podcastservice.dto.podcast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PodcastListItemDto {

    private Integer id;

    private String name;

    private String host;

    private long episodesCount;
}
