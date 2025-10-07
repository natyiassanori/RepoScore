package service;

import factory.GitHubRepoItemFactory;
import factory.ScoredRepositoryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.GitHubApiClient;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.service.GitHubPaginationService;
import org.reposcore.service.ScoreCalculatorService;
import org.reposcore.service.ScoringRepositoriesService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoringRepositoriesServiceTest {

    @Mock
    private GitHubApiClient gitHubApiClient;

    @Mock
    private ScoreCalculatorService scoreCalculatorService;

    @Mock
    private GitHubPaginationService gitHubPaginationService;

    @InjectMocks
    private ScoringRepositoriesService scoringRepositoriesService;

    private Date testDate;
    private GitHubRepoItem testRepoItem;
    private ScoredRepository testScoredRepo;

    @BeforeEach
    void setUp() {
        testDate = new Date(System.currentTimeMillis() - 86400000L); // 1 day ago
        testRepoItem = GitHubRepoItemFactory.createDefault();
        testScoredRepo = ScoredRepositoryFactory.createWithScore(0.85);
    }

    @Test
    void whenGettingRepositoriesWithScoreUsingWithLanguageAndDate_ShouldBuildCorrectQuery() {
        String language = "java";
        List<GitHubRepoItem> mockRepos = List.of(testRepoItem);
        List<ScoredRepository> mockScoredRepos = List.of(testScoredRepo);

        when(gitHubPaginationService.fetchAllRepositoriesFromGitHub(anyString()))
                .thenReturn(mockRepos);
        when(scoreCalculatorService.assignScoresForRepositories(mockRepos))
                .thenReturn(mockScoredRepos);

        List<ScoredRepository> result = scoringRepositoriesService.getRepositoriesWithScore(testDate, language);

        assertEquals(1, result.size());
        assertEquals(testScoredRepo, result.getFirst());

        verify(gitHubPaginationService).fetchAllRepositoriesFromGitHub(
                argThat(query -> query.contains("language:JAVA") && query.contains("created:>"))
        );
        verify(scoreCalculatorService).assignScoresForRepositories(mockRepos);
    }

    @Test
    void whenGettingRepositoriesWithScoreWithOnlyLanguage_ShouldBuildLanguageOnlyQuery() {
        String language = "python";
        List<GitHubRepoItem> mockRepos = List.of(testRepoItem);
        List<ScoredRepository> mockScoredRepos = List.of(testScoredRepo);

        when(gitHubPaginationService.fetchAllRepositoriesFromGitHub(anyString()))
                .thenReturn(mockRepos);
        when(scoreCalculatorService.assignScoresForRepositories(mockRepos))
                .thenReturn(mockScoredRepos);

        scoringRepositoriesService.getRepositoriesWithScore(null, language);

        verify(gitHubPaginationService).fetchAllRepositoriesFromGitHub(
                argThat(query -> query.startsWith("language:PYTHON") && !query.contains("created:>"))
        );
    }

    @Test
    void whenGettingRepositoriesWithScoreWithOnlyDate_ShouldBuildDateOnlyQuery() {
        List<GitHubRepoItem> mockRepos = List.of(testRepoItem);
        List<ScoredRepository> mockScoredRepos = List.of(testScoredRepo);

        when(gitHubPaginationService.fetchAllRepositoriesFromGitHub(anyString()))
                .thenReturn(mockRepos);
        when(scoreCalculatorService.assignScoresForRepositories(mockRepos))
                .thenReturn(mockScoredRepos);

        scoringRepositoriesService.getRepositoriesWithScore(testDate, null);

        verify(gitHubPaginationService).fetchAllRepositoriesFromGitHub(
                argThat(query -> query.startsWith("created:>") && !query.contains("language:"))
        );
    }

    @Test
    void whenGettingRepositoriesWithScoreWithNoFilters_ShouldUseDefaultQuery() {
        List<GitHubRepoItem> mockRepos = List.of(testRepoItem);
        List<ScoredRepository> mockScoredRepos = List.of(testScoredRepo);

        when(gitHubPaginationService.fetchAllRepositoriesFromGitHub(anyString()))
                .thenReturn(mockRepos);
        when(scoreCalculatorService.assignScoresForRepositories(mockRepos))
                .thenReturn(mockScoredRepos);

        scoringRepositoriesService.getRepositoriesWithScore(null, null);

        verify(gitHubPaginationService).fetchAllRepositoriesFromGitHub(
                argThat(query -> query.startsWith("Q") && !query.contains("language:") && !query.contains("created:>"))
        );
    }
}
