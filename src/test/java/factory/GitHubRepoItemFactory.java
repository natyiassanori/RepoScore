package factory;

import org.reposcore.feign.client.dto.GitHubRepoItem;

import java.util.Date;

public class GitHubRepoItemFactory {

    private static final Date DEFAULT_DATE = new Date();
    private static final String DEFAULT_OWNER = "test-owner";
    private static final String DEFAULT_REPO_NAME = "test-repo";
    private static final String DEFAULT_LANGUAGE = "JAVA";
    private static final String DEFAULT_DESCRIPTION = "Test repository";
    private static final int DEFAULT_STARS = 100;
    private static final int DEFAULT_FORKS = 50;

    public static GitHubRepoItem createDefault() {
        return new GitHubRepoItem(
                1L,
                DEFAULT_REPO_NAME,
                DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                "https://github.com/" + DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DATE,
                DEFAULT_DATE,
                DEFAULT_LANGUAGE,
                DEFAULT_STARS,
                DEFAULT_FORKS
        );
    }

    public static GitHubRepoItem createWithId(long id) {
        return new GitHubRepoItem(
                id,
                DEFAULT_REPO_NAME + "-" + id,
                DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME + "-" + id,
                "https://github.com/" + DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME + "-" + id,
                DEFAULT_DESCRIPTION + " " + id,
                DEFAULT_DATE,
                DEFAULT_DATE,
                DEFAULT_LANGUAGE,
                DEFAULT_STARS,
                DEFAULT_FORKS
        );
    }

    public static GitHubRepoItem createWithMetrics(int stars, int forks) {
        return new GitHubRepoItem(
                1L,
                DEFAULT_REPO_NAME,
                DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                "https://github.com/" + DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DATE,
                DEFAULT_DATE,
                DEFAULT_LANGUAGE,
                stars,
                forks
        );
    }

    public static GitHubRepoItem createWithDates(Date createdAt, Date updatedAt) {
        return new GitHubRepoItem(
                1L,
                DEFAULT_REPO_NAME,
                DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                "https://github.com/" + DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                DEFAULT_DESCRIPTION,
                createdAt,
                updatedAt,
                DEFAULT_LANGUAGE,
                DEFAULT_STARS,
                DEFAULT_FORKS
        );
    }

    public static GitHubRepoItem createWithLanguage(String language) {
        return new GitHubRepoItem(
                1L,
                DEFAULT_REPO_NAME,
                DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                "https://github.com/" + DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DATE,
                DEFAULT_DATE,
                language,
                DEFAULT_STARS,
                DEFAULT_FORKS
        );
    }

    public static GitHubRepoItem createTensorFlow() {
        return new GitHubRepoItem(
                3L,
                "tensorflow",
                "tensorflow/tensorflow",
                "https://github.com/tensorflow/tensorflow",
                "An Open Source Machine Learning Framework for Everyone",
                DEFAULT_DATE,
                DEFAULT_DATE,
                "Python",
                180000,
                90000
        );
    }

    public static java.util.List<GitHubRepoItem> createPythonRepositories() {
        return java.util.List.of(
                createTensorFlow(),
                createWithLanguage("Python")
        );
    }
}
