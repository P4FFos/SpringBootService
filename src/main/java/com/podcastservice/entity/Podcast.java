package com.podcastservice.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "podcasts", indexes = {
        @Index(name = "idx_podcast_host", columnList = "host")
})
public class Podcast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String host;

    @OneToMany(mappedBy = "podcast")
    private final List<Episode> episodes = new ArrayList<>();

    public Podcast(String name, String host) {
        this.name = name;
        this.host = host;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes.clear();
        if (episodes != null) {
            episodes.forEach(this::addEpisode);
        }
    }

    public void addEpisode(Episode episode) {
        if (episode != null) {
            episodes.add(episode);
            episode.setPodcast(this);
        }
    }

    @Override
    public String toString() {
        return "Podcast{"
                + "name='" + name + '\''
                + ", host='" + host + '\''
                + ", episodes=" + episodes.size()
                + '}';
    }
}
