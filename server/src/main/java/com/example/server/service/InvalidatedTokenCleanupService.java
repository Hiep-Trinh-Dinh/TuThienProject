package com.example.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InvalidatedTokenCleanupService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Hàm này sẽ tự động chạy mỗi 1 giờ
     * và xóa tất cả token đã hết hạn.
     */
    @Scheduled(fixedRate = 3600000) // 1 giờ = 3600000 ms
    public void cleanExpiredTokens() {
        int rows = jdbcTemplate.update(
                "DELETE FROM invalidated_token WHERE expired_time < CURRENT_TIMESTAMP"
        );
        System.out.println("🧹 Dọn dẹp token hết hạn: " + rows + " bản ghi đã bị xóa.");
    }
}
