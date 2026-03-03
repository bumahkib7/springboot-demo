package dev.runixcloud.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {

    private final S3Client s3;
    private final String bucket;

    public StorageService(S3Client s3, @Value("${s3.bucket}") String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    public byte[] download(String key) {
        try {
            return s3.getObject(GetObjectRequest.builder()
                    .bucket(bucket).key(key).build())
                    .readAllBytes();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to download file: " + key, e);
        }
    }

    public void delete(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket).key(key).build());
    }

    public List<String> listFiles() {
        try {
            ListObjectsV2Response res = s3.listObjectsV2(
                    ListObjectsV2Request.builder().bucket(bucket).build());
            List<String> keys = new ArrayList<>();
            for (S3Object obj : res.contents()) {
                keys.add(obj.key());
            }
            return keys;
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean isAvailable() {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
