package integration;

import factory.GitHubApiClientResponseFactory;
import factory.GitHubRepoItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.reposcore.feign.client.GitHubApiClient;
import org.reposcore.feign.client.dto.GitHubApiClientResponse;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = org.reposcore.RepoScoreApplication.class)
@AutoConfigureMockMvc
class ScoringRepositoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubApiClient gitHubApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGettingScoredRepositoriesWithoutQueryParameters_ThenShouldReturnAllRepositories() throws Exception {
        List<GitHubRepoItem> mockRepositories = createPythonRepositories();
        when(gitHubApiClient.searchRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(createGitHubApiResponseWithRepositories(mockRepositories));

        mockMvc.perform(get("/scoring")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.repositories").isArray())
                .andExpect(jsonPath("$.repositories").isNotEmpty())
                .andExpect(jsonPath("$.repositories.length()").value(2));

        verify(gitHubApiClient).searchRepositories("Q", 100, 1);
    }
    @Test
    void whenGettingScoredRepositoriesWithQueryParameters_ThenShouldReturnFilteredRepositories() throws Exception {
        String language = "python";
        List<GitHubRepoItem> mockRepositories = createPythonRepositories();
        
        when(gitHubApiClient.searchRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(createGitHubApiResponseWithRepositories(mockRepositories));

        mockMvc.perform(get("/scoring")
                        .param("language", language)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.repositories").isArray())
                .andExpect(jsonPath("$.repositories").isNotEmpty())
                .andExpect(jsonPath("$.repositories.length()").value(2));

        verify(gitHubApiClient).searchRepositories("language:PYTHON", 100, 1);
    }

    private List<GitHubRepoItem> createPythonRepositories() {
        return GitHubRepoItemFactory.createPythonRepositories();
    }

    private ResponseEntity<GitHubApiClientResponse> createGitHubApiResponseWithRepositories(List<GitHubRepoItem> repositories) {
        GitHubApiClientResponse response = GitHubApiClientResponseFactory.createWithRepositories(repositories);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
    }

}
