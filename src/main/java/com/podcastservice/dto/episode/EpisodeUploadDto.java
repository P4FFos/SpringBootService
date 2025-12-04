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
public class EpisodeUploadDto {

    @NotBlank
    private String title;

    @NotNull
    @PastOrPresent
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Integer duration;

    @NotEmpty
    private List<@NotBlank String> tags = new ArrayList<>();

    @NotBlank
    private String guest;

    public void setTags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }
}
