package com.resumeMatcher.resume_matcher.job;

import jakarta.validation.constraints.NotBlank;

public class JobDtos {

    public record JobRequest(
            @NotBlank(message = "title is required") String title,
            String department,
            @NotBlank(message = "description is required") String description,
            String requirements
    ) {}

    public record JobResponse(
            Long id,
            String title,
            String department,
            String description,
            String requirements
    ) {
        public static JobResponse from(Job j) {
            return new JobResponse(
                    j.getId(), j.getTitle(), j.getDepartment(), j.getDescription(), j.getRequirements());
        }
    }
}
