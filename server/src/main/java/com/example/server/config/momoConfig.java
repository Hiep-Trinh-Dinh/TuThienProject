package com.example.server.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class momoConfig {

    @Value("${momo.partnercode}")
    private String partnerCode;

    @Value("${momo.accesskey}")
    private String accessKey;

    @Value("${momo.secretkey}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirecturl}")
    private String redirectUrl;

    @Value("${momo.ipnurl}")
    private String ipnUrl;

    @Value("${momo.query}")
    private String query;


    // Getters
    public String getPartnerCode() { return partnerCode; }
    public String getAccessKey() { return accessKey; }
    public String getSecretKey() { return secretKey; }
    public String getEndpoint() { return endpoint; }
    public String getRedirectUrl() { return redirectUrl; }
    public String getIpnUrl() { return ipnUrl; }
    public String getQuery() { return query; }
}

