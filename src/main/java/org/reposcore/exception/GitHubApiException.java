package org.reposcore.exception;

public class GitHubApiException extends RuntimeException {

    public GitHubApiException(String message) {
        super("Error calling githubApi: " + message);
    }
}
