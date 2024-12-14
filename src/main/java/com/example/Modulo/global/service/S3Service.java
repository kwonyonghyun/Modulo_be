package com.example.Modulo.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.Modulo.exception.FileSizeExceededException;
import com.example.Modulo.exception.FileUploadException;
import com.example.Modulo.exception.InvalidFileTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadFile(String directory, MultipartFile file) {
        validateFile(file);
        try {
            String fileName = createUniqueFileName(directory, file.getOriginalFilename());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new FileUploadException();
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileTypeException();
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new InvalidFileTypeException();
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException();
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileName(fileUrl);
            amazonS3.deleteObject(bucket, fileName);
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            throw new FileUploadException();
        }
    }

    private String createUniqueFileName(String directory, String originalFileName) {
        return directory + "/" + UUID.randomUUID() + extractExtension(originalFileName);
    }

    private String extractFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    private String extractExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidFileTypeException();
        }
    }
}