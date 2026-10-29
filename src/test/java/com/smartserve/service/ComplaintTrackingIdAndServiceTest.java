package com.smartserve.service;

import com.smartserve.dto.ComplaintRequestDto;
import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintCategory;
import com.smartserve.model.ComplaintStatus;
import com.smartserve.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests verifying core business logic, ID generation, and status transitions
 * independently of an external running MongoDB instance.
 */
@ExtendWith(MockitoExtension.class)
class ComplaintTrackingIdAndServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    private ComplaintServiceImpl complaintService;

    @BeforeEach
    void setUp() {
        complaintService = new ComplaintServiceImpl(complaintRepository);
    }

    @Test
    @DisplayName("Should generate collision-free Tracking ID matching SMP-YYYYMMDD-XXXX format")
    void testTrackingIdFormat() {
        when(complaintRepository.existsByTrackingId(anyString())).thenReturn(false);

        String trackingId = complaintService.generateUniqueTrackingId();

        assertThat(trackingId).isNotNull();
        assertThat(trackingId).matches("^SMP-\\d{8}-[2-9A-HJ-NP-Z]{4}$");
    }

    @Test
    @DisplayName("Should persist new complaint with initial SUBMITTED status and timestamps")
    void testCreateComplaintSuccess() {
        when(complaintRepository.existsByTrackingId(anyString())).thenReturn(false);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ComplaintRequestDto dto = new ComplaintRequestDto(
                "Burst water pipe on MG Road",
                ComplaintCategory.WATER,
                "Clean water leaking continuously near metro pillar 42."
        );

        Complaint result = complaintService.createComplaint(dto);

        assertThat(result).isNotNull();
        assertThat(result.getTrackingId()).startsWith("SMP-");
        assertThat(result.getTitle()).isEqualTo("Burst water pipe on MG Road");
        assertThat(result.getCategory()).isEqualTo(ComplaintCategory.WATER);
        assertThat(result.getDescription()).contains("Clean water leaking");
        assertThat(result.getStatus()).isEqualTo(ComplaintStatus.SUBMITTED);
        assertThat(result.getSubmissionTime()).isNotNull();
        assertThat(result.getLastUpdatedTime()).isNotNull();

        verify(complaintRepository, times(1)).save(any(Complaint.class));
    }

    @Test
    @DisplayName("Should update complaint status and append administrative remarks")
    void testUpdateComplaintStatusSuccess() {
        String trackingId = "SMP-20261029-TEST";
        Complaint existing = new Complaint(trackingId, "Flickering street light", ComplaintCategory.ELECTRICITY, "Near house 10");
        existing.setStatus(ComplaintStatus.SUBMITTED);

        when(complaintRepository.findByTrackingId(trackingId)).thenReturn(Optional.of(existing));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Complaint updated = complaintService.updateComplaintStatus(trackingId, ComplaintStatus.IN_PROGRESS, "Field electrician dispatched");

        assertThat(updated.getStatus()).isEqualTo(ComplaintStatus.IN_PROGRESS);
        assertThat(updated.getAdminRemarks()).isEqualTo("Field electrician dispatched");
        assertThat(updated.getLastUpdatedTime()).isNotNull();

        ArgumentCaptor<Complaint> captor = ArgumentCaptor.forClass(Complaint.class);
        verify(complaintRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ComplaintStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating non-existent complaint")
    void testUpdateStatusNotFound() {
        when(complaintRepository.findByTrackingId(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                complaintService.updateComplaintStatus("SMP-99999999-NONE", ComplaintStatus.RESOLVED, "Done")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should uppercase input tracking ID when retrieving complaint")
    void testGetComplaintByTrackingIdCaseInsensitive() {
        Complaint complaint = new Complaint("SMP-20261029-ABCD", "Pothole", ComplaintCategory.ROADS, "Pothole on 5th cross");
        when(complaintRepository.findByTrackingId("SMP-20261029-ABCD")).thenReturn(Optional.of(complaint));

        Optional<Complaint> result = complaintService.getComplaintByTrackingId("smp-20261029-abcd");

        assertThat(result).isPresent();
        assertThat(result.get().getTrackingId()).isEqualTo("SMP-20261029-ABCD");
        verify(complaintRepository).findByTrackingId("SMP-20261029-ABCD");
    }

    @Test
    @DisplayName("Should aggregate correct status metrics for admin dashboard")
    void testGetStatusMetrics() {
        when(complaintRepository.countByStatus(ComplaintStatus.SUBMITTED)).thenReturn(5L);
        when(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS)).thenReturn(3L);
        when(complaintRepository.countByStatus(ComplaintStatus.RESOLVED)).thenReturn(8L);
        when(complaintRepository.countByStatus(ComplaintStatus.REJECTED)).thenReturn(1L);

        Map<String, Long> metrics = complaintService.getStatusMetrics();

        assertThat(metrics.get("TOTAL")).isEqualTo(17L);
        assertThat(metrics.get("SUBMITTED")).isEqualTo(5L);
        assertThat(metrics.get("IN_PROGRESS")).isEqualTo(3L);
        assertThat(metrics.get("RESOLVED")).isEqualTo(8L);
        assertThat(metrics.get("REJECTED")).isEqualTo(1L);
    }
}
