package com.example.server.controller;

import com.example.server.service.MomoService;
import com.example.server.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class MomoController {

    private final MomoService momoService;
    private final PaymentService paymentService;

    public MomoController(MomoService momoService,
                          PaymentService paymentService) {
        this.momoService = momoService;
        this.paymentService = paymentService;
    }

    /**
     * Redirect URL sau khi user thanh toán xong trên trang MoMo
     * Đường dẫn này phải trùng với momo.redirectUrl trong properties.
     */
    @GetMapping("/redirect")
    public ResponseEntity<String> redirect(@RequestParam Map<String, String> params) {
        String resultCode = params.get("resultCode");
        String message = params.get("message");

        // Thực tế: nên redirect về FE, ví dụ: /donations/result?...
        String body = "Thanh toán MoMo: resultCode=" + resultCode + ", message=" + message;
        return ResponseEntity.ok(body);
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
    public Map<String, Object> queryPayment(@RequestBody Map<String, Object> body,
                                            HttpServletResponse resp) throws Exception {
        String orderId = (String) body.get("orderId");
        String requestId = (String) body.get("requestId");
        return momoService.queryPayment(orderId, requestId);
    }
}
