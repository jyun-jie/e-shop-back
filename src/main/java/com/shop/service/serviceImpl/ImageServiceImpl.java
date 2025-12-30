package com.shop.service.serviceImpl;

import com.shop.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.io.IOException;
import java.sql.SQLOutput;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String uploadProductImage(MultipartFile file) throws IOException {


        UUID uuid = UUID.randomUUID();

        String key = "public/product/" + uuid + "-" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return "https://" + bucket + ".s3.ap-east-2.amazonaws.com/" + key;
    }

}
