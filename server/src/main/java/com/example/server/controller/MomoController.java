package com.example.server.controller;

import com.example.server.entity.Donation;
import com.example.server.service.DonationService;
import com.example.server.service.MomoService;
import com.example.server.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class MomoController {

    private final MomoService momoService;
    private final PaymentService paymentService;
    private final DonationService donationService;
    private static final String FRONTEND_URL = "http://localhost:5173";

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
     * Cập nhật payment_status ngay tại đây như một fallback nếu IPN chưa được gọi.
     */
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirect(@RequestParam Map<String, String> params) {
        System.out.println("========================================");
        System.out.println("[MOMO_CALLBACK] Vào /redirect với params: " + params);
        System.out.println("========================================");
        
        String resultCode = params.get("resultCode");
        String orderInfo = params.get("orderInfo");
        String extraData = params.get("extraData");
        String message = params.get("message");
        String orderId = params.get("orderId");

        System.out.println("[REDIRECT] Nhận redirect từ Momo: resultCode=" + resultCode + ", orderInfo=" + orderInfo + ", extraData=" + extraData);

        Long projectId = null;
        Long donationId = null;
        
        try {
            // Parse donationId từ extraData hoặc query theo orderId
            if (extraData != null && !extraData.isBlank()) {
                try {
                    donationId = Long.parseLong(extraData);
                    System.out.println("[REDIRECT] Lấy donationId từ extraData: " + donationId);
                } catch (NumberFormatException e) {
                    System.out.println("[REDIRECT] extraData không phải số hợp lệ: " + extraData);
                }
            }
            
            // Nếu không có trong extraData, query donation theo orderId
            if (donationId == null && orderId != null && !orderId.isBlank()) {
                try {
                    Donation donation = donationService.getDonationByOrderId(orderId);
                    if (donation != null) {
                        donationId = donation.getDonationId();
                        projectId = donation.getProjectId(); // Lấy luôn projectId từ donation
                        System.out.println("[REDIRECT] Lấy donationId từ orderId: " + donationId + ", projectId: " + projectId);
                    } else {
                        System.out.println("[REDIRECT] Không tìm thấy donation với orderId: " + orderId);
                    }
                } catch (Exception e) {
                    System.out.println("[REDIRECT] Lỗi khi query donation theo orderId: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Cập nhật payment_status nếu có donationId (fallback nếu IPN chưa được gọi)
            if (donationId != null && "0".equals(resultCode)) {
                try {
                    Donation updatedDonation = donationService.updatePaymentStatus(donationId, Donation.PaymentStatus.success);
                    if (updatedDonation != null) {
                        projectId = updatedDonation.getProjectId(); // Lấy projectId trực tiếp từ donation
                        System.out.println("[REDIRECT] Đã cập nhật donation " + donationId + " sang status: success (fallback)");
                        System.out.println("[REDIRECT] Lấy projectId từ donation: " + projectId);
                    } else {
                        System.out.println("[REDIRECT] Không tìm thấy donation với ID: " + donationId);
                    }
                } catch (Exception e) {
                    System.out.println("[REDIRECT] Lỗi khi cập nhật donation status: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Nếu vẫn chưa có projectId, parse từ orderInfo
            if (projectId == null) {
                projectId = extractProjectIdFromOrderInfo(orderInfo);
                if (projectId != null) {
                    System.out.println("[REDIRECT] Lấy projectId từ orderInfo: " + projectId);
                }
            }
        } catch (Exception e) {
            System.out.println("[REDIRECT] Lỗi xử lý: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Đảm bảo luôn redirect, dù có lỗi hay không
        String redirectUrl;
        try {
            if ("0".equals(resultCode) && projectId != null) {
                // Redirect đến trang chi tiết project nếu thanh toán thành công
                redirectUrl = FRONTEND_URL + "/projects/" + projectId + "?payment=success";
                System.out.println("[REDIRECT] Redirect đến trang chi tiết dự án: " + redirectUrl);
            } else {
                // Nếu thất bại hoặc không có projectId, redirect về danh sách project
                redirectUrl = FRONTEND_URL + "/projects?payment=" + 
                    ("0".equals(resultCode) ? "success" : "failed") + 
                    (message != null ? "&message=" + java.net.URLEncoder.encode(message, "UTF-8") : "");
                System.out.println("[REDIRECT] Redirect đến danh sách dự án: " + redirectUrl);
            }
        } catch (Exception e) {
            System.out.println("[REDIRECT] Lỗi khi xử lý redirect URL: " + e.getMessage());
            e.printStackTrace();
            redirectUrl = FRONTEND_URL + "/projects?payment=success";
        }
        
        // Sử dụng ResponseEntity với HttpStatus.FOUND để redirect
        System.out.println("[REDIRECT] Thực hiện redirect đến: " + redirectUrl);
        return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    /**
     * Extract projectId từ orderInfo
     * Format: "Ung ho du an {projectId}"
     */
    private Long extractProjectIdFromOrderInfo(String orderInfo) {
        if (orderInfo == null || orderInfo.isEmpty()) {
            return null;
        }
        
        // Pattern để tìm số cuối cùng trong orderInfo
        Pattern pattern = Pattern.compile("\\d+$");
        Matcher matcher = pattern.matcher(orderInfo.trim());
        
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    /**
     * IPN URL - MoMo server gọi trực tiếp -> cập nhật trạng thái donation
     * Đường dẫn này phải trùng với momo.ipnUrl trong properties.
     */
    @PostMapping("/ipn")
    public ResponseEntity<Map<String, Object>> ipn(@RequestBody Map<String, Object> body) {
        System.out.println("[MOMO_CALLBACK] Vào /ipn với body: " + body);
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
     * Verify payment từ frontend callback
     * Frontend gọi endpoint này sau khi nhận callback từ Momo
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> params) {
        System.out.println("[PAYMENT_VERIFY] Nhận request verify từ frontend: " + params);
        
        try {
            String resultCode = params.get("resultCode");
            String orderId = params.get("orderId");
            String extraData = params.get("extraData");
            String orderInfo = params.get("orderInfo");
            
            Long projectId = null;
            Long donationId = null;
            
            // Parse donationId từ extraData
            if (extraData != null && !extraData.isBlank()) {
                try {
                    donationId = Long.parseLong(extraData);
                    System.out.println("[PAYMENT_VERIFY] Lấy donationId từ extraData: " + donationId);
                } catch (NumberFormatException e) {
                    System.out.println("[PAYMENT_VERIFY] extraData không phải số hợp lệ: " + extraData);
                }
            }
            
            // Nếu không có trong extraData, query donation theo orderId
            if (donationId == null && orderId != null && !orderId.isBlank()) {
                try {
                    Donation donation = donationService.getDonationByOrderId(orderId);
                    if (donation != null) {
                        donationId = donation.getDonationId();
                        projectId = donation.getProjectId();
                        System.out.println("[PAYMENT_VERIFY] Lấy donationId từ orderId: " + donationId + ", projectId: " + projectId);
                    }
                } catch (Exception e) {
                    System.out.println("[PAYMENT_VERIFY] Lỗi khi query donation: " + e.getMessage());
                }
            }
            
            // Cập nhật payment_status nếu thanh toán thành công
            if (donationId != null && "0".equals(resultCode)) {
                try {
                    Donation updatedDonation = donationService.updatePaymentStatus(donationId, Donation.PaymentStatus.success);
                    if (updatedDonation != null) {
                        projectId = updatedDonation.getProjectId();
                        System.out.println("[PAYMENT_VERIFY] Đã cập nhật donation " + donationId + " sang status: success");
                    }
                } catch (Exception e) {
                    System.out.println("[PAYMENT_VERIFY] Lỗi khi cập nhật donation status: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Nếu vẫn chưa có projectId, parse từ orderInfo
            if (projectId == null) {
                projectId = extractProjectIdFromOrderInfo(orderInfo);
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "projectId", projectId != null ? projectId : 0,
                "donationId", donationId != null ? donationId : 0
            ));
            
        } catch (Exception e) {
            System.out.println("[PAYMENT_VERIFY] Lỗi: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Query trạng thái thanh toán (debug / đối soát)
     */
    @PostMapping("/query")
    public Map<String, Object> queryPayment(@RequestBody Map<String, Object> body,
                                            HttpServletResponse resp) throws Exception {
        String orderId = (String) body.get("orderId");
        String requestId = (String) body.get("requestId");
        return momoService.queryPayment(orderId, requestId);
    }
}
