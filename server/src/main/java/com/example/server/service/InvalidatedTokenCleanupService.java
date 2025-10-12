package com.example.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvalidatedTokenCleanupService {

    private final JdbcTemplate jdbcTemplate;


    /**
     * Hàm này sẽ tự động chạy mỗi 1 giờ
     * và xóa tất cả token đã hết hạn.
     */
    @Scheduled(fixedRate = 3600000) // 1 giờ = 3600000 ms
    public void cleanExpiredTokens() {
        jdbcTemplate.update(
                "DELETE FROM invalidated_token WHERE expired_time < CURRENT_TIMESTAMP"
        );
    }
}
