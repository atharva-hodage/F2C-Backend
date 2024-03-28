package com.F2C.jwt.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "photos")
@Data
public class GeoPhoto {
    @Id
    private String id;
    private byte[] imageData;
    private double latitude;
    private double longitude;
}
