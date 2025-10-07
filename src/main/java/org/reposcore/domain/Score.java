package org.reposcore.domain;

import org.reposcore.feign.client.dto.GitHubRepoItem;

public interface Score {
    double calculate(GitHubRepoItem repo);
}
