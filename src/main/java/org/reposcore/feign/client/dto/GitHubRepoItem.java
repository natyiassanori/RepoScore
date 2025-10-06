package org.reposcore.feign.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record GitHubRepoItem(
    long id,
    String name,
    @JsonProperty("full_name") String fullName,
    @JsonProperty("html_url") String htmlUrl,
    String description,
    @JsonProperty("created_at") Date createdAt,
    @JsonProperty("updated_at") Date updatedAt,
    String language,
    @JsonProperty("stargazers_count") int stargazersCount,
    @JsonProperty("forks_count") int forksCount
) {}

