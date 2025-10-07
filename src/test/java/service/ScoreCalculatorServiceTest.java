package service;

import factory.GitHubRepoItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.service.ScoreCalculatorService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScoreCalculatorServiceTest {

    @InjectMocks
    private ScoreCalculatorService scoreCalculatorService;

    private GitHubRepoItem repoWithHighMetrics;
    private GitHubRepoItem repoWithLowMetrics;
    private GitHubRepoItem repoWithZeroMetrics;
    private GitHubRepoItem recentRepo;
    private GitHubRepoItem oldRepo;

    @BeforeEach
    void setUp() {
        Date now = new Date();
        Date oneHourAgo = new Date(now.getTime() - 3600000L); // 1 hour ago
        Date oneYearAgo = new Date(now.getTime() - 31536000000L); // 1 year ago

        repoWithHighMetrics = GitHubRepoItemFactory.createWithMetrics(1000, 500);
        repoWithLowMetrics = GitHubRepoItemFactory.createWithMetrics(10, 5);
        repoWithZeroMetrics = GitHubRepoItemFactory.createWithMetrics(0, 0);
        recentRepo = GitHubRepoItemFactory.createWithDates(now, oneHourAgo);
        oldRepo = GitHubRepoItemFactory.createWithDates(now, oneYearAgo);
    }

    @Test
    void whenAssigningScoresForRepositoriesWithOnlyOneRepository_ShouldCalculateScoreCorrectly() {
        List<GitHubRepoItem> repositories = List.of(repoWithHighMetrics);

        List<ScoredRepository> result = scoreCalculatorService.assignScoresForRepositories(repositories);

        assertEquals(1, result.size());
        
        ScoredRepository scoredRepo = result.getFirst();
        assertEquals("test-repo", scoredRepo.getName());
        assertEquals(1000, scoredRepo.getStars());
        assertEquals(500, scoredRepo.getForks());
        assertEquals(5.45, scoredRepo.getScore());
    }

    @Test
    void whenAssigningScoresForMultipleRepositories_ShouldCalculateScoresCorrectly() {
        List<GitHubRepoItem> repositories = List.of(repoWithHighMetrics, repoWithLowMetrics, repoWithZeroMetrics);

        List<ScoredRepository> result = scoreCalculatorService.assignScoresForRepositories(repositories);

        assertNotNull(result);
        assertEquals(3, result.size());

        ScoredRepository highMetricsScored = result.stream()
                .filter(r -> r.getStars() == 1000)
                .findFirst().orElseThrow();
        
        ScoredRepository lowMetricsScored = result.stream()
                .filter(r -> r.getStars() == 10)
                .findFirst().orElseThrow();
        
        ScoredRepository zeroMetricsScored = result.stream()
                .filter(r -> r.getStars() == 0)
                .findFirst().orElseThrow();

        assertEquals(5.45, highMetricsScored.getScore());
        assertEquals(1.88, lowMetricsScored.getScore());
        assertEquals(0.2, zeroMetricsScored.getScore());
    }

    @Test
    void whenAssigningScoresForEmptyList_ShouldReturnEmptyList() {
        List<GitHubRepoItem> repositories = List.of();

        List<ScoredRepository> result = scoreCalculatorService.assignScoresForRepositories(repositories);

        assertTrue(result.isEmpty());
    }

    @Test
    void whenAssigningScoresForRecentUpdatedRepository_ShouldHaveHigherRecencyScore() {
        List<GitHubRepoItem> repositories = List.of(recentRepo, oldRepo);

        List<ScoredRepository> result = scoreCalculatorService.assignScoresForRepositories(repositories);

        assertEquals(2, result.size());

        ScoredRepository recentScored = result.stream()
                .filter(r -> r.getUpdatedAt().equals(recentRepo.updatedAt()))
                .findFirst().orElseThrow();
        
        ScoredRepository oldScored = result.stream()
                .filter(r -> r.getUpdatedAt().equals(oldRepo.updatedAt()))
                .findFirst().orElseThrow();

        assertEquals(3.6, recentScored.getScore());
        assertEquals(3.42, oldScored.getScore());
    }

    @Test
    void whenAssigningScoresForRepositories_ShouldPreserveRepositoryData() {
        List<GitHubRepoItem> repositories = List.of(repoWithHighMetrics);

        List<ScoredRepository> result = scoreCalculatorService.assignScoresForRepositories(repositories);

        assertEquals(1, result.size());

        ScoredRepository scoredRepo = result.getFirst();
        assertEquals(repoWithHighMetrics.name(), scoredRepo.getName());
        assertEquals(repoWithHighMetrics.fullName(), scoredRepo.getFullName());
        assertEquals(repoWithHighMetrics.language(), scoredRepo.getLanguage());
        assertEquals(repoWithHighMetrics.stargazersCount(), scoredRepo.getStars());
        assertEquals(repoWithHighMetrics.forksCount(), scoredRepo.getForks());
        assertEquals(repoWithHighMetrics.description(), scoredRepo.getDescription());
        assertEquals(repoWithHighMetrics.htmlUrl(), scoredRepo.getHtmlUrl());
        assertEquals(repoWithHighMetrics.createdAt(), scoredRepo.getCreatedAt());
        assertEquals(repoWithHighMetrics.updatedAt(), scoredRepo.getUpdatedAt());
        assertEquals(5.45, scoredRepo.getScore());
    }
}
