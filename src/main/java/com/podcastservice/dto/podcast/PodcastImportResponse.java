package com.podcastservice.dto.podcast;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// summary after upload
public class PodcastImportResponse {

    private int imported;

    private int failed;

    private List<String> errors = new ArrayList<>();

    public void setErrors(List<String> errors) {
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }
}
