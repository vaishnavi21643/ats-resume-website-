package com.resumeMatcher.resume_matcher.job;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String department;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    /** Raw requirements text, kept separate so it can be highlighted in match explanations. */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(updatable = false)
    private Instant createdAt;

//new code
    @Column(name = "jd_file_path")
    private String jdFilePath;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    /** Combined text sent to the matching service. */
    @Transient
    public String getFullText() {
        StringBuilder sb = new StringBuilder();
        sb.append(description == null ? "" : description);
        if (requirements != null && !requirements.isBlank()) {
            sb.append("\n\nRequirements:\n").append(requirements);
        }
        return sb.toString();
    }
}
