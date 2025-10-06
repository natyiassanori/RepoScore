package org.reposcore.service;

import lombok.RequiredArgsConstructor;
import org.reposcore.feign.client.GitHubApiClient;
import org.reposcore.feign.client.dto.GitHubApiClientResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ScoringRepositoriesService {

    private final GitHubApiClient gitHubApiClient;

    public ResponseEntity<String> getRepositoriesWithScore(Date createdDate, String language) {
        String query = buildQueryParameter(createdDate, language);
        
        ResponseEntity<GitHubApiClientResponse> allRepositories = gitHubApiClient.searchRepositories(query, 100, 1);

        return ResponseEntity.ok("ok");
    }

    private String buildQueryParameter(Date createdDate, String language) {
        StringBuilder query = new StringBuilder();
        
        if (language != null && !language.isEmpty()) {
            query.append("language:").append(language.toUpperCase());
        }
        
        if (createdDate != null) {
            if (!query.isEmpty()) {
                query.append(" ");
            }
            query.append("created:>").append(createdDate.toInstant().toString().split("T")[0]);
        }

        //Q is used to return everything without any filter.
        if (query.isEmpty()) {
            query.append("Q");
        }
        
        return query.toString();
    }
}
