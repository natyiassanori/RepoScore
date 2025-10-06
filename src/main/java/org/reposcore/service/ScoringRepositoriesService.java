package org.reposcore.service;

import lombok.RequiredArgsConstructor;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.dto.ScoringRepositoriesResponse;
import org.reposcore.feign.client.GitHubApiClient;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.mapper.ScoringRepositoriesResponseMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringRepositoriesService {

    private final GitHubApiClient gitHubApiClient;

    private final GitHubPaginationService gitHubPaginationService;

    public ScoringRepositoriesResponse getRepositoriesWithScore(Date createdDate, String language) {
        String query = buildQueryParameter(createdDate, language);

        List<GitHubRepoItem> allRepositories = gitHubPaginationService.fetchAllRepositoriesFromGitHub(gitHubApiClient, query);

        List<ScoredRepository> repositores = allRepositories.stream().map(item -> ScoringRepositoriesResponseMapper.mapToScoredRepository(item, 0)).toList();

        return new ScoringRepositoriesResponse(repositores);
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
