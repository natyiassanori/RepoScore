package mapper;

import factory.GitHubRepoItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reposcore.dto.ScoredRepository;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.mapper.ScoringRepositoriesResponseMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringRepositoriesResponseMapperTest {

    private GitHubRepoItem testGitHubRepo;

    @BeforeEach
    void setUp() {
        testGitHubRepo = GitHubRepoItemFactory.createDefault();
    }

    @Test
    void whenMappingToScoredRepositoryWithValidData_ShouldMapAllFields() {
        double testScore = 0.85;

        ScoredRepository result = ScoringRepositoriesResponseMapper.mapToScoredRepository(
                testGitHubRepo, testScore);

        assertEquals(testScore, result.getScore());
        assertEquals(testGitHubRepo.name(), result.getName());
        assertEquals(testGitHubRepo.fullName(), result.getFullName());
        assertEquals(testGitHubRepo.language(), result.getLanguage());
        assertEquals(testGitHubRepo.stargazersCount(), result.getStars());
        assertEquals(testGitHubRepo.forksCount(), result.getForks());
        assertEquals(testGitHubRepo.description(), result.getDescription());
        assertEquals(testGitHubRepo.htmlUrl(), result.getHtmlUrl());
        assertEquals(testGitHubRepo.createdAt(), result.getCreatedAt());
        assertEquals(testGitHubRepo.updatedAt(), result.getUpdatedAt());
        assertEquals(testScore, result.getScore());
    }
}
