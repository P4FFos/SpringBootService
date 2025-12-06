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
// full episode data
public class EpisodeDetailsDto {

    private Integer id;

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

    private Integer podcastId;

    private String podcastName;

    public void setTags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }
}
