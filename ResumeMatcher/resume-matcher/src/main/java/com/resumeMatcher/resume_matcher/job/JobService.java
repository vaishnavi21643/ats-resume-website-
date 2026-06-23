package com.resumeMatcher.resume_matcher.job;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static com.resumeMatcher.resume_matcher.job.JobDtos.JobRequest;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job create(JobRequest req) {
        Job job = new Job();
        job.setTitle(req.title());
        job.setDepartment(req.department());
        job.setDescription(req.description());
        job.setRequirements(req.requirements());
        return jobRepository.save(job);
    }

//    public Job create(String title, String department, String description, String requirements) {
//        Job job = new Job();
//        job.setTitle(title);
//        job.setDepartment(department);
//        job.setDescription(description);
//        job.setRequirements(requirements);
//        job.setJdFilePath(savedFilePath); // same variable you already use for saving the file
//        return jobRepository.save(job);
//
//    }


    public Job create(String title, String department, String description, String requirements, String savedFilePath) {
        Job job = new Job();
        job.setTitle(title);
        job.setDepartment(department);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setJdFilePath(savedFilePath);
        return jobRepository.save(job);
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job not found with id: " + id));
    }

    public Job update(Long id, JobRequest req) {
        Job job = findById(id);
        job.setTitle(req.title());
        job.setDepartment(req.department());
        job.setDescription(req.description());
        job.setRequirements(req.requirements());
        return jobRepository.save(job);
    }

    public void delete(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new NoSuchElementException("Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }
}
