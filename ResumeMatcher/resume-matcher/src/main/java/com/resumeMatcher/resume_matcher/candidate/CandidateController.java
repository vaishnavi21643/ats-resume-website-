//package com.resumeMatcher.resume_matcher.candidate;
//
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import com.resumeMatcher.resume_matcher.util.PdfTextExtractor;
//
//import java.util.List;
//
//import static com.resumeMatcher.resume_matcher.candidate.CandidateDtos.CandidateRequest;
//import static com.resumeMatcher.resume_matcher.candidate.CandidateDtos.CandidateResponse;
//
//@RestController
//@RequestMapping("/candidates")
//public class CandidateController {
//
//    private final CandidateService candidateService;
//    //new changes made
//    private final PdfTextExtractor pdfTextExtractor;
//
//    public CandidateController(CandidateService candidateService, PdfTextExtractor pdfTextExtractor) {
//        this.candidateService = candidateService;
//        this.pdfTextExtractor = pdfTextExtractor;
//    }
//
//// public CandidateController(CandidateService candidateService) {
////        this.candidateService = candidateService;
////    }
//
////    @PostMapping
////    public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CandidateRequest req) {
////        Candidate created = candidateService.create(req);
////        return ResponseEntity.status(HttpStatus.CREATED).body(CandidateResponse.from(created));
////    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<CandidateResponse> create(
//            @RequestParam("name") String name,
//            @RequestParam("email") String email,
//            @RequestParam("resume") MultipartFile resumeFile
//    ) {
//        String resumeText = pdfTextExtractor.extractText(resumeFile);
//        Candidate created = candidateService.create(name, email, resumeFile.getOriginalFilename(), resumeText);
//        return ResponseEntity.status(HttpStatus.CREATED).body(CandidateResponse.from(created));
//    }
//
//    @GetMapping
//    public List<CandidateResponse> findAll() {
//        return candidateService.findAll().stream().map(CandidateResponse::from).toList();
//    }
//
//    @GetMapping("/{id}")
//    public CandidateResponse findById(@PathVariable Long id) {
//        return CandidateResponse.from(candidateService.findById(id));
//    }
//
//    @PutMapping("/{id}")
//    public CandidateResponse update(@PathVariable Long id, @Valid @RequestBody CandidateRequest req) {
//        return CandidateResponse.from(candidateService.update(id, req));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        candidateService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//}





package com.resumeMatcher.resume_matcher.candidate;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.resumeMatcher.resume_matcher.util.PdfTextExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.resumeMatcher.resume_matcher.candidate.CandidateDtos.CandidateRequest;
import static com.resumeMatcher.resume_matcher.candidate.CandidateDtos.CandidateResponse;

@RestController
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService;
    private final PdfTextExtractor pdfTextExtractor;

    public CandidateController(CandidateService candidateService, PdfTextExtractor pdfTextExtractor) {
        this.candidateService = candidateService;
        this.pdfTextExtractor = pdfTextExtractor;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidateResponse> create(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("resume") MultipartFile resumeFile
    ) throws IOException {
        String resumeText = pdfTextExtractor.extractText(resumeFile);
        String savedFilePath = saveFile(resumeFile, "candidates");
        Candidate created = candidateService.create(name, email, savedFilePath, resumeText);
        return ResponseEntity.status(HttpStatus.CREATED).body(CandidateResponse.from(created));
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
    public List<CandidateResponse> findAll() {
        return candidateService.findAll().stream().map(CandidateResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CandidateResponse findById(@PathVariable Long id) {
        return CandidateResponse.from(candidateService.findById(id));
    }

    @PutMapping("/{id}")
    public CandidateResponse update(@PathVariable Long id, @Valid @RequestBody CandidateRequest req) {
        return CandidateResponse.from(candidateService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}