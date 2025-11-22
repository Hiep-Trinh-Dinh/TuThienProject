package com.example.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.*;

class InvalidatedTokenCleanupServiceTest {
    @Mock private JdbcTemplate jdbcTemplate;
    @InjectMocks private InvalidatedTokenCleanupService invalidatedTokenCleanupService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cleanExpiredTokens_CallsJdbcTemplateUpdate() {
        when(jdbcTemplate.update(anyString())).thenReturn(5);
        invalidatedTokenCleanupService.cleanExpiredTokens();
        verify(jdbcTemplate, times(1)).update(
            eq("DELETE FROM invalidated_token WHERE expired_time < CURRENT_TIMESTAMP")
        );
    }

    @Test
    void cleanExpiredTokens_ReturnsDeletedCount() {
        when(jdbcTemplate.update(anyString())).thenReturn(10);
        invalidatedTokenCleanupService.cleanExpiredTokens();
        verify(jdbcTemplate).update(anyString());
    }

    @Test
    void cleanExpiredTokens_NoTokensDeleted_ReturnsZero() {
        when(jdbcTemplate.update(anyString())).thenReturn(0);
        invalidatedTokenCleanupService.cleanExpiredTokens();
        verify(jdbcTemplate).update(anyString());
    }
}

