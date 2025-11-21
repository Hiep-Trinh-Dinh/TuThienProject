package com.example.server.dto.response;

import java.math.BigDecimal;

public record ProjectStatsResponse(
        long totalProjects,
        long totalPending,
        long totalActive,
        BigDecimal totalRaisedAmount
) {
    // Constructor for JPA projection
    public ProjectStatsResponse(Long totalProjects, Long totalPending,
                                Long totalActive, BigDecimal totalRaisedAmount) {
        this(totalProjects != null ? totalProjects : 0L,
                totalPending != null ? totalPending : 0L,
                totalActive != null ? totalActive : 0L,
                totalRaisedAmount != null ? totalRaisedAmount : BigDecimal.ZERO);
    }
}
