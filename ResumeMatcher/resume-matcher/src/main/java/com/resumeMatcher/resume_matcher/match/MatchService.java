package com.resumeMatcher.resume_matcher.match;

import com.resumeMatcher.resume_matcher.candidate.Candidate;
import com.resumeMatcher.resume_matcher.candidate.CandidateService;
import com.resumeMatcher.resume_matcher.fastapi.FastApiClient;
import com.resumeMatcher.resume_matcher.fastapi.dto.FastApiDtos.MatchResponse;
import com.resumeMatcher.resume_matcher.job.Job;
import com.resumeMatcher.resume_matcher.job.JobService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatchService {

    private final CandidateService candidateService;
    private final JobService jobService;
    private final FastApiClient fastApiClient;
    private final MatchResultRepository matchResultRepository;

    public MatchService(
            CandidateService candidateService,
            JobService jobService,
            FastApiClient fastApiClient,
            MatchResultRepository matchResultRepository
    ) {
        this.candidateService = candidateService;
        this.jobService = jobService;
        this.fastApiClient = fastApiClient;
        this.matchResultRepository = matchResultRepository;
    }

    /**
     * Runs (or re-runs) a match between a candidate and a job, persisting the result.
     * If a match already exists for this pair, it is overwritten with the fresh result.
     */

    @Transactional
    public MatchResult runMatch(Long candidateId, Long jobId) {
        Candidate candidate = candidateService.findById(candidateId);
        Job job = jobService.findById(jobId);

        MatchResponse response = fastApiClient.match(candidate.getResumeText(), job.getFullText());

        MatchResult result = matchResultRepository
                .findByCandidateIdAndJobId(candidateId, jobId)
                .orElseGet(MatchResult::new);

        result.setCandidate(candidate);
        result.setJob(job);
        result.setScore(response.score());
        result.setExplanation(response.explanation());

        return matchResultRepository.save(result);
    }

    @Transactional(readOnly = true)
    public List<MatchResult> findByCandidate(Long candidateId) {
        // Ensures a 404 is thrown if the candidate doesn't exist
        candidateService.findById(candidateId);
        return matchResultRepository.findByCandidateIdOrderByScoreDesc(candidateId);
    }

    @Transactional(readOnly = true)
    public List<MatchResult> findByJob(Long jobId) {
        jobService.findById(jobId);
        return matchResultRepository.findByJobIdOrderByScoreDesc(jobId);
    }
}
