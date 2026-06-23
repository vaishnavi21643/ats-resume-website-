package com.resumeMatcher.resume_matcher.match;

import com.resumeMatcher.resume_matcher.candidate.Candidate;
import com.resumeMatcher.resume_matcher.job.Job;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "match_results")
@Getter
@Setter
@NoArgsConstructor
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(nullable = false)
    private double score;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public MatchResult(Candidate candidate, Job job, double score, String explanation) {
        this.candidate = candidate;
        this.job = job;
        this.score = score;
        this.explanation = explanation;
    }
}
