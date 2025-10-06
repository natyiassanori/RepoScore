package factory;

import org.reposcore.dto.ScoredRepository;

import java.util.Date;

public class ScoredRepositoryFactory {

    private static final Date DEFAULT_DATE = new Date();
    private static final String DEFAULT_OWNER = "test-owner";
    private static final String DEFAULT_REPO_NAME = "test-repo";
    private static final String DEFAULT_LANGUAGE = "JAVA";
    private static final String DEFAULT_DESCRIPTION = "Test repository";
    private static final int DEFAULT_STARS = 100;
    private static final int DEFAULT_FORKS = 50;

    public static ScoredRepository createWithScore(double score) {
        return ScoredRepository.builder()
                .name(DEFAULT_REPO_NAME)
                .fullName(DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME)
                .language(DEFAULT_LANGUAGE)
                .stars(DEFAULT_STARS)
                .forks(DEFAULT_FORKS)
                .description(DEFAULT_DESCRIPTION)
                .htmlUrl("https://github.com/" + DEFAULT_OWNER + "/" + DEFAULT_REPO_NAME)
                .createdAt(DEFAULT_DATE)
                .updatedAt(DEFAULT_DATE)
                .score(score)
                .build();
    }
}
