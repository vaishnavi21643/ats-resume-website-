//package com.resumeMatcher.resume_matcher.chat;
//
//import com.resumeMatcher.resume_matcher.candidate.Candidate;
//import com.resumeMatcher.resume_matcher.candidate.CandidateRepository;
//import com.resumeMatcher.resume_matcher.job.Job;
//import com.resumeMatcher.resume_matcher.job.JobRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.MultipartBodyBuilder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ChatService {
//
//    private static final String SESSION_ID = "global-rag-session";
//
////    private final WebClient webClient;
//private final WebClient fastApiWebClient;
//
//    private final CandidateRepository candidateRepo;
//    private final JobRepository jobRepo;
//
//    private final AtomicBoolean seeded = new AtomicBoolean(false);
//
//    // ------------------------------------------------------------------ //
//    //  PUBLIC API
//    // ------------------------------------------------------------------ //
//
//    @Transactional(readOnly = true)
//    public void seedContext() {
//        log.info("Seeding RAG context with PDFs...");
//
//        List<Candidate> candidates = candidateRepo.findAll();
//        List<Job> jobs = jobRepo.findAll();
//
//        int uploaded = 0;
//
//        for (Candidate c : candidates) {
//            if (c.getResumeFilePath() != null && !c.getResumeFilePath().isBlank()) {
//                boolean ok = uploadPdf(
//                        "candidate_" + c.getId() + "_" + sanitize(c.getName()) + ".pdf",
//                        c.getResumeFilePath()
//                );
//                if (ok) uploaded++;
//            } else {
//                log.warn("Candidate {} has no resume file, skipping", c.getId());
//            }
//        }
//
//        for (Job j : jobs) {
//            if (j.getJdFilePath() != null && !j.getJdFilePath().isBlank()) {
//                boolean ok = uploadPdf(
//                        "job_" + j.getId() + "_" + sanitize(j.getTitle()) + ".pdf",
//                        j.getJdFilePath()
//                );
//                if (ok) uploaded++;
//            } else {
//                log.warn("Job {} has no JD file, skipping", j.getId());
//            }
//        }
//
//        seeded.set(true);
//        log.info("Seeding complete — {}/{} documents uploaded",
//                uploaded, candidates.size() + jobs.size());
//    }
//
//    public ChatDtos.ChatResponse ask(String question) {
//        // Auto-seed on first message
//        if (!seeded.get()) {
//            seedContext();
//        }
//
//        Map<String, Object> body = Map.of(
//                "session_id", SESSION_ID,
//                "question", question
//        );
//
//        Map<?, ?> response = fastApiWebClient.post()
//                .uri("/ask")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(Map.class)
//                .block();
//
//        String answer = response != null ? (String) response.get("answer") : "No response";
//        Object rawSources = response != null ? response.get("sources") : List.of();
//        List<String> sources = rawSources instanceof List<?> list
//                ? list.stream().map(Object::toString).toList()
//                : List.of();
//
//        return new ChatDtos.ChatResponse(answer, sources);
//    }
//
//    public void clearSession() {
//        fastApiWebClient.delete()
//                .uri("/session/" + SESSION_ID)
//                .retrieve()
//                .toBodilessEntity()
//                .block();
//        seeded.set(false);
//        log.info("RAG session cleared — will reseed on next ask");
//    }
//
//    // ------------------------------------------------------------------ //
//    //  PRIVATE HELPERS
//    // ------------------------------------------------------------------ //
//
//    private boolean uploadPdf(String filename, String filePath) {
//        try {
//            byte[] bytes = Files.readAllBytes(Path.of(filePath));
//
//            MultipartBodyBuilder builder = new MultipartBodyBuilder();
//            builder.part("session_id", SESSION_ID);
//            builder.part("files", new ByteArrayResource(bytes) {
//                @Override
//                public String getFilename() { return filename; }
//            }).contentType(MediaType.APPLICATION_PDF);
//
//            fastApiWebClient.post()
//                    .uri("/upload")
//                    .contentType(MediaType.MULTIPART_FORM_DATA)
//                    .body(BodyInserters.fromMultipartData(builder.build()))
//                    .retrieve()
//                    .toBodilessEntity()
//                    .block();
//
//            log.debug("Uploaded: {}", filename);
//            return true;
//
//        } catch (Exception e) {
//            log.warn("Failed to upload {} — {}", filename, e.getMessage());
//            return false;
//        }
//    }
//
//    private String sanitize(String input) {
//        if (input == null) return "unknown";
//        return input.replaceAll("[^a-zA-Z0-9_-]", "_");
//    }
//}


