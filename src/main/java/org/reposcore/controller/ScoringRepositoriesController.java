package org.reposcore.controller;

import lombok.RequiredArgsConstructor;
import org.reposcore.dto.ScoringRepositoriesResponse;
import org.reposcore.service.ScoringRepositoriesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/scoring")
@RequiredArgsConstructor
public class ScoringRepositoriesController {

    private final ScoringRepositoriesService scoringRepositoriesService;

    @GetMapping
    public ScoringRepositoriesResponse getScoredRepositories(@RequestParam(name = "earliestCreatedDate", required = false)
                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date earliestCreatedDate,
                                                             @RequestParam(name = "language", required = false) String language) {
        return scoringRepositoriesService.getRepositoriesWithScore(earliestCreatedDate, language);
    }
}
