package com.smartserve.service;

import com.smartserve.dto.ComplaintRequestDto;
import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintStatus;
import com.smartserve.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Production implementation of ComplaintService encapsulating persistence,
 * validation, and status tracking business rules.
 */
@Service
public class ComplaintServiceImpl implements ComplaintService {

    private static final Logger log = LoggerFactory.getLogger(ComplaintServiceImpl.class);
    private static final String ALPHANUMERIC = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"; // excludes ambiguous 0, 1, I, O
    private static final int RANDOM_TOKEN_LENGTH = 4;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ComplaintRepository complaintRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ComplaintServiceImpl(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public Complaint createComplaint(ComplaintRequestDto request) {
        log.info("Registering new complaint with category: {}", request.getCategory());

        String trackingId = generateUniqueTrackingId();

        Complaint complaint = new Complaint();
        complaint.setTrackingId(trackingId);
        complaint.setTitle(request.getTitle().trim());
        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription().trim());
        complaint.setStatus(ComplaintStatus.SUBMITTED);
        complaint.setSubmissionTime(LocalDateTime.now());
        complaint.setLastUpdatedTime(LocalDateTime.now());
        complaint.setAdminRemarks(null);

        Complaint savedComplaint = complaintRepository.save(complaint);
        log.info("Complaint successfully persisted with Tracking ID: {}", savedComplaint.getTrackingId());
        return savedComplaint;
    }

    @Override
    public Optional<Complaint> getComplaintByTrackingId(String trackingId) {
        if (trackingId == null || trackingId.trim().isEmpty()) {
            return Optional.empty();
        }
        return complaintRepository.findByTrackingId(trackingId.trim().toUpperCase());
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderBySubmissionTimeDesc();
    }

    @Override
    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        if (status == null) {
            return getAllComplaints();
        }
        return complaintRepository.findByStatusOrderBySubmissionTimeDesc(status);
    }

    @Override
    public Complaint updateComplaintStatus(String trackingId, ComplaintStatus newStatus, String adminRemarks) {
        log.info("Updating complaint {} to status: {}", trackingId, newStatus);

        Complaint complaint = complaintRepository.findByTrackingId(trackingId.trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Complaint with Tracking ID " + trackingId + " not found."));

        complaint.setStatus(newStatus);
        if (adminRemarks != null && !adminRemarks.trim().isEmpty()) {
            complaint.setAdminRemarks(adminRemarks.trim());
        }
        complaint.setLastUpdatedTime(LocalDateTime.now());

        Complaint updated = complaintRepository.save(complaint);
        log.info("Complaint {} successfully updated. Last updated at: {}", trackingId, updated.getLastUpdatedTime());
        return updated;
    }

    @Override
    public String generateUniqueTrackingId() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String trackingId;
        int attempts = 0;

        do {
            StringBuilder token = new StringBuilder(RANDOM_TOKEN_LENGTH);
            for (int i = 0; i < RANDOM_TOKEN_LENGTH; i++) {
                int index = secureRandom.nextInt(ALPHANUMERIC.length());
                token.append(ALPHANUMERIC.charAt(index));
            }
            trackingId = "SMP-" + datePart + "-" + token;
            attempts++;

            if (attempts > 50) {
                // Safeguard against infinite loops in high-density scenarios
                trackingId = "SMP-" + datePart + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                break;
            }
        } while (complaintRepository.existsByTrackingId(trackingId));

        return trackingId;
    }

    @Override
    public Map<String, Long> getStatusMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        long submitted = complaintRepository.countByStatus(ComplaintStatus.SUBMITTED);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        long rejected = complaintRepository.countByStatus(ComplaintStatus.REJECTED);
        long total = submitted + inProgress + resolved + rejected;

        metrics.put("TOTAL", total);
        metrics.put("SUBMITTED", submitted);
        metrics.put("IN_PROGRESS", inProgress);
        metrics.put("RESOLVED", resolved);
        metrics.put("REJECTED", rejected);
        return metrics;
    }
}
