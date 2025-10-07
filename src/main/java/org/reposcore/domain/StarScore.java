package org.reposcore.domain;

import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.springframework.stereotype.Component;

@Component
public class StarScore implements Score {

    private static final double weight = 0.4;

    @Override
    public double calculate(GitHubRepoItem repository) {
        return Math.log(1 + repository.stargazersCount()) * weight;
    }
}
