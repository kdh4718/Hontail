package com.hontail.back.s3.service;

public interface S3Service {
    public String getPreSignedUrl(String fileName);
}
