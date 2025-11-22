package com.example.server.service;

import com.example.server.config.momoConfig;
import com.example.server.dto.momo.CreateMomoRequest;
import com.example.server.dto.momo.CreateMomoResponse;
import com.example.server.dto.response.PaymentResponse;
import com.example.server.entity.Donation;
import com.example.server.util.CryptoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock momoConfig momoConfig;
    @Mock DonationService donationService;
    @Mock RestTemplate restTemplate;
    @InjectMocks PaymentService paymentService;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentService(momoConfig, donationService);
        // replace real RestTemplate for mock
        java.lang.reflect.Field restTemplateField = paymentService.getClass().getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(paymentService, restTemplate);
    }

    @Test
    void createPayment_success() throws Exception {
        // config mock
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getIpnUrl()).thenReturn("http://ipn");
        when(momoConfig.getRedirectUrl()).thenReturn("http://redirect");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getEndpoint()).thenReturn("http://momo-endpoint");
        // mock hmac
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("signature");
        // mock momo response
        CreateMomoResponse momoResp = new CreateMomoResponse();
        momoResp.setResultCode(0);
        momoResp.setMessage("OK");
        momoResp.setOrderId("OID");
        momoResp.setPayUrl("PAY_URL");
        ResponseEntity<CreateMomoResponse> entity = new ResponseEntity<>(momoResp, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(CreateMomoResponse.class))).thenReturn(entity);
        PaymentResponse paymentResponse = paymentService.createPayment(1000, "order", 1L);
        assertThat(paymentResponse.getPayUrl()).isEqualTo("PAY_URL");
        util.close();
    }

    @Test
    void createPayment_httpErrorRuntimeException() throws Exception {
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getIpnUrl()).thenReturn("http://ipn");
        when(momoConfig.getRedirectUrl()).thenReturn("http://redirect");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getEndpoint()).thenReturn("http://momo-endpoint");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("sig");
        ResponseEntity<CreateMomoResponse> entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(CreateMomoResponse.class))).thenReturn(entity);
        assertThatThrownBy(() -> paymentService.createPayment(1000, "info", null)).isInstanceOf(RuntimeException.class);
        util.close();
    }

    @Test
    void createPayment_nullBodyThrow() throws Exception {
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getIpnUrl()).thenReturn("http://ipn");
        when(momoConfig.getRedirectUrl()).thenReturn("http://redirect");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getEndpoint()).thenReturn("http://momo-endpoint");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("sig");
        ResponseEntity<CreateMomoResponse> entity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(CreateMomoResponse.class))).thenReturn(entity);
        assertThatThrownBy(() -> paymentService.createPayment(1000, "info", null)).isInstanceOf(RuntimeException.class);
        util.close();
    }

    @Test
    void createPayment_errorResultCodeThrow() throws Exception {
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getIpnUrl()).thenReturn("http://ipn");
        when(momoConfig.getRedirectUrl()).thenReturn("http://redirect");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getEndpoint()).thenReturn("http://momo-endpoint");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("sig");
        CreateMomoResponse momoResp = new CreateMomoResponse();
        momoResp.setResultCode(1);
        momoResp.setMessage("ERR");
        ResponseEntity<CreateMomoResponse> entity = new ResponseEntity<>(momoResp, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(CreateMomoResponse.class))).thenReturn(entity);
        assertThatThrownBy(() -> paymentService.createPayment(1000, "info", null)).isInstanceOf(RuntimeException.class);
        util.close();
    }

    @Test
    void handleMomoIpn_validSignatureAndSuccess() {
        Map<String,Object> req = new HashMap<>();
        req.put("partnerCode", "xxx");
        req.put("orderId", "oid");
        req.put("message", "msg");
        req.put("responseTime", 12345L);
        req.put("resultCode", 0);
        req.put("requestId", "rrrr");
        req.put("extraData", "101");
        req.put("signature", "good");
        when(momoConfig.getAccessKey()).thenReturn("acc");
        when(momoConfig.getSecretKey()).thenReturn("sec");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("good");
        Donation donation = new Donation();
        when(donationService.updatePaymentStatus(anyLong(), any())).thenReturn(donation);
        paymentService.handleMomoIpn(req);
        util.close();
    }

    @Test
    void handleMomoIpn_invalidSignature() {
        Map<String,Object> req = new HashMap<>();
        req.put("signature", "bad");
        when(momoConfig.getAccessKey()).thenReturn("a");
        when(momoConfig.getSecretKey()).thenReturn("b");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("sig");
        paymentService.handleMomoIpn(req);
        util.close();
    }

    @Test
    void handleMomoIpn_nullBody_doesNothing() {
        paymentService.handleMomoIpn(null);
        verify(donationService, never()).updatePaymentStatus(anyLong(), any());
    }

    @Test
    void handleMomoIpn_emptyExtraData_doesNotUpdateDonation() {
        Map<String,Object> req = new HashMap<>();
        req.put("partnerCode", "xxx");
        req.put("orderId", "oid");
        req.put("message", "msg");
        req.put("responseTime", 12345L);
        req.put("resultCode", 0);
        req.put("requestId", "rrrr");
        req.put("extraData", "");
        req.put("signature", "good");
        when(momoConfig.getAccessKey()).thenReturn("acc");
        when(momoConfig.getSecretKey()).thenReturn("sec");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("good");
        paymentService.handleMomoIpn(req);
        verify(donationService, never()).updatePaymentStatus(anyLong(), any());
        util.close();
    }

    @Test
    void handleMomoIpn_invalidExtraData_doesNotUpdateDonation() {
        Map<String,Object> req = new HashMap<>();
        req.put("partnerCode", "xxx");
        req.put("orderId", "oid");
        req.put("message", "msg");
        req.put("responseTime", 12345L);
        req.put("resultCode", 0);
        req.put("requestId", "rrrr");
        req.put("extraData", "not-a-number");
        req.put("signature", "good");
        when(momoConfig.getAccessKey()).thenReturn("acc");
        when(momoConfig.getSecretKey()).thenReturn("sec");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("good");
        paymentService.handleMomoIpn(req);
        verify(donationService, never()).updatePaymentStatus(anyLong(), any());
        util.close();
    }

    @Test
    void handleMomoIpn_resultCodeFailed_updatesToFailed() {
        Map<String,Object> req = new HashMap<>();
        req.put("partnerCode", "xxx");
        req.put("orderId", "oid");
        req.put("message", "msg");
        req.put("responseTime", 12345L);
        req.put("resultCode", 1);
        req.put("requestId", "rrrr");
        req.put("extraData", "101");
        req.put("signature", "good");
        when(momoConfig.getAccessKey()).thenReturn("acc");
        when(momoConfig.getSecretKey()).thenReturn("sec");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("good");
        Donation donation = new Donation();
        when(donationService.updatePaymentStatus(anyLong(), eq(Donation.PaymentStatus.failed))).thenReturn(donation);
        paymentService.handleMomoIpn(req);
        verify(donationService).updatePaymentStatus(eq(101L), eq(Donation.PaymentStatus.failed));
        util.close();
    }

    @Test
    void handleMomoIpn_responseTimeAsNumber() {
        Map<String,Object> req = new HashMap<>();
        req.put("partnerCode", "xxx");
        req.put("orderId", "oid");
        req.put("message", "msg");
        req.put("responseTime", 12345);
        req.put("resultCode", 0);
        req.put("requestId", "rrrr");
        req.put("extraData", "101");
        req.put("signature", "good");
        when(momoConfig.getAccessKey()).thenReturn("acc");
        when(momoConfig.getSecretKey()).thenReturn("sec");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("good");
        Donation donation = new Donation();
        when(donationService.updatePaymentStatus(anyLong(), any())).thenReturn(donation);
        paymentService.handleMomoIpn(req);
        util.close();
    }
}
