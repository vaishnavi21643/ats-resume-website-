package com.resumeMatcher.resume_matcher.candidate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CandidateDtos {

    public record CandidateRequest(
            @NotBlank(message = "name is required") String name,
            @NotBlank(message = "email is required") @Email String email,
            String resumeFilePath,
            @NotBlank(message = "resumeText is required") String resumeText
    ) {}

    public record CandidateResponse(
            Long id,
            String name,
            String email,
            String resumeFilePath,
            String resumeText
    ) {
        public static CandidateResponse from(Candidate c) {
            return new CandidateResponse(
                    c.getId(), c.getName(), c.getEmail(), c.getResumeFilePath(), c.getResumeText());
        }
    }
}
