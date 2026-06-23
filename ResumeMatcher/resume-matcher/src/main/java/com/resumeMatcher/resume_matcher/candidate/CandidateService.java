package com.resumeMatcher.resume_matcher.candidate;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static com.resumeMatcher.resume_matcher.candidate.CandidateDtos.CandidateRequest;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public Candidate create(CandidateRequest req) {
        if (candidateRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("A candidate with this email already exists.");
        }
        Candidate candidate = new Candidate();
        candidate.setName(req.name());
        candidate.setEmail(req.email());
        candidate.setResumeFilePath(req.resumeFilePath());
        candidate.setResumeText(req.resumeText());
        return candidateRepository.save(candidate);
    }


    //new added code
    public Candidate create(String name, String email, String resumeFilePath, String resumeText) {
        if (candidateRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A candidate with this email already exists.");
        }
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setResumeFilePath(resumeFilePath);
        candidate.setResumeText(resumeText);
        return candidateRepository.save(candidate);
    }

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    public Candidate findById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidate not found with id: " + id));
    }

    public Candidate update(Long id, CandidateRequest req) {
        Candidate candidate = findById(id);
        candidate.setName(req.name());
        candidate.setEmail(req.email());
        candidate.setResumeFilePath(req.resumeFilePath());
        candidate.setResumeText(req.resumeText());
        return candidateRepository.save(candidate);
    }

    public void delete(Long id) {
        if (!candidateRepository.existsById(id)) {
            throw new NoSuchElementException("Candidate not found with id: " + id);
        }
        candidateRepository.deleteById(id);
    }
}
