package factory;

import org.reposcore.feign.client.dto.GitHubApiClientResponse;
import org.reposcore.feign.client.dto.GitHubRepoItem;

import java.util.List;

public class GitHubApiClientResponseFactory {

    public static GitHubApiClientResponse createWithRepositories(List<GitHubRepoItem> repositories) {
        return new GitHubApiClientResponse(
                repositories.size(),
                false,
                repositories
        );
    }

    public static GitHubApiClientResponse createEmpty() {
        return new GitHubApiClientResponse(
                0,
                false,
                List.of()
        );
    }
}
