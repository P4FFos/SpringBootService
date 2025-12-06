package com.podcastservice.dto.podcast;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// filter for list/report endpoints
public class PodcastFilterRequest {

    @JsonProperty("episodeId")
    private Integer episodeId;

    private String host;

    private String guest;

    private String tag;

    private LocalDate releaseDateFrom;

    private LocalDate releaseDateTo;
}
