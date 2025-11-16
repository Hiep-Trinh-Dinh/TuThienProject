package com.example.server.service;

import com.example.server.config.momoConfig;
import com.example.server.dto.momo.CreateMomoRequest;
import com.example.server.dto.payment.CreateMomoResponse;
import com.example.server.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MomoService {

    private final momoConfig momoConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MomoService(momoConfig momoConfig) {
        this.momoConfig = momoConfig;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gọi API tạo giao dịch MoMo
     * @param amount    số tiền
     * @param orderId   mã đơn hàng nội bộ (ví dụ DONATION_3)
     * @param orderInfo nội dung hiển thị trên MoMo
     * @param extraData chuỗi phụ, ta sẽ nhét donationId vào đây
     */
    public CreateMomoResponse createPayment(long amount,
                                            String orderId,
                                            String orderInfo,
                                            String extraData) throws Exception {

        String requestId = UUID.randomUUID().toString();
        String requestType = "captureWallet";

        // rawSignature theo doc MoMo
        String rawSignature =
                "accessKey=" + momoConfig.getAccessKey() +
                        "&amount=" + amount +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + momoConfig.getIpnUrl() +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + momoConfig.getPartnerCode() +
                        "&redirectUrl=" + momoConfig.getRedirectUrl() +
                        "&requestId=" + requestId +
                        "&requestType=" + requestType;

        // Ký HMAC: secretKey, rawSignature
        String signature = CryptoUtil.hmacSHA256(momoConfig.getSecretKey(), rawSignature);

        // Build request body
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

        ResponseEntity<String> response = restTemplate.postForEntity(
                momoConfig.getEndpoint(),
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("MoMo API error: HTTP " + response.getStatusCodeValue());
        }

        return objectMapper.readValue(response.getBody(), CreateMomoResponse.class);
    }

    /**
     * Gọi API query trạng thái thanh toán (dùng khi cần đối soát)
     */
    public Map<String, Object> queryPayment(String orderId, String requestId) throws Exception {
        String rawSignature =
                "accessKey=" + momoConfig.getAccessKey() +
                        "&orderId=" + orderId +
                        "&partnerCode=" + momoConfig.getPartnerCode() +
                        "&requestId=" + requestId;

        String signature = CryptoUtil.hmacSHA256(momoConfig.getSecretKey(), rawSignature);

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", momoConfig.getPartnerCode());
        body.put("requestId", requestId);
        body.put("orderId", orderId);
        body.put("signature", signature);
        body.put("lang", "vi");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp = restTemplate.postForEntity(
                momoConfig.getQuery(),
                entity,
                Map.class
        );

        return resp.getBody();
    }
}
