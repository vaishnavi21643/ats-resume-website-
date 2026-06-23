package com.resumeMatcher.resume_matcher.fastapi;

import com.resumeMatcher.resume_matcher.fastapi.dto.FastApiDtos.MatchRequest;
import com.resumeMatcher.resume_matcher.fastapi.dto.FastApiDtos.MatchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FastApiClient {

    private final WebClient fastApiWebClient;
    private final String matchPath;

    public FastApiClient(
            WebClient fastApiWebClient,
            @Value("${fastapi.match-path:/match}") String matchPath
    ) {
        this.fastApiWebClient = fastApiWebClient;
        this.matchPath = matchPath;
    }

    /**
     * Calls the FastAPI RAG service to compute a similarity score + LLM-generated
     * explanation between a candidate's resume and a job description.
     * Blocks the calling thread (acceptable for a standard MVC controller).
     */
    public MatchResponse match(String resumeText, String jobDescription) {
        MatchRequest request = new MatchRequest(resumeText, jobDescription);

        return fastApiWebClient.post()
                .uri(matchPath)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MatchResponse.class)
                .block();
    }
}
