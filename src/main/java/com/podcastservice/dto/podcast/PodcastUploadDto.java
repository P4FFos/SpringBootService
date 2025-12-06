package com.podcastservice.dto.podcast;

import java.util.ArrayList;
import java.util.List;

import com.podcastservice.dto.episode.EpisodeUploadDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// podcast with nested episodes
public class PodcastUploadDto {

    @NotBlank
    private String name;

    @NotBlank
    private String host;

    @Valid
    private List<EpisodeUploadDto> episodes = new ArrayList<>();

    public void setEpisodes(List<EpisodeUploadDto> episodes) {
        this.episodes = episodes != null ? new ArrayList<>(episodes) : new ArrayList<>();
    }
}
