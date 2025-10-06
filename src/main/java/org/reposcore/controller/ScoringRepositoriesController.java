package org.reposcore.controller;

import lombok.RequiredArgsConstructor;
import org.reposcore.service.ScoringRepositoriesService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getScoredRepositories(@RequestParam(value = "earliestCreatedDate", required = false) Date createdDate,
                                                        @RequestParam(value = "language", required = false) String language) {
        return scoringRepositoriesService.getRepositoriesWithScore(createdDate, language);
    }
}
