package org.reposcore.domain;

import org.reposcore.feign.client.dto.GitHubRepoItem;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class RecencyScore implements Score {

    //used to calculate exponential decay of recency
    private static final double ALPHA = 0.1;

    private static final double weight = 0.2;

    @Override
    public double calculate(GitHubRepoItem repository) {

        long hoursSinceLastUpdate = getHoursSinceLastUpdate(repository.updatedAt());

        return Math.exp(-ALPHA * hoursSinceLastUpdate)*weight;
    }

    private long getHoursSinceLastUpdate(Date updatedAt) {
        Instant lastUpdate = updatedAt.toInstant();
        Instant now = Instant.now();

        return Duration.between(lastUpdate, now).toHours();
    }
}
