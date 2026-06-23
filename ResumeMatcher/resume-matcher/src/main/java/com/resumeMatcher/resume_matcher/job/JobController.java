package com.resumeMatcher.resume_matcher.job;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.resumeMatcher.resume_matcher.util.PdfTextExtractor;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.resumeMatcher.resume_matcher.job.JobDtos.JobRequest;
import static com.resumeMatcher.resume_matcher.job.JobDtos.JobResponse;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final PdfTextExtractor pdfTextExtractor;

//    public JobController(JobService jobService) {
//        this.jobService = jobService;
//    }

    public JobController(JobService jobService, PdfTextExtractor pdfTextExtractor) {
        this.jobService = jobService;
        this.pdfTextExtractor = pdfTextExtractor;
    }

//    @PostMapping
//    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest req) {
//        Job created = jobService.create(req);
//        return ResponseEntity.status(HttpStatus.CREATED).body(JobResponse.from(created));
//    }

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<JobResponse> create(
//            @RequestParam("title") String title,
//            @RequestParam(value = "department", required = false) String department,
//            @RequestParam("jd") MultipartFile jdFile,
//            @RequestParam(value = "requirements", required = false) String requirements
//    ) {
//        String description = pdfTextExtractor.extractText(jdFile);
//        Job created = jobService.create(title, department, description, requirements);
//        return ResponseEntity.status(HttpStatus.CREATED).body(JobResponse.from(created));
//    }



//    --------------------------------------new create------------------------

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobResponse> create(
            @RequestParam("title") String title,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam("jd") MultipartFile jdFile,
            @RequestParam(value = "requirements", required = false) String requirements
    ) throws IOException {
        // Extract text for DB
        String description = pdfTextExtractor.extractText(jdFile);

        // Save PDF to disk and get the path
        String savedFilePath = saveFile(jdFile, "jobs");

        Job created = jobService.create(title, department, description, requirements, savedFilePath);
        return ResponseEntity.status(HttpStatus.CREATED).body(JobResponse.from(created));
    }

//    private String saveFile(MultipartFile file, String folder) throws IOException {
//        Path uploadDir = Path.of("uploads", folder);
//        Files.createDirectories(uploadDir);
//        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//        Path filePath = uploadDir.resolve(filename);
//        file.transferTo(filePath.toFile());
//        return filePath.toAbsolutePath().toString();
//    }

    private String saveFile(MultipartFile file, String folder) throws IOException {
        Path uploadDir = Path.of("uploads", folder).toAbsolutePath();
        Files.createDirectories(uploadDir);
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);  // ← replaces file.transferTo()
        return filePath.toString();
    }

    @GetMapping
    public List<JobResponse> findAll() {
        return jobService.findAll().stream().map(JobResponse::from).toList();
    }

    @GetMapping("/{id}")
    public JobResponse findById(@PathVariable Long id) {
        return JobResponse.from(jobService.findById(id));
    }

    @PutMapping("/{id}")
    public JobResponse update(@PathVariable Long id, @Valid @RequestBody JobRequest req) {
        return JobResponse.from(jobService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
