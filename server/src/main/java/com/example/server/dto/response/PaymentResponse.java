package com.example.server.dto.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String donationId;   // mã donation từ backend
    private String orderId;    // mã đơn hàng từ MoMo
    private String payUrl;     // link tới cổng thanh toán MoMo
    private String qrCodeUrl;  // link hình QR code nếu có

    public PaymentResponse(String donationId, String orderId, String payUrl, String qrCodeUrl) {
        this.donationId = donationId;
        this.orderId = orderId;
        this.payUrl = payUrl;
        this.qrCodeUrl = qrCodeUrl;
    }
}
