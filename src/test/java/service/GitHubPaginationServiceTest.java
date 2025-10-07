package service;

import factory.GitHubApiClientResponseFactory;
import factory.GitHubRepoItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reposcore.exception.GitHubApiException;
import org.reposcore.feign.client.GitHubApiClient;
import org.reposcore.feign.client.dto.GitHubApiClientResponse;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.reposcore.service.GitHubPaginationService;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubPaginationServiceTest {

    @Mock
    private GitHubApiClient gitHubApiClient;

    @InjectMocks
    private GitHubPaginationService gitHubPaginationService;

    private static final String ACCEPT_HEADER = "application/vnd.github+json";

    private GitHubRepoItem testRepo1;
    private GitHubRepoItem testRepo2;
    private GitHubRepoItem testRepo3;

    @BeforeEach
    void setUp() {
        testRepo1 = GitHubRepoItemFactory.createWithId(1L);
        testRepo2 = GitHubRepoItemFactory.createWithId(2L);
        testRepo3 = GitHubRepoItemFactory.createWithId(3L);
    }

    @Test
    void whenFetchingAllRepositoriesFromGitHubWithSinglePage_ShouldReturnAllRepositories() {
        GitHubApiClientResponse response = GitHubApiClientResponseFactory.createWithRepositories(
                List.of(testRepo1, testRepo2)
        );

        ResponseEntity<GitHubApiClientResponse> responseEntity = ResponseEntity.ok()
                .header("Link", "")
                .body(response);

        when(gitHubApiClient.searchRepositories(ACCEPT_HEADER, "test query", 100, 1))
                .thenReturn(responseEntity);

        List<GitHubRepoItem> result = gitHubPaginationService.fetchAllRepositoriesFromGitHub("test query");

        assertEquals(2, result.size());
        assertTrue(result.contains(testRepo1));
        assertTrue(result.contains(testRepo2));
        verify(gitHubApiClient).searchRepositories(ACCEPT_HEADER, "test query", 100, 1);
    }

    @Test
    void whenFetchingAllRepositoriesFromGitHubWithMultiplePages_ShouldFetchAllPages() {
        GitHubApiClientResponse firstPageResponse = new GitHubApiClientResponse(
                3, false, List.of(testRepo1)
        );

        GitHubApiClientResponse secondPageResponse = new GitHubApiClientResponse(
                3, false, List.of(testRepo2)
        );

        GitHubApiClientResponse thirdPageResponse = new GitHubApiClientResponse(
                3, false, List.of(testRepo3)
        );

        ResponseEntity<GitHubApiClientResponse> firstPageEntity = ResponseEntity.ok()
                .header("Link", "<https://api.github.com/search/repositories?page=2>; rel=\"next\"")
                .body(firstPageResponse);

        ResponseEntity<GitHubApiClientResponse> secondPageEntity = ResponseEntity.ok()
                .header("Link", "<https://api.github.com/search/repositories?page=3>; rel=\"next\"")
                .body(secondPageResponse);

        ResponseEntity<GitHubApiClientResponse> thirdPageEntity = ResponseEntity.ok()
                .header("Link", "")
                .body(thirdPageResponse);

        when(gitHubApiClient.searchRepositories(ACCEPT_HEADER, "test query", 100, 1))
                .thenReturn(firstPageEntity);
        when(gitHubApiClient.searchRepositories(ACCEPT_HEADER, "test query", 100, 2))
                .thenReturn(secondPageEntity);
        when(gitHubApiClient.searchRepositories(ACCEPT_HEADER, "test query", 100, 3))
                .thenReturn(thirdPageEntity);

        List<GitHubRepoItem> result = gitHubPaginationService.fetchAllRepositoriesFromGitHub("test query");

        assertEquals(3, result.size());
        assertTrue(result.contains(testRepo1));
        assertTrue(result.contains(testRepo2));
        assertTrue(result.contains(testRepo3));

        verify(gitHubApiClient).searchRepositories(ACCEPT_HEADER, "test query", 100, 1);
        verify(gitHubApiClient).searchRepositories(ACCEPT_HEADER, "test query", 100, 2);
        verify(gitHubApiClient).searchRepositories(ACCEPT_HEADER, "test query", 100, 3);
    }

    @Test
    void whenFetchingAllRepositoriesFromGitHubWithEmptyResponse_ShouldReturnEmptyList() {
        GitHubApiClientResponse emptyResponse = GitHubApiClientResponseFactory.createEmpty();

        ResponseEntity<GitHubApiClientResponse> responseEntity = ResponseEntity.ok()
                .header("Link", "")
                .body(emptyResponse);

        when(gitHubApiClient.searchRepositories(ACCEPT_HEADER, "test query", 100, 1))
                .thenReturn(responseEntity);

        List<GitHubRepoItem> result = gitHubPaginationService.fetchAllRepositoriesFromGitHub("test query");

        assertTrue(result.isEmpty());

        verify(gitHubApiClient).searchRepositories(ACCEPT_HEADER,"test query", 100, 1);
    }

    @Test
    void whenFetchingAllRepositoriesFromGitHubWithExceptionOnSecondPage_ShouldThrowGitHubApiException() {
        GitHubApiClientResponse firstPageResponse = new GitHubApiClientResponse(
                2, false, List.of(testRepo1, testRepo2)
        );

        ResponseEntity<GitHubApiClientResponse> firstPageEntity = ResponseEntity.ok()
                .header("Link", "<https://api.github.com/search/repositories?page=2>; rel=\"next\"")
                .body(firstPageResponse);

        when(gitHubApiClient.searchRepositories(ACCEPT_HEADER,"test query", 100, 1))
                .thenReturn(firstPageEntity);
        when(gitHubApiClient.searchRepositories(ACCEPT_HEADER,"test query", 100, 2))
                .thenThrow(new RuntimeException("API Error"));

        assertThrows(GitHubApiException.class, () -> gitHubPaginationService.fetchAllRepositoriesFromGitHub("test query"));

        verify(gitHubApiClient).searchRepositories(ACCEPT_HEADER,"test query", 100, 1);
        verify(gitHubApiClient).searchRepositories(ACCEPT_HEADER,"test query", 100, 2);
    }
}
