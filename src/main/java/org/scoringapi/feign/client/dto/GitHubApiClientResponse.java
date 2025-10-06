package org.scoringapi.feign.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GitHubApiClientResponse(
    @JsonProperty("total_count") int totalCount,
    @JsonProperty("incomplete_results") boolean incompleteResults,
    List<GitHubRepoItem> items
) {}
