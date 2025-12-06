package com.podcastservice.dto.episode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// create or update an episode
public class EpisodeSaveDto {

    @NotBlank(message = "title is required")
    private String title;

    @NotNull(message = "releaseDate is required")
    @PastOrPresent
    private LocalDate releaseDate;

    @NotNull(message = "duration is required")
    @Positive(message = "duration must be positive")
    private Integer duration;

    @NotEmpty(message = "tags are required")
    private List<@NotBlank(message = "tag cannot be blank") String> tags = new ArrayList<>();

    @NotBlank(message = "guest is required")
    private String guest;

    @NotNull(message = "podcastId is required")
    private Integer podcastId;

    public EpisodeSaveDto(String title, LocalDate releaseDate, Integer duration, List<String> tags, String guest, Integer podcastId) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.guest = guest;
        this.podcastId = podcastId;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }
}
