package com.example.server.dto.momo;

import lombok.Data;

@Data
public class MomoIpnRequest {

    private String partnerCode;
    private String orderId;
    private String requestId;
    private long amount;
    private long transId;
    private int resultCode;
    private String message;
    private String payType;
    private long responseTime;
    private String extraData;
    private String signature;
}
