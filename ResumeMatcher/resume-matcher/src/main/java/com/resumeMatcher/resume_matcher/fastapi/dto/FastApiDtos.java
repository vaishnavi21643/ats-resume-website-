package com.resumeMatcher.resume_matcher.fastapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FastApiDtos {

    /**
     * Sent to FastAPI's /match endpoint.
     * Field names are mapped to snake_case to match the Pydantic model on the FastAPI side.
     */
    public record MatchRequest(
            @JsonProperty("resume_text") String resumeText,
            @JsonProperty("job_description") String jobDescription
    ) {}

    /** Received back from FastAPI's /match endpoint. */
    public record MatchResponse(
            double score,
            String explanation
    ) {}
}
