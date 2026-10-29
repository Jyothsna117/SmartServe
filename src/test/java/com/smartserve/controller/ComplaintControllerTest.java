package com.smartserve.controller;

import com.smartserve.config.SecurityConfig;
import com.smartserve.dto.ComplaintRequestDto;
import com.smartserve.model.Complaint;
import com.smartserve.model.ComplaintCategory;
import com.smartserve.model.ComplaintStatus;
import com.smartserve.service.ComplaintService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for public citizen complaint submission and tracking flows.
 */
@WebMvcTest(ComplaintController.class)
@Import(SecurityConfig.class)
class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintService complaintService;

    @Test
    @DisplayName("GET /complaints/new should return submission form view with categories")
    void testShowComplaintForm() throws Exception {
        mockMvc.perform(get("/complaints/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("submit-complaint"))
                .andExpect(model().attributeExists("complaintForm"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("POST /complaints with valid data should save complaint and redirect to success page")
    void testSubmitComplaintSuccess() throws Exception {
        Complaint savedComplaint = new Complaint("SMP-20261029-ABCD", "Broken pipe", ComplaintCategory.WATER, "Water leak near bus stop");
        when(complaintService.createComplaint(any(ComplaintRequestDto.class))).thenReturn(savedComplaint);

        mockMvc.perform(post("/complaints")
                        .with(csrf())
                        .param("title", "Broken pipe near bus stop")
                        .param("category", "WATER")
                        .param("description", "Large water pipe leakage causing flooding on road."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/complaints/success"))
                .andExpect(flash().attribute("trackingId", "SMP-20261029-ABCD"));

        verify(complaintService, times(1)).createComplaint(any(ComplaintRequestDto.class));
    }

    @Test
    @DisplayName("POST /complaints with validation errors should redisplay submission form")
    void testSubmitComplaintValidationError() throws Exception {
        mockMvc.perform(post("/complaints")
                        .with(csrf())
                        .param("title", "") // empty title violates validation
                        .param("description", "Too short"))
                .andExpect(status().isOk())
                .andExpect(view().name("submit-complaint"))
                .andExpect(model().hasErrors());

        verify(complaintService, never()).createComplaint(any());
    }

    @Test
    @DisplayName("GET /complaints/track with valid Tracking ID should display complaint details")
    void testTrackComplaintFound() throws Exception {
        String trackingId = "SMP-20261029-WTR1";
        Complaint complaint = new Complaint(trackingId, "Pipeline leak", ComplaintCategory.WATER, "Continuous water leak");
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        complaint.setAdminRemarks("Plumber assigned");

        when(complaintService.getComplaintByTrackingId(trackingId)).thenReturn(Optional.of(complaint));

        mockMvc.perform(get("/complaints/track").param("trackingId", trackingId))
                .andExpect(status().isOk())
                .andExpect(view().name("track-complaint"))
                .andExpect(model().attributeExists("complaint"))
                .andExpect(model().attribute("searchedId", trackingId));
    }

    @Test
    @DisplayName("GET /complaints/track with unknown Tracking ID should display not found warning")
    void testTrackComplaintNotFound() throws Exception {
        String trackingId = "SMP-00000000-NONE";
        when(complaintService.getComplaintByTrackingId(trackingId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/complaints/track").param("trackingId", trackingId))
                .andExpect(status().isOk())
                .andExpect(view().name("track-complaint"))
                .andExpect(model().attribute("notFound", true))
                .andExpect(model().attribute("searchedId", trackingId));
    }
}
