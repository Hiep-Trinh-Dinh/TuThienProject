package com.example.server.service;

import com.example.server.config.momoConfig;
import com.example.server.dto.momo.CreateMomoResponse;
import com.example.server.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MomoServiceTest {
    private momoConfig momoConfig;
    private RestTemplate restTemplate;
    private MomoService momoService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        momoConfig = mock(momoConfig.class);
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        momoService = new MomoService(momoConfig);
        try {
            java.lang.reflect.Field restTemplateField = MomoService.class.getDeclaredField("restTemplate");
            restTemplateField.setAccessible(true);
            restTemplateField.set(momoService, restTemplate);
            java.lang.reflect.Field objectMapperField = MomoService.class.getDeclaredField("objectMapper");
            objectMapperField.setAccessible(true);
            objectMapperField.set(momoService, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createPayment_Success() throws Exception {
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getIpnUrl()).thenReturn("http://ipn");
        when(momoConfig.getRedirectUrl()).thenReturn("http://redirect");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getEndpoint()).thenReturn("http://momo-endpoint");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("signature");
        CreateMomoResponse response = new CreateMomoResponse();
        response.setResultCode(0);
        response.setOrderId("OID");
        response.setPayUrl("PAY_URL");
        String jsonResponse = objectMapper.writeValueAsString(response);
        ResponseEntity<String> entity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(entity);
        CreateMomoResponse result = momoService.createPayment(1000, "OID", "info", "extra");
        assertThat(result.getResultCode()).isEqualTo(0);
        assertThat(result.getPayUrl()).isEqualTo("PAY_URL");
        util.close();
    }

    @Test
    void queryPayment_Success() throws Exception {
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getQuery()).thenReturn("http://query-endpoint");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("signature");
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("resultCode", 0);
        ResponseEntity<Map> entity = new ResponseEntity<>(responseMap, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(entity);
        Map<String, Object> result = momoService.queryPayment("OID", "RID");
        assertThat(result).isNotNull();
        assertThat(result.get("resultCode")).isEqualTo(0);
        util.close();
    }

    @Test
    void createPayment_WithExtraData_Success() throws Exception {
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getIpnUrl()).thenReturn("http://ipn");
        when(momoConfig.getRedirectUrl()).thenReturn("http://redirect");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getEndpoint()).thenReturn("http://momo-endpoint");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("signature");
        CreateMomoResponse response = new CreateMomoResponse();
        response.setResultCode(0);
        response.setOrderId("OID123");
        response.setPayUrl("PAY_URL");
        String jsonResponse = objectMapper.writeValueAsString(response);
        ResponseEntity<String> entity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(entity);
        CreateMomoResponse result = momoService.createPayment(1000, "OID123", "info", "extra-data");
        assertThat(result.getResultCode()).isEqualTo(0);
        assertThat(result.getPayUrl()).isEqualTo("PAY_URL");
        util.close();
    }

    @Test
    void createPayment_HttpError_ThrowsException() throws Exception {
        when(momoConfig.getAccessKey()).thenReturn("key");
        when(momoConfig.getPartnerCode()).thenReturn("par");
        when(momoConfig.getIpnUrl()).thenReturn("http://ipn");
        when(momoConfig.getRedirectUrl()).thenReturn("http://redirect");
        when(momoConfig.getSecretKey()).thenReturn("secret");
        when(momoConfig.getEndpoint()).thenReturn("http://momo-endpoint");
        MockedStatic<CryptoUtil> util = mockStatic(CryptoUtil.class);
        util.when(() -> CryptoUtil.hmacSHA256(anyString(), anyString())).thenReturn("signature");
        ResponseEntity<String> entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(entity);
        assertThatThrownBy(() -> momoService.createPayment(1000, "OID", "info", "extra"))
            .isInstanceOf(RuntimeException.class);
        util.close();
    }
}

