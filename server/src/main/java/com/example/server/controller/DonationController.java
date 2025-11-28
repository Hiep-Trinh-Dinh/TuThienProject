package com.example.server.controller;

import com.example.server.dto.DonationDTO;
import com.example.server.dto.response.GlobalStatsResponse;
import com.example.server.dto.response.PaymentResponse;
import com.example.server.entity.Donation;
import com.example.server.service.DonationReportService;
import com.example.server.service.DonationService;
import com.example.server.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationService donationService;
    private final PaymentService paymentService;
    private final DonationReportService donationReportService;

    @Autowired
    public DonationController(DonationService donationService, PaymentService paymentService, DonationReportService donationReportService) {
        this.donationService = donationService;
        this.paymentService = paymentService;
        this.donationReportService = donationReportService;
    }
    // Tạo donation mới
//    @PostMapping
//    public ResponseEntity<DonationDTO> createDonation(@RequestBody DonationDTO donationDTO) {
//        try {
//            Donation donation = donationDTO.toEntity();
//            Donation savedDonation = donationService.createDonation(donation);
//            return ResponseEntity.ok(DonationDTO.fromEntity(savedDonation));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }


    //Tạo Donation mới -v2
    @PostMapping
    public ResponseEntity<PaymentResponse> createDonation(@RequestBody DonationDTO dto) {
        try {
            System.out.println("[CREATE_DONATION] Tạo donation đúng ");

            System.out.println("[LOG] Nhận request tạo donation.");
            Donation donation = donationService.createDonation(dto.toEntity());
            String OrderInfo = "Ung ho du an " + donation.getProjectId();
            System.out.println("[LOG] Đã tạo donation: id=" + donation.getDonationId());
            if ("momo".equalsIgnoreCase(dto.getPaymentMethod())) {
                // Gọi service tạo payment, truyền donationId vào extraData để IPN có thể cập nhật status
                PaymentResponse momoRes = paymentService.createPayment(
                    donation.getAmount().longValue(), 
                    OrderInfo, 
                    donation.getDonationId()
                );
                System.out.println("[LOG] Đã gọi Momo, nhận orderId=" + momoRes.getOrderId());

                // Lưu orderId vào donation để có thể query lại khi redirect
                donation.setOrderId(momoRes.getOrderId());
                donationService.updateDonationOrderId(donation.getDonationId(), momoRes.getOrderId());
                System.out.println("[LOG] Đã lưu orderId=" + momoRes.getOrderId() + " vào donation " + donation.getDonationId());

                // Gắn donationId vào response để FE có thể sử dụng
                momoRes.setDonationId(String.valueOf(donation.getDonationId()));
                System.out.println("[THÔNG BÁO] Đã lưu xong donation, đơn hàng Momo: orderId=" + momoRes.getOrderId());
                System.out.println("[LOG] Luồng đã tới bước lưu vào DB, donationId=" + donation.getDonationId() + " đã được lưu vào extraData và orderId");
                return ResponseEntity.ok(momoRes);

            }
            // Xử lý với paymentMethod khác nếu có...
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body((PaymentResponse) Map.of("message", "Request không hợp lệ hoặc thiếu trường"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy tất cả donations với pagination
    @GetMapping
    public ResponseEntity<Page<DonationDTO>> getAllDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        // Lấy tất cả donations (mọi trạng thái) để trang quản trị xem đầy đủ sao kê
        Page<Donation> donations = donationService.getAllDonations(pageable);
        Page<DonationDTO> donationDTOs = donations.map(DonationDTO::fromEntity);
        return ResponseEntity.ok(donationDTOs);
    }

    @GetMapping("/allnoPaging")
    public ResponseEntity<List<DonationDTO>> getAllDonationsNoPaging() {
        List<Donation> donations = donationService.getAllDonations();
        List<DonationDTO> result = donations.stream().map(DonationDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }


    // Lấy donations theo project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<DonationDTO>> getDonationsByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Donation> donations = donationService.getDonationsByProject(projectId, pageable);
        Page<DonationDTO> donationDTOs = donations.map(DonationDTO::fromEntity);
        return ResponseEntity.ok(donationDTOs);
    }

    // Lấy donations theo donor
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<Page<DonationDTO>> getDonationsByDonor(
            @PathVariable Long donorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Donation> donations = donationService.getDonationsByDonor(donorId, pageable);
        Page<DonationDTO> donationDTOs = donations.map(DonationDTO::fromEntity);
        return ResponseEntity.ok(donationDTOs);
    }

    // Lấy donations theo donor ko paging
    @GetMapping("/{donorId}")
    public ResponseEntity<List<DonationDTO>> getDonationsByDonor(
            @PathVariable Long donorId) {

        List<Donation> donations = donationService.getDonationsByDonor(donorId);
        List<DonationDTO> donationDTOs = donations.stream()
                .map(DonationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(donationDTOs);
    }

    // Cập nhật payment status
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<DonationDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            Donation.PaymentStatus paymentStatus = Donation.PaymentStatus.valueOf(status.toLowerCase());
            Donation updatedDonation = donationService.updatePaymentStatus(id, paymentStatus);

            if (updatedDonation != null) {
                return ResponseEntity.ok(DonationDTO.fromEntity(updatedDonation));
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/admin/donations/{id}/status")
    public ResponseEntity<DonationDTO> updatePaymentStatusAlias(
            @PathVariable Long id, 
            @RequestParam String status) {
        // Gọi lại logic cũ để tránh duplicate code
        return updatePaymentStatus(id, status);
    }

    // Thống kê tổng donations của project
    @GetMapping("/project/{projectId}/total")
    public ResponseEntity<BigDecimal> getTotalDonationsByProject(@PathVariable Long projectId) {
        BigDecimal total = donationService.getTotalDonationsByProject(projectId);
        return ResponseEntity.ok(total);
    }

    // Thống kê số donor của project
    @GetMapping("/project/{projectId}/donor-count")
    public ResponseEntity<Long> getDonorCountByProject(@PathVariable Long projectId) {
        Long count = donationService.countDonorsByProject(projectId);
        return ResponseEntity.ok(count);
    }

    // Thống kê số project của donor
    @GetMapping("/donor/{donorId}/project-count")
    public ResponseEntity<Long> getProjectCountByDonor(@PathVariable Long donorId) {
        Long count = donationService.countProjectsByDonor(donorId);
        return ResponseEntity.ok(count);
    }

    // Thống kê tổng donations của donor
    @GetMapping("/donor/{donorId}/total")
    public ResponseEntity<BigDecimal> getTotalDonationsByDonor(@PathVariable Long donorId) {
        BigDecimal total = donationService.getTotalDonationsByDonor(donorId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/stats/global")
    public ResponseEntity<GlobalStatsResponse> getGlobalStats() {
        return ResponseEntity.ok(donationService.getGlobalStats());
    }

    // Xóa donation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        try {
            donationService.deleteDonation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Lấy donations gần nhất
    @GetMapping("/recent")
    public ResponseEntity<List<DonationDTO>> getRecentDonations(
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        Page<Donation> donations = donationService.getRecentDonations(pageable);
        List<DonationDTO> donationDTOs = donations.getContent().stream()
                .map(DonationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(donationDTOs);
    }

    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> exportDonationsExcel() {
        try {
            byte[] excelBytes = donationReportService.exportDonationsToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=donations_report.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheets");
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
