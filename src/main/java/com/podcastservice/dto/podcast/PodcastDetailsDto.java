package com.podcastservice.dto.podcast;

import java.util.ArrayList;
import java.util.List;

import com.podcastservice.dto.episode.EpisodeDetailsDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PodcastDetailsDto {

    private Integer id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "host is required")
    private String host;

    private List<EpisodeDetailsDto> episodes = new ArrayList<>();

    public void setEpisodes(List<EpisodeDetailsDto> episodes) {
        this.episodes = episodes != null ? new ArrayList<>(episodes) : new ArrayList<>();
    }

}
