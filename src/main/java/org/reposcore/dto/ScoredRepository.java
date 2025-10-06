package org.reposcore.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ScoredRepository {
    private String name;
    private String fullName;
    private String language;
    private int stars;
    private int forks;
    private String description;
    private String htmlUrl;
    private Date createdAt;
    private Date updatedAt;
    private double score;
}