package com.resumeMatcher.resume_matcher.chat;

import com.resumeMatcher.resume_matcher.candidate.Candidate;
import com.resumeMatcher.resume_matcher.candidate.CandidateRepository;
import com.resumeMatcher.resume_matcher.job.Job;
import com.resumeMatcher.resume_matcher.job.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String SESSION_ID = "global-rag-session";

    private final WebClient fastApiWebClient;

    private final CandidateRepository candidateRepo;
    private final JobRepository jobRepo;

    private final AtomicBoolean seeded = new AtomicBoolean(false);

    // ------------------------------------------------------------------ //
    //  PUBLIC API
    // ------------------------------------------------------------------ //

    @Transactional(readOnly = true)
    public void seedContext() {
        log.info("Seeding RAG context with PDFs...");

        List<Candidate> candidates = candidateRepo.findAll();
        List<Job> jobs = jobRepo.findAll();

        // Build ONE multipart request with ALL PDFs
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("session_id", SESSION_ID);                   // ← single session_id field

        int added = 0;

        for (Candidate c : candidates) {
            if (c.getResumeFilePath() != null && !c.getResumeFilePath().isBlank()) {
                try {
                    byte[] bytes = Files.readAllBytes(Path.of(c.getResumeFilePath()));
                    String filename = "candidate_" + c.getId() + "_" + sanitize(c.getName()) + ".pdf";
                    builder.part("files", new ByteArrayResource(bytes) {
                        @Override public String getFilename() { return filename; }
                    }).contentType(MediaType.APPLICATION_PDF);
                    added++;
                } catch (Exception e) {
                    log.warn("Could not read resume for candidate {} — {}", c.getId(), e.getMessage());
                }
            } else {
                log.warn("Candidate {} has no resume file, skipping", c.getId());
            }
        }

        for (Job j : jobs) {
            if (j.getJdFilePath() != null && !j.getJdFilePath().isBlank()) {
                try {
                    byte[] bytes = Files.readAllBytes(Path.of(j.getJdFilePath()));
                    String filename = "job_" + j.getId() + "_" + sanitize(j.getTitle()) + ".pdf";
                    builder.part("files", new ByteArrayResource(bytes) {
                        @Override public String getFilename() { return filename; }
                    }).contentType(MediaType.APPLICATION_PDF);
                    added++;
                } catch (Exception e) {
                    log.warn("Could not read JD for job {} — {}", j.getId(), e.getMessage());
                }
            } else {
                log.warn("Job {} has no JD file, skipping", j.getId());
            }
        }

        if (added == 0) {
            log.warn("No PDFs found to upload — skipping seed");
            return;
        }

        // Single POST with all files at once
        fastApiWebClient.post()
                .uri("/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toBodilessEntity()
                .block();

        seeded.set(true);
        log.info("Seeding complete — {}/{} documents uploaded",
                added, candidates.size() + jobs.size());
    }

    public ChatDtos.ChatResponse ask(String question) {
        if (!seeded.get()) {
            seedContext();
        }

        Map<String, Object> body = Map.of(
                "session_id", SESSION_ID,
                "question", question
        );

        Map<?, ?> response = fastApiWebClient.post()
                .uri("/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String answer = response != null ? (String) response.get("answer") : "No response";
        Object rawSources = response != null ? response.get("sources") : List.of();
        List<String> sources = rawSources instanceof List<?> list
                ? list.stream().map(Object::toString).toList()
                : List.of();

        return new ChatDtos.ChatResponse(answer, sources);
    }

    public void clearSession() {
        fastApiWebClient.delete()
                .uri("/session/" + SESSION_ID)
                .retrieve()
                .toBodilessEntity()
                .block();
        seeded.set(false);
        log.info("RAG session cleared — will reseed on next ask");
    }

    // ------------------------------------------------------------------ //
    //  PRIVATE HELPERS
    // ------------------------------------------------------------------ //

    private String sanitize(String input) {
        if (input == null) return "unknown";
        return input.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}