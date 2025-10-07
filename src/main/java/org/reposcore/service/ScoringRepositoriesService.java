package org.reposcore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.GitHubApiClient;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringRepositoriesService {

    private final GitHubPaginationService gitHubPaginationService;

    private final ScoreCalculatorService scoreCalculatorService;

    public List<ScoredRepository> getRepositoriesWithScore(Date createdDate, String language) {
        String query = buildQueryParameter(createdDate, language);

        log.info("Calculating score for repositories that matches the query: {}", query);

        List<GitHubRepoItem> allRepositories = gitHubPaginationService.fetchAllRepositoriesFromGitHub(query);

        List<ScoredRepository> scoredRepositories = scoreCalculatorService.assignScoresForRepositories(allRepositories);

        log.info("Score assigned to {} repositories", scoredRepositories.size());

        return scoredRepositories;
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
