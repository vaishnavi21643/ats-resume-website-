package com.resumeMatcher.resume_matcher.match;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.resumeMatcher.resume_matcher.match.MatchDtos.MatchResultResponse;

@RestController
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /** Trigger (or re-run) a match between a candidate and a job. */
    @PostMapping("/candidates/{candidateId}/jobs/{jobId}/match")
    public MatchResultResponse runMatch(@PathVariable Long candidateId, @PathVariable Long jobId) {
        MatchResult result = matchService.runMatch(candidateId, jobId);
        return MatchResultResponse.from(result);
    }

    /** All job matches for a candidate, best score first. */
    @GetMapping("/candidates/{candidateId}/matches")
    public List<MatchResultResponse> matchesForCandidate(@PathVariable Long candidateId) {
        return matchService.findByCandidate(candidateId).stream()
                .map(MatchResultResponse::from)
                .toList();
    }

    /** All candidate matches for a job, best score first. */
    @GetMapping("/jobs/{jobId}/matches")
    public List<MatchResultResponse> matchesForJob(@PathVariable Long jobId) {
        return matchService.findByJob(jobId).stream()
                .map(MatchResultResponse::from)
                .toList();
    }
}
