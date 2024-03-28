package com.F2C.jwt.mongodb.models;

import lombok.Data;

@Data
public class CropEarningsResponse {
    private String cropName;
    private Double earnings;
}
