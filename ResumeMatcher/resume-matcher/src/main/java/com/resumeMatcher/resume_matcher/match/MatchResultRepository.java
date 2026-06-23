//package com.resumeMatcher.resume_matcher.match;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
//
//    List<MatchResult> findByCandidateIdOrderByScoreDesc(Long candidateId);
//
//    List<MatchResult> findByJobIdOrderByScoreDesc(Long jobId);
//
//    Optional<MatchResult> findByCandidateIdAndJobId(Long candidateId, Long jobId);
//}

package com.resumeMatcher.resume_matcher.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    @Query("SELECT m FROM MatchResult m JOIN FETCH m.candidate JOIN FETCH m.job " +
            "WHERE m.candidate.id = :candidateId ORDER BY m.score DESC")
    List<MatchResult> findByCandidateIdOrderByScoreDesc(@Param("candidateId") Long candidateId);

    @Query("SELECT m FROM MatchResult m JOIN FETCH m.candidate JOIN FETCH m.job " +
            "WHERE m.job.id = :jobId ORDER BY m.score DESC")
    List<MatchResult> findByJobIdOrderByScoreDesc(@Param("jobId") Long jobId);

    @Query("SELECT m FROM MatchResult m JOIN FETCH m.candidate JOIN FETCH m.job " +
            "WHERE m.candidate.id = :candidateId AND m.job.id = :jobId")
    Optional<MatchResult> findByCandidateIdAndJobId(@Param("candidateId") Long candidateId, @Param("jobId") Long jobId);
}