package org.reposcore.service;

import lombok.extern.slf4j.Slf4j;
import org.reposcore.feign.client.GitHubApiClient;
import org.reposcore.feign.client.dto.GitHubApiClientResponse;
import org.reposcore.feign.client.dto.GitHubRepoItem;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GitHubPaginationService {

    @Cacheable("gitHubRepositories")
    public List<GitHubRepoItem> fetchAllRepositoriesFromGitHub(GitHubApiClient gitHubApiClient, String query) {
        List<GitHubRepoItem> allRepositories = new ArrayList<>();
        
        int currentPage = 1;
        int perPage = 100;
        boolean hasMorePages = true;
        
        while (hasMorePages) {
            try {
                ResponseEntity<GitHubApiClientResponse> responseEntity = gitHubApiClient.searchRepositories(
                    query, 
                    perPage, 
                    currentPage
                );
                
                GitHubApiClientResponse response = responseEntity.getBody();
                
                if (isValidResponse(response)) {
                    allRepositories.addAll(response.items());
                    
                    // Check response header (Link) to determine if there are more pages
                    String linkHeader = responseEntity.getHeaders().getFirst("Link");
                    hasMorePages = hasNextPage(linkHeader);
                    
                    if (hasMorePages) {
                        currentPage++;
                    }
                } else {
                    hasMorePages = false;
                }
            } catch (Exception ex) {
                log.error("Error fetching repositories from page {}: {}", currentPage, ex.getMessage());
                hasMorePages = false;
            }
        }
        
        log.info("Fetched {} repositories across {} pages", allRepositories.size(), currentPage);
        return allRepositories;
    }
    
    private static boolean isValidResponse(GitHubApiClientResponse response) {
        return response != null && 
               response.items() != null && 
               !response.items().isEmpty();
    }
    
    private static boolean hasNextPage(String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return false;
        }
        return linkHeader.contains("rel=\"next\"");
    }
}
