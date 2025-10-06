package e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = org.reposcore.RepoScoreApplication.class)
@AutoConfigureMockMvc
class RepoScoreE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGettingScoredRepositoriesWithLanguageAndCreatedDate_ThenShouldReturnFilteredRepositories() throws Exception {
        mockMvc.perform(get("/scoring")
                        .param("language", "python")
                        .param("createdDate", "2024-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.repositories").isArray())
                .andExpect(jsonPath("$.repositories").isNotEmpty())
                .andExpect(jsonPath("$.repositories[0].name").exists())
                .andExpect(jsonPath("$.repositories[0].fullName").exists())
                .andExpect(jsonPath("$.repositories[0].language").exists())
                .andExpect(jsonPath("$.repositories[0].stars").exists())
                .andExpect(jsonPath("$.repositories[0].forks").exists())
                .andExpect(jsonPath("$.repositories[0].score").exists())
                .andExpect(jsonPath("$.repositories[0].score").isNumber())
                .andExpect(jsonPath("$.repositories[0].score").value(greaterThanOrEqualTo(0.0)))
                .andExpect(jsonPath("$.repositories[0].score").value(lessThanOrEqualTo(1.0)))
                .andExpect(jsonPath("$.repositories[*].language").value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo("Python"))))
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    System.out.println("E2E Test with Both Filters Response: " + response);
                });
    }
}
