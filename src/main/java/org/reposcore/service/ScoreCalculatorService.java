package org.reposcore.service;

import lombok.RequiredArgsConstructor;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.mapper.ScoringRepositoriesResponseMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreCalculatorService {

    //used to calculate exponential decay of recency
    private static final double ALPHA = 0.1;

    private static final double WEIGHT_STARS = 0.4;
    private static final double WEIGHT_FORKS = 0.4;
    private static final double WEIGHT_RECENCY = 0.2;


    public List<ScoredRepository> assignScoresForRepositories(List<GitHubRepoItem> repositories) {
        int maxStars = repositories.stream().mapToInt(GitHubRepoItem::stargazersCount).max().orElse(0);
        int maxForks = repositories.stream().mapToInt(GitHubRepoItem::forksCount).max().orElse(0);

        List<ScoredRepository> scoredRepositories = new ArrayList<>();

        for(GitHubRepoItem gitHubRepo : repositories) {

            double score = getRepositoryScore(gitHubRepo, maxStars, maxForks);

            scoredRepositories.add(ScoringRepositoriesResponseMapper.mapToScoredRepository(gitHubRepo, score));
        }

        return scoredRepositories;

    }


    private double getRepositoryScore(GitHubRepoItem gitHubRepo, int maxStars, int maxForks) {
        double starsScore = calculateStarsNormalized(gitHubRepo.stargazersCount(), maxStars);
        double forksScore = calculateForksNormalized(gitHubRepo.forksCount(), maxForks);
        double recencyScore = calculateRecencyNormalized(getHoursSinceLastUpdate(gitHubRepo.updatedAt()));

        return calculateTotalScore(starsScore, forksScore, recencyScore);
    }

    private long getHoursSinceLastUpdate(Date updatedAt) {
        Instant lastUpdate = updatedAt.toInstant();
        Instant now = Instant.now();

        return Duration.between(lastUpdate, now).toHours();
    }

    private double calculateStarsNormalized(int numberOfStars, int maxStars){
        return maxStars > 0 ? Math.log(1 + numberOfStars) / Math.log(1 + maxStars) : 0;
    }

    private double calculateForksNormalized(int numberOfForks, int maxForks){
        return maxForks > 0 ? Math.log(1 + numberOfForks) / Math.log(1 + maxForks) : 0;
    }

    private double calculateRecencyNormalized(long hoursSinceLastUpdate){
        return Math.exp(-ALPHA * hoursSinceLastUpdate);
    }


    private double calculateTotalScore(double starsScore, double forksScore, double recencyScore) {
        return WEIGHT_STARS * starsScore +
                WEIGHT_FORKS * forksScore +
                WEIGHT_RECENCY * recencyScore;
    }

}
