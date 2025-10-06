package org.scoringapi.feign.client;

import org.scoringapi.feign.client.dto.GitHubApiClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "githubApiClient",
        url = "${services.github-api.url}"
)
public interface GitHubApiClient {

    @GetMapping(path = "/search/repositories")
    ResponseEntity<GitHubApiClientResponse> searchRepositories(@RequestParam(value = "q") String query,
                                                              @RequestParam(value = "per_page") int perPage,
                                                              @RequestParam(value = "page") int page);
}
