package com.smartserve.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB Document representing a citizen complaint in the 'complaints' collection.
 */
@Document(collection = "complaints")
public class Complaint {

    @Id
    private String id;

    @Indexed(unique = true)
    private String trackingId;

    private String title;

    private ComplaintCategory category;

    private String description;

    private ComplaintStatus status;

    private LocalDateTime submissionTime;

    private LocalDateTime lastUpdatedTime;

    private String adminRemarks;

    // Default constructor for Spring Data MongoDB
    public Complaint() {
        this.status = ComplaintStatus.SUBMITTED;
        this.submissionTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
    }

    public Complaint(String trackingId, String title, ComplaintCategory category, String description) {
        this.trackingId = trackingId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.status = ComplaintStatus.SUBMITTED;
        this.submissionTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "id='" + id + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", status=" + status +
                ", submissionTime=" + submissionTime +
                '}';
    }
}
