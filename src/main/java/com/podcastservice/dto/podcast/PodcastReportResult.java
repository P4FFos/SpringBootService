package com.podcastservice.dto.podcast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
// file content and metadata for CSV report
public class PodcastReportResult {

    private String fileName;

    private byte[] content;

    private String contentType;
}
