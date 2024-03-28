package com.F2C.jwt.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.F2C.jwt.mongodb.models.Images;


public interface ImageRepository extends MongoRepository<Images, String> {

}
