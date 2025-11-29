package com.example.server.dto.response;

import java.math.BigDecimal;

public record GlobalStatsResponse(
        Long totalDonors,
        BigDecimal totalRaisedAmount
) {
}

