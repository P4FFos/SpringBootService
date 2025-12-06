package com.podcastservice.service.Impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.podcastservice.dto.episode.EpisodeUploadDto;
import com.podcastservice.dto.podcast.PodcastDetailsDto;
import com.podcastservice.dto.podcast.PodcastFilterRequest;
import com.podcastservice.dto.podcast.PodcastImportResponse;
import com.podcastservice.dto.podcast.PodcastListItemDto;
import com.podcastservice.dto.podcast.PodcastListResponse;
import com.podcastservice.dto.podcast.PodcastReportResult;
import com.podcastservice.dto.podcast.PodcastSaveDto;
import com.podcastservice.dto.podcast.PodcastUploadDto;
import com.podcastservice.entity.Episode;
import com.podcastservice.entity.Podcast;
import com.podcastservice.exception.DuplicateResourceException;
import com.podcastservice.exception.NotFoundException;
import com.podcastservice.mapper.PodcastMapper;
import com.podcastservice.repository.EpisodeRepository;
import com.podcastservice.repository.PodcastRepository;
import com.podcastservice.repository.specification.EpisodeSpecifications;
import com.podcastservice.repository.specification.PodcastSpecifications;
import com.podcastservice.service.PodcastService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PodcastServiceImpl implements PodcastService {

    private final PodcastRepository podcastRepository;
    private final PodcastMapper podcastMapper;
    private final EpisodeRepository episodeRepository;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Override
    public int createPodcast(PodcastSaveDto dto) {
        checkUniqueName(dto.getName());
        Podcast podcast = podcastMapper.toEntity(dto);
        attachEpisodes(podcast, dto.getEpisodesIds());
        return podcastRepository.save(podcast).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PodcastDetailsDto getPodcast(int id) {
        Podcast podcast = getOrThrow(id);
        return podcastMapper.toDetails(podcast);
    }

    @Override
    public void updatePodcast(int id, PodcastSaveDto dto) {
        Podcast podcast = getOrThrow(id);
        checkUniqueName(dto.getName(), id);
        podcastMapper.updateEntity(podcast, dto);
        attachEpisodes(podcast, dto.getEpisodesIds());
        podcastRepository.save(podcast);
    }

    @Override
    public void deletePodcast(int id) {
        Podcast podcast = getOrThrow(id);
        podcastRepository.delete(podcast);
    }

    @Override
    @Transactional(readOnly = true)
    public Podcast getOrThrow(int id) {
        return podcastRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Podcast with id %s not found (404)".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public PodcastListResponse listPodcasts(PodcastFilterRequest request) {
        List<Podcast> podcasts = podcastRepository.findAll(
                PodcastSpecifications.byFilter(request),
                Sort.by("id").ascending());

        List<PodcastListItemDto> items = podcasts.stream()
                .map(podcast -> new PodcastListItemDto(podcast.getId(), podcast.getName(), podcast.getHost(),
                        countEpisodesForPodcast(request, podcast.getId())))
                .toList();

        long totalEpisodes = episodeRepository.count(EpisodeSpecifications.byFilter(request));
        return new PodcastListResponse(items, totalEpisodes);
    }

    @Override
    @Transactional(readOnly = true)
    public PodcastReportResult exportReport(PodcastFilterRequest request) {
        List<Episode> episodes = episodeRepository.findAll(
                EpisodeSpecifications.byFilter(request),
                Sort.by(Sort.Order.asc("podcast.id"), Sort.Order.asc("releaseDate"), Sort.Order.asc("id")));

        byte[] content = buildCsvReport(episodes);
        return new PodcastReportResult(buildReportFileName(), content, "text/csv");
    }

    @Override
    public PodcastImportResponse uploadPodcasts(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or missing");
        }
        List<PodcastUploadDto> podcasts = parseUpload(file);
        int imported = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        for (PodcastUploadDto dto : podcasts) {
            try {
                validateUpload(dto);
                saveUploadedPodcast(dto);
                imported++;
            } catch (Exception ex) {
                failed++;
                errors.add("Podcast %s: %s".formatted(dto.getName(), ex.getMessage()));
            }
        }
        return new PodcastImportResponse(imported, failed, errors);
    }

    private void checkUniqueName(String name) {
        if (podcastRepository.existsByName(name)) {
            throw new DuplicateResourceException("Podcast with name %s already exists".formatted(name));
        }
    }

    private void checkUniqueName(String name, int excludeId) {
        if (podcastRepository.existsByNameAndIdNot(name, excludeId)) {
            throw new DuplicateResourceException("Podcast with name %s already exists".formatted(name));
        }
    }

    private void attachEpisodes(Podcast podcast, List<Integer> episodeIds) {
        if (episodeIds == null || episodeIds.isEmpty()) {
            podcast.setEpisodes(new ArrayList<>());
            return;
        }
        List<Episode> episodes = episodeRepository.findAllById(episodeIds);
        if (episodes.size() != episodeIds.size()) {
            throw new NotFoundException("Some episodes not found for ids " + episodeIds);
        }
        Integer podcastId = podcast.getId();
        for (Episode episode : episodes) {
            Podcast current = episode.getPodcast();
            if (current != null && (podcastId == null || !current.getId().equals(podcastId))) {
                throw new IllegalArgumentException(
                        "Episode %s already belongs to podcast %s".formatted(episode.getId(), current.getId()));
            }
        }
        podcast.setEpisodes(episodes);
    }

    private long countEpisodesForPodcast(PodcastFilterRequest request, Integer podcastId) {
        return episodeRepository.count(
                EpisodeSpecifications.byFilter(request).and(EpisodeSpecifications.forPodcast(podcastId)));
    }

    private byte[] buildCsvReport(List<Episode> episodes) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8),
                        CSVFormat.DEFAULT.withHeader("Podcast ID", "Podcast Name", "Host", "Episode ID", "Episode Title",
                                "Release Date", "Duration", "Guest", "Tags"))) {
            for (Episode episode : episodes) {
                Podcast podcast = episode.getPodcast();
                String tags = CollectionUtils.isEmpty(episode.getTags()) ? "" : String.join("|", episode.getTags());
                printer.printRecord(
                        podcast != null ? podcast.getId() : null,
                        podcast != null ? podcast.getName() : null,
                        podcast != null ? podcast.getHost() : null,
                        episode.getId(),
                        episode.getTitle(),
                        episode.getReleaseDate(),
                        episode.getDuration(),
                        episode.getGuest(),
                        tags);
            }
            printer.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build report", e);
        }
    }

    private String buildReportFileName() {
        return "podcasts_report.csv";
    }

    private List<PodcastUploadDto> parseUpload(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read provided file", e);
        }
    }

    private void saveUploadedPodcast(PodcastUploadDto dto) {
        checkUniqueName(dto.getName());
        Podcast savedPodcast = podcastRepository.save(new Podcast(dto.getName(), dto.getHost()));

        if (!CollectionUtils.isEmpty(dto.getEpisodes())) {
            List<Episode> episodes = dto.getEpisodes().stream()
                    .map(ep -> toEpisodeEntity(ep, savedPodcast))
                    .collect(Collectors.toList());
            try {
                episodeRepository.saveAll(episodes);
            } catch (DataIntegrityViolationException ex) {
                throw new IllegalArgumentException("Episode data violates constraints: " + ex.getMostSpecificCause().getMessage(), ex);
            }
        }
    }

    private void validateUpload(PodcastUploadDto dto) {
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(formatViolations(violations), violations);
        }
    }

    private String formatViolations(Iterable<ConstraintViolation<PodcastUploadDto>> violations) {
        return StreamSupport.stream(violations.spliterator(), false)
                .map(v -> "%s %s".formatted(v.getPropertyPath(), v.getMessage()))
                .collect(Collectors.joining("; "));
    }

    private Episode toEpisodeEntity(EpisodeUploadDto dto, Podcast podcast) {
        Episode episode = new Episode();
        episode.setTitle(dto.getTitle());
        episode.setReleaseDate(dto.getReleaseDate());
        episode.setDuration(dto.getDuration());
        episode.setTags(dto.getTags());
        episode.setGuest(dto.getGuest());
        episode.setPodcast(podcast);
        return episode;
    }

}
