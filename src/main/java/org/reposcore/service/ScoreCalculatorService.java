package org.reposcore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.mapper.ScoringRepositoriesResponseMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

            log.debug("Score for repository with id {} = {}", gitHubRepo.id(), score);

            scoredRepositories.add(ScoringRepositoriesResponseMapper.mapToScoredRepository(gitHubRepo, score));
        }

        return scoredRepositories;

    }


    private double getRepositoryScore(GitHubRepoItem gitHubRepo, int maxStars, int maxForks) {
        int stars = gitHubRepo.stargazersCount();
        int forks = gitHubRepo.forksCount();
        Date updatedAt = gitHubRepo.updatedAt();

        log.debug("Calculating score for repository with id {}, {} stars, {} forks and last updated at {}",
                gitHubRepo.id(), stars, forks, updatedAt);

        double starsScore = calculateStarsNormalized(stars, maxStars);
        double forksScore = calculateForksNormalized(forks, maxForks);
        double recencyScore = calculateRecencyNormalized(getHoursSinceLastUpdate(updatedAt));

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
        double score = WEIGHT_STARS * starsScore +
                WEIGHT_FORKS * forksScore +
                WEIGHT_RECENCY * recencyScore;

        BigDecimal roundedScore = new BigDecimal(score).setScale(2, RoundingMode.HALF_UP);

        return roundedScore.doubleValue();
    }

}
