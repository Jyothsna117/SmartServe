package com.smartserve.service;

import com.smartserve.dto.ComplaintRequestDto;
import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface defining complaint management business logic.
 */
public interface ComplaintService {

    /**
     * Creates and persists a new complaint with an auto-generated unique tracking ID,
     * timestamp, and initial status SUBMITTED.
     */
    Complaint createComplaint(ComplaintRequestDto request);

    /**
     * Retrieves a single complaint by its unique tracking ID.
     */
    Optional<Complaint> getComplaintByTrackingId(String trackingId);

    /**
     * Retrieves all complaints ordered by newest submission time.
     */
    List<Complaint> getAllComplaints();

    /**
     * Filters complaints by current status.
     */
    List<Complaint> getComplaintsByStatus(ComplaintStatus status);

    /**
     * Updates complaint lifecycle status and optional administrator remarks.
     */
    Complaint updateComplaintStatus(String trackingId, ComplaintStatus newStatus, String adminRemarks);

    /**
     * Generates a collision-free tracking ID in the format SMP-YYYYMMDD-XXXX.
     */
    String generateUniqueTrackingId();

    /**
     * Computes complaint counts grouped by lifecycle status for analytics.
     */
    Map<String, Long> getStatusMetrics();
}
