package com.smartserve.repository;

import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintCategory;
import com.smartserve.model.ComplaintStatus;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MongoDB Live Integration Test.
 * This test runs against the real MongoDB instance (localhost:27017).
 * If the MongoDB service is not currently reachable, the test dynamically skips
 * using JUnit 5 Assumptions without failing standard CI/CD pipeline builds.
 */
@SpringBootTest
class ComplaintMongoIntegrationTest {

    @Autowired(required = false)
    private ComplaintRepository complaintRepository;

    @BeforeEach
    void checkMongoAvailability() {
        boolean mongoReachable = isPortReachable("localhost", 27017, 1000);
        Assumptions.assumeTrue(mongoReachable,
                "MongoDB is not listening on localhost:27017. Skipping live integration test.");
    }

    private boolean isPortReachable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    @DisplayName("Should successfully insert, retrieve, and update complaint in live MongoDB")
    void testLiveMongoCrudLifecycle() {
        assertThat(complaintRepository).isNotNull();

        String testTrackingId = "SMP-INTEG-" + System.currentTimeMillis();
        Complaint complaint = new Complaint(testTrackingId, "Live Mongo Test Issue", ComplaintCategory.ROADS, "Integration test description");

        // 1. Insert
        Complaint saved = complaintRepository.save(complaint);
        assertThat(saved.getId()).isNotNull();

        // 2. Query
        Optional<Complaint> fetched = complaintRepository.findByTrackingId(testTrackingId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getTitle()).isEqualTo("Live Mongo Test Issue");

        // 3. Update
        fetched.get().setStatus(ComplaintStatus.RESOLVED);
        fetched.get().setAdminRemarks("Resolved via live integration test");
        Complaint updated = complaintRepository.save(fetched.get());
        assertThat(updated.getStatus()).isEqualTo(ComplaintStatus.RESOLVED);

        // 4. Cleanup
        complaintRepository.deleteById(saved.getId());
    }
}
