package com.example.server.controller;

import com.example.server.entity.Donation;
import com.example.server.service.DonationService;
import com.example.server.service.MomoService;
import com.example.server.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class MomoController {

    private final MomoService momoService;
    private final PaymentService paymentService;
    private final DonationService donationService;

    public MomoController(MomoService momoService,
                          PaymentService paymentService,
                          DonationService donationService) {
        this.momoService = momoService;
        this.paymentService = paymentService;
        this.donationService = donationService;
    }

    /**
     * Redirect URL sau khi user thanh toán xong trên trang MoMo
     * Đường dẫn này phải trùng với momo.redirectUrl trong properties.
     */
    @GetMapping("/redirect")
    public void redirect(@RequestParam Map<String, String> params,
                         HttpServletResponse response) throws IOException {

        String resultCode = params.get("resultCode");   // "0" nếu thành công
        String message    = params.get("message");
        String extraData  = params.get("extraData");    // donationId
        String orderInfo  = params.get("orderInfo");    // "Ung ho du an 6"

        System.out.println("[REDIRECT] resultCode=" + resultCode +
                ", message=" + message +
                ", extraData=" + extraData +
                ", orderInfo=" + orderInfo);

        // 1️⃣ Cập nhật trạng thái donation nếu có extraData (donationId)
        if (extraData != null && !extraData.isBlank()) {
            try {
                Long donationId = Long.parseLong(extraData);

                Donation.PaymentStatus status =
                        "0".equals(resultCode)
                                ? Donation.PaymentStatus.success
                                : Donation.PaymentStatus.failed;

                donationService.updatePaymentStatus(donationId, status);
                System.out.println("[REDIRECT] Update donationId=" + donationId + " -> " + status);
            } catch (NumberFormatException ex) {
                System.out.println("[REDIRECT] extraData không phải số: " + extraData);
            }
        }

        // 2️⃣ Lấy projectId từ orderInfo: "Ung ho du an 6"
        Long projectId = null;
        if (orderInfo != null) {
            try {
                String prefix = "Ung ho du an ";
                if (orderInfo.startsWith(prefix)) {
                    String idStr = orderInfo.substring(prefix.length()).trim();
                    projectId = Long.parseLong(idStr);
                }
            } catch (Exception e) {
                System.out.println("[REDIRECT] Không parse được projectId từ orderInfo=" + orderInfo);
            }
        }

        // 3️⃣ Xây URL frontend để redirect
        String feUrl;
        if (projectId != null) {
            // 👇 Trang dự án cụ thể
            feUrl = "http://localhost:5173/projects/" + projectId
                    + "?paymentResult=" + ("0".equals(resultCode) ? "success" : "failed")
                    + (extraData != null ? "&donationId=" + extraData : "");
        } else {
            // fallback: trang kết quả chung
            feUrl = "http://localhost:5173/payment-result"
                    + "?resultCode=" + (resultCode != null ? resultCode : "")
                    + (extraData != null ? "&donationId=" + extraData : "")
                    + (message != null ? "&message=" + message : "");
        }

        System.out.println("[REDIRECT] Redirect FE: " + feUrl);
        response.sendRedirect(feUrl);
    }

    /**
     * IPN URL - MoMo server gọi trực tiếp -> cập nhật trạng thái donation
     * Đường dẫn này phải trùng với momo.ipnUrl trong properties.
     */
    @PostMapping("/ipn")
    public ResponseEntity<Map<String, Object>> ipn(@RequestBody Map<String, Object> body) {
        try {
            System.out.println("[IPN] Nhận IPN: " + body);
            paymentService.handleMomoIpn(body);

            return ResponseEntity.ok(Map.of(
                    "resultCode", 0,
                    "message", "Thành công"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "resultCode", 500,
                    "message", "Lỗi xử lý IPN"
            ));
        }
    }

    /**
     * Query trạng thái thanh toán (debug / đối soát)
     */
    @PostMapping("/query")
    public Map<String, Object> queryPayment(@RequestBody Map<String, Object> body) throws Exception {
        String orderId = (String) body.get("orderId");
        String requestId = (String) body.get("requestId");
        return momoService.queryPayment(orderId, requestId);
    }
}
