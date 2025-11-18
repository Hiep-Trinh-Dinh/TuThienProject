package com.example.server.dto.momo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateMomoResponse {

    private String orderId;
    private int resultCode;
    private String message;
    private String payUrl;
    private String qrCodeUrl;  // MoMo có trả trường này ở một số flow
}
