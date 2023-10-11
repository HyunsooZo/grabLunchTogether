package com.grablunchtogether.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.grablunchtogether.dto.ImageDto;
import com.grablunchtogether.enums.ImageDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3BucketService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public ImageDto.Dto saveFile(MultipartFile multipartFile,
                                 ImageDirectory imageDirectory) throws IOException {

        // 파일 이름에 이미지 타입에 대한 경로 및 UUID와 원래 파일의 확장자를 포함
        String originalFileName = imageDirectory.getString() + "/" +
                UUID.randomUUID() + "." + getFileExtension(multipartFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // 파일 업로드
        amazonS3.putObject(bucket, originalFileName, multipartFile.getInputStream(), metadata);

        // 업로드한 파일의 URL 반환
        return ImageDto.Dto.builder()
                .imageUrl(amazonS3.getUrl(bucket, originalFileName).toString())
                .build();
    }

    public void deleteFile(String imagePath) {
        // 파일 삭제
        amazonS3.deleteObject(bucket, imagePath);
    }

    private String getFileExtension(MultipartFile file) {
        // 파일 이름에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return originalFilename.substring(lastDotIndex + 1);
            }
        }
        return ""; // 확장자가 없는 경우 빈 문자열 반환
    }
}
