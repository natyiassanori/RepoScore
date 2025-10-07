package org.reposcore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reposcore.domain.ForkScore;
import org.reposcore.domain.RecencyScore;
import org.reposcore.domain.Score;
import org.reposcore.domain.StarScore;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.mapper.ScoringRepositoriesResponseMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreCalculatorService {

    private final List<Score> scores = List.of(new StarScore(), new ForkScore(), new RecencyScore());


    public List<ScoredRepository> assignScoresForRepositories(List<GitHubRepoItem> repositories) {
        List<ScoredRepository> scoredRepositories = new ArrayList<>();

        for(GitHubRepoItem gitHubRepo : repositories) {

            log.debug("Calculating score for repository with id {}, {} stars, {} forks and last updated at {}",
                    gitHubRepo.id(), gitHubRepo.stargazersCount(), gitHubRepo.forksCount(), gitHubRepo.updatedAt());

            double score = getRepositoryScore(gitHubRepo);

            log.debug("Score for repository with id {} = {}", gitHubRepo.id(), score);

            scoredRepositories.add(ScoringRepositoriesResponseMapper.mapToScoredRepository(gitHubRepo, score));
        }

        return scoredRepositories;

    }

    private double getRepositoryScore(GitHubRepoItem gitHubRepo) {
        double rawScore = scores.stream()
                .mapToDouble(score -> score.calculate(gitHubRepo))
                .sum();

        //return score with 2 decimal cases only
        return new BigDecimal(rawScore)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
