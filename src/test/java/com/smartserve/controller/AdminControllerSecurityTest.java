package com.smartserve.controller;

import com.smartserve.config.SecurityConfig;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller and Security tests verifying authentication barriers, role enforcement,
 * and admin dashboard operations.
 */
@WebMvcTest(controllers = {AdminController.class, AdminLoginController.class})
@Import(SecurityConfig.class)
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintService complaintService;

    @Test
    @DisplayName("Unauthenticated request to /admin/dashboard should redirect to /admin/login")
    void testUnauthenticatedAccessDenied() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Authenticated ADMIN should successfully access /admin/dashboard")
    void testAdminAccessAuthorized() throws Exception {
        when(complaintService.getAllComplaints()).thenReturn(Collections.emptyList());
        when(complaintService.getStatusMetrics()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"))
                .andExpect(model().attributeExists("complaints"))
                .andExpect(model().attributeExists("metrics"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Authenticated ADMIN should view complaint update page")
    void testViewComplaintDetail() throws Exception {
        String trackingId = "SMP-20261029-TEST";
        Complaint complaint = new Complaint(trackingId, "Pothole", ComplaintCategory.ROADS, "Deep pothole on road");
        when(complaintService.getComplaintByTrackingId(trackingId)).thenReturn(Optional.of(complaint));

        mockMvc.perform(get("/admin/complaints/" + trackingId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-update"))
                .andExpect(model().attributeExists("complaint"))
                .andExpect(model().attributeExists("updateForm"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Authenticated ADMIN should successfully update complaint status and redirect")
    void testUpdateStatusSuccess() throws Exception {
        String trackingId = "SMP-20261029-TEST";
        Complaint updated = new Complaint(trackingId, "Pothole", ComplaintCategory.ROADS, "Deep pothole on road");
        updated.setStatus(ComplaintStatus.IN_PROGRESS);

        when(complaintService.updateComplaintStatus(eq(trackingId), eq(ComplaintStatus.IN_PROGRESS), any()))
                .thenReturn(updated);

        mockMvc.perform(post("/admin/complaints/" + trackingId + "/status")
                        .with(csrf())
                        .param("status", "IN_PROGRESS")
                        .param("adminRemarks", "Road repair squad notified"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(complaintService, times(1)).updateComplaintStatus(eq(trackingId), eq(ComplaintStatus.IN_PROGRESS), any());
    }

    @Test
    @DisplayName("Unauthenticated POST to update status should be blocked with 403 or redirect to login")
    void testUnauthenticatedStatusUpdateBlocked() throws Exception {
        mockMvc.perform(post("/admin/complaints/SMP-20261029-TEST/status")
                        .with(csrf())
                        .param("status", "RESOLVED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));

        verify(complaintService, never()).updateComplaintStatus(any(), any(), any());
    }

    @Test
    @DisplayName("Public user can access /admin/login page without authentication")
    void testAdminLoginPagePublic() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-login"));
    }
}
