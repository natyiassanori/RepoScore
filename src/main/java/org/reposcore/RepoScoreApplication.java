package org.reposcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {org.reposcore.feign.client.GitHubApiClient.class})
public class RepoScoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(RepoScoreApplication.class, args);
    }
}
