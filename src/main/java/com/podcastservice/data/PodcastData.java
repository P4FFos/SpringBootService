package com.podcastservice.data;

import java.util.ArrayList;
import java.util.List;

import com.podcastservice.entity.Episode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PodcastData {

    private Integer id;

    private String name;

    private String host;

    private List<Episode> episodes = new ArrayList<>();
}
