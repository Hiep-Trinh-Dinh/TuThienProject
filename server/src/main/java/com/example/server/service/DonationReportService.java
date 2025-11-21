package com.example.server.service;

import com.example.server.entity.Donation;
import com.example.server.repository.DonationsRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.sf.jasperreports.engine.export.JRXlsxExporter; // KHÔNG CẦN import này với jasperreports 6.21+
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

@Service
public class DonationReportService {
    @Autowired
    private DonationsRepository donationRepository;

    public byte[] exportDonationsToExcel() throws Exception {
        List<Donation> donations = donationRepository.findAll();
        // Chuyển đổi Enum sang String để JasperReports có thể xử lý
        List<Map<String, Object>> reportData = donations.stream().map(donation -> {
            Map<String, Object> row = new HashMap<>();
            row.put("donationId", donation.getDonationId());
            row.put("donorId", donation.getDonorId());
            row.put("projectId", donation.getProjectId());
            row.put("amount", donation.getAmount());
            row.put("paymentMethod", donation.getPaymentMethod() != null ? donation.getPaymentMethod().toString() : "");
            row.put("paymentStatus", donation.getPaymentStatus() != null ? donation.getPaymentStatus().toString() : "");
            row.put("donatedAt", donation.getDonatedAt());
            return row;
        }).toList();
        // Tải template jrxml
        InputStream templateStream = getClass().getResourceAsStream("/donation_report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
        Map<String, Object> params = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Đường import đúng từ jasperreports-core (export.ooxml)
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        exporter.setConfiguration(configuration);
        exporter.exportReport();
        return baos.toByteArray();
    }
}
