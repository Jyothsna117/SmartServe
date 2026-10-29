package com.smartserve.repository;

import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB Repository for managing Complaint documents.
 */
@Repository
public interface ComplaintRepository extends MongoRepository<Complaint, String> {

    /**
     * Find a complaint by its unique tracking ID (e.g. SMP-20261029-ABCD).
     */
    Optional<Complaint> findByTrackingId(String trackingId);

    /**
     * Check if a tracking ID already exists in MongoDB.
     */
    boolean existsByTrackingId(String trackingId);

    /**
     * Retrieve all complaints ordered chronologically by newest submission first.
     */
    List<Complaint> findAllByOrderBySubmissionTimeDesc();

    /**
     * Filter complaints by lifecycle status ordered by newest submission first.
     */
    List<Complaint> findByStatusOrderBySubmissionTimeDesc(ComplaintStatus status);

    /**
     * Count complaints by specific lifecycle status for admin dashboard metrics.
     */
    long countByStatus(ComplaintStatus status);
}
