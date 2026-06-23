package com.resumeMatcher.resume_matcher.match;

public class MatchDtos {

    public record MatchResultResponse(
            Long id,
            Long candidateId,
            String candidateName,
            Long jobId,
            String jobTitle,
            double score,
            String explanation
    ) {
        public static MatchResultResponse from(MatchResult m) {
            return new MatchResultResponse(
                    m.getId(),
                    m.getCandidate().getId(),
                    m.getCandidate().getName(),
                    m.getJob().getId(),
                    m.getJob().getTitle(),
                    m.getScore(),
                    m.getExplanation()
            );
        }
    }
}
