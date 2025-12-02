package com.podcastservice.dto.podcast;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PodcastSaveDto {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "host is required")
    private String host;

    @NotNull(message = "episodesIds cannot be null")
    private List<Integer> episodesIds = new ArrayList<>();

    public PodcastSaveDto(String name, String host, List<Integer> episodesIds) {
        this.name = name;
        this.host = host;
        this.episodesIds = episodesIds != null ? new ArrayList<>(episodesIds) : new ArrayList<>();
    }

    public void setEpisodesIds(List<Integer> episodesIds) {
        this.episodesIds = episodesIds != null ? new ArrayList<>(episodesIds) : new ArrayList<>();
    }
}
