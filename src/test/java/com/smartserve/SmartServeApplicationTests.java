package com.smartserve;

import com.smartserve.repository.ComplaintRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring context verification test.
 * Repository is mocked to allow clean CI builds and local unit testing
 * independent of an active MongoDB socket connection.
 */
@SpringBootTest
@ActiveProfiles("test")
class SmartServeApplicationTests {

    @MockBean
    private ComplaintRepository complaintRepository;

    @Test
    @DisplayName("Spring Application Context loads successfully")
    void contextLoads() {
        assertThat(complaintRepository).isNotNull();
    }
}
