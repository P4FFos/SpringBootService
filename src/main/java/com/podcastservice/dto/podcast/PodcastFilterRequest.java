package com.podcastservice.dto.podcast;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PodcastFilterRequest {

    @JsonProperty("episodeId")
    private Integer episodeId;

    private String host;

    private String guest;

    private String tag;

    private LocalDate releaseDateFrom;

    private LocalDate releaseDateTo;

    @NotNull
    @Min(0)
    private Integer page = 0;

    @NotNull
    @Positive
    private Integer size = 10;
}
