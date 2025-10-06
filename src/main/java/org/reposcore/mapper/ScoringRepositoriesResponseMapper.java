package org.reposcore.mapper;

import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.dto.GitHubRepoItem;

public class ScoringRepositoriesResponseMapper {

    public static ScoredRepository mapToScoredRepository(GitHubRepoItem gitHubRepo, double score) {

        return ScoredRepository.builder().score(score)
                        .forks(gitHubRepo.forksCount())
                        .stars(gitHubRepo.stargazersCount())
                        .name(gitHubRepo.name())
                        .description(gitHubRepo.description())
                        .htmlUrl(gitHubRepo.htmlUrl())
                        .fullName(gitHubRepo.fullName())
                        .language(gitHubRepo.language())
                        .createdAt(gitHubRepo.createdAt())
                        .updatedAt(gitHubRepo.updatedAt()).build();
    }
}
