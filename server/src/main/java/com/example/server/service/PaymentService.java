package com.example.server.service;

import com.example.server.config.momoConfig;
import com.example.server.dto.momo.CreateMomoRequest;
import com.example.server.dto.momo.CreateMomoResponse;
import com.example.server.dto.response.PaymentResponse;
import com.example.server.entity.Donation;
import com.example.server.util.CryptoUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final momoConfig momoConfig;
    private final RestTemplate restTemplate;
    private final DonationService donationService;

    public PaymentService(momoConfig momoConfig, DonationService donationService) {
        this.momoConfig = momoConfig;
        this.restTemplate = new RestTemplate();
        this.donationService = donationService;
    }

    /**
     * Khởi tạo thanh toán MoMo và trả về thông tin cho FE.
     * DonationController chịu trách nhiệm tạo Donation trong DB,
     * hàm này chỉ lo gọi MoMo và trả payUrl, orderId,...
     * @param amount Số tiền thanh toán
     * @param orderInfo Thông tin đơn hàng
     * @param donationId ID của donation để lưu vào extraData (có thể null)
     */
    public PaymentResponse createPayment(long amount, String orderInfo, Long donationId) throws Exception {
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String requestType = "captureWallet";
        // Lưu donationId vào extraData để sau này IPN có thể cập nhật status
        String extraData = donationId != null ? String.valueOf(donationId) : "";

        // rawSignature đúng format theo tài liệu MoMo
        String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                momoConfig.getAccessKey(),
                amount,
                extraData,
                momoConfig.getIpnUrl(),
                orderId,
                orderInfo,
                momoConfig.getPartnerCode(),
                momoConfig.getRedirectUrl(),
                requestId,
                requestType
        );

        // ký HMAC(secretKey, rawSignature)
        String signature = CryptoUtil.hmacSHA256(momoConfig.getSecretKey(), rawSignature);

        // build body request
        CreateMomoRequest request = new CreateMomoRequest();
        request.setPartnerCode(momoConfig.getPartnerCode());
        request.setAccessKey(momoConfig.getAccessKey());
        request.setRequestId(requestId);
        request.setAmount(String.valueOf(amount));
        request.setOrderId(orderId);
        request.setOrderInfo(orderInfo);
        request.setRedirectUrl(momoConfig.getRedirectUrl());
        request.setIpnUrl(momoConfig.getIpnUrl());
        request.setRequestType(requestType);
        request.setExtraData(extraData);
        request.setSignature(signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateMomoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateMomoResponse> response = restTemplate.postForEntity(
                momoConfig.getEndpoint(),
                entity,
                CreateMomoResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("MoMo API error: HTTP " + response.getStatusCode().value());
        }

        CreateMomoResponse momoRes = response.getBody();
        if (momoRes == null) {
            throw new RuntimeException("MoMo API trả về body rỗng");
        }

        if (momoRes.getResultCode() != 0) {
            throw new RuntimeException("MoMo tạo giao dịch thất bại: " + momoRes.getMessage());
        }

        // Map sang PaymentResponse để trả về FE
        return new PaymentResponse(
                null,                      // donationId sẽ được set ở DonationController
                momoRes.getOrderId(),
                momoRes.getPayUrl(),
                null                       // qrCodeUrl nếu sau này cần thêm vào CreateMomoResponse
        );
    }
    /**
     * Xử lý IPN (callback) từ MoMo.
     * Hiện tại hàm này chỉ:
     *  - đọc các field MoMo gửi
     *  - kiểm tra chữ ký (signature)
     *  - log ra console
     * Sau này khi bạn truyền donationId vào extraData, ta sẽ update trạng thái donation ở đây.
     */
    public void handleMomoIpn(Map<String, Object> body) {
        try {
            if (body == null) {
                System.out.println("[IPN] Body null, bỏ qua");
                return;
            }

            String partnerCode = (String) body.get("partnerCode");
            String orderId    = (String) body.get("orderId");
            String message    = (String) body.get("message");
            Object responseTimeObj = body.get("responseTime");
            Object resultCodeObj   = body.get("resultCode");
            String requestId  = (String) body.get("requestId");
            String extraData  = (String) body.get("extraData");
            String signature  = (String) body.get("signature");

            long responseTime = 0L;
            if (responseTimeObj != null) {
                if (responseTimeObj instanceof Number) {
                    responseTime = ((Number) responseTimeObj).longValue();
                } else {
                    responseTime = Long.parseLong(responseTimeObj.toString());
                }
            }

            int resultCode = 0;
            if (resultCodeObj != null) {
                if (resultCodeObj instanceof Number) {
                    resultCode = ((Number) resultCodeObj).intValue();
                } else {
                    resultCode = Integer.parseInt(resultCodeObj.toString());
                }
            }

            // Build raw string giống logic trong MomoController cũ
            String raw = "accessKey=" + momoConfig.getAccessKey() +
                    "&extraData=" + (extraData != null ? extraData : "") +
                    "&message=" + (message != null ? message : "") +
                    "&orderId=" + orderId +
                    "&partnerCode=" + partnerCode +
                    "&requestId=" + requestId +
                    "&responseTime=" + responseTime +
                    "&resultCode=" + resultCode;

            String expectedSignature = CryptoUtil.hmacSHA256(momoConfig.getSecretKey(), raw);

            if (signature == null || !expectedSignature.equals(signature)) {
                System.out.println("[IPN] Invalid signature cho orderId=" + orderId);
                return; // không xử lý tiếp
            }

            System.out.println("[IPN] IPN hợp lệ từ MoMo. orderId=" + orderId +
                    ", resultCode=" + resultCode +
                    ", extraData=" + extraData);

            // Cập nhật trạng thái donation dựa trên donationId trong extraData
            if (extraData != null && !extraData.isBlank()) {
                try {
                    Long donationId = Long.parseLong(extraData);
                    Donation.PaymentStatus status =
                            (resultCode == 0)
                                    ? Donation.PaymentStatus.success
                                    : Donation.PaymentStatus.failed;
                    
                    Donation updatedDonation = donationService.updatePaymentStatus(donationId, status);
                    if (updatedDonation != null) {
                        System.out.println("[IPN] Đã cập nhật donation " + donationId + " sang status: " + status);
                    } else {
                        System.out.println("[IPN] Không tìm thấy donation với ID: " + donationId);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[IPN] extraData không phải là số hợp lệ: " + extraData);
                } catch (Exception e) {
                    System.out.println("[IPN] Lỗi khi cập nhật donation status: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("[IPN] extraData rỗng, không thể cập nhật donation status");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
