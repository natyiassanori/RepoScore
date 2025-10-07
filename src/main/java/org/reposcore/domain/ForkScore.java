package org.reposcore.domain;

import org.reposcore.feign.client.dto.GitHubRepoItem;

public class ForkScore implements Score {

    private static final double weight = 0.4;

    @Override
    public double calculate(GitHubRepoItem repository) {
        return Math.log(1 + repository.forksCount()) * weight;
    }
}
