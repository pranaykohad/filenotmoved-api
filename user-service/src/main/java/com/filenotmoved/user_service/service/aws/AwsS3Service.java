package com.filenotmoved.user_service.service.aws;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.filenotmoved.user_service.exception.custom.GenericException;
import com.filenotmoved.user_service.exception.custom.InvalidFileFormatException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String addFile(byte[] bytes, String extension, String contentType, String folderName, String fileName,
            String type) {
        final String key = folderName + "/" + fileName + "_" + type + "." + extension;
        try {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(key).contentType(contentType).build(),
                    RequestBody.fromBytes(bytes));
            log.info("file: {} is added in AWS S3 bucket", key);
        } catch (S3Exception e) {
            log.error("S3 Bucket configuration error: {}", e.getMessage());
            throw new GenericException("Not able to upload file in System. Please contact system administrator.");
        } catch (InvalidFileFormatException ex) {
            log.error("InvalidFileFormatException while uploading file to AWS S3: {}", ex.getMessage());
            throw new GenericException("File format is not correct: " + fileName);
        }
        return key;
    }

    public String addOrReplaceFile(byte[] bytes, String extension, String contentType, String folderName,
            String fileName) {
        final String key = folderName + "/" + fileName + "." + extension;
        try {
            final ListObjectsV2Response imageList = s3Client.listObjectsV2(
                    ListObjectsV2Request.builder().bucket(bucketName).prefix(folderName + "/" + fileName).build());
            imageList.contents().stream().filter(image -> {
                final String keyName = image.key();
                final int slash = keyName.lastIndexOf('/');
                final int dot = keyName.lastIndexOf('.');
                String existingFileName;
                if (dot > slash) {
                    existingFileName = keyName.substring(slash + 1, dot);
                } else {
                    existingFileName = keyName.substring(slash + 1);
                }
                return existingFileName.equals(fileName);
            }).forEach(s3Object -> {
                log.info("Deleting existing file with same name: {}", s3Object.key());
                s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(s3Object.key()).build());
            });

            log.info("Uploading file to AWS S3 bucket: {}", key);
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(key).contentType(contentType).build(),
                    RequestBody.fromBytes(bytes));
            log.info("file: {} is uploaded/replaced in AWS S3 bucket", key);
        } catch (S3Exception e) {
            log.error("S3 Bucket configuration error: {}", e.getMessage());
            throw new GenericException("Not able to upload file in System. Please contact system administrator.");
        } catch (InvalidFileFormatException ex) {
            log.error("InvalidFileFormatException while uploading file to AWS S3: {}", ex.getMessage());
            throw new GenericException("File format is not correct: " + fileName);
        }
        return key;
    }

    public byte[] downloadFile(String fileName, String folderName) {
        final String key = folderName + "/" + fileName;
        try {
            log.info("Downloading file from AWS S3 bucket: {}", key);
            ResponseBytes<GetObjectResponse> objectBytes = s3Client
                    .getObjectAsBytes(GetObjectRequest.builder().bucket(bucketName).key(key).build());
            log.info("file: {} is downloaded from AWS S3 bucket", key);
            return objectBytes.asByteArray();
        } catch (Exception ex) {
            log.error("Exception while downloading file from AWS S3: {}", ex.getMessage());
            throw new GenericException("File not found in s3 bucket: " + ex.getMessage());
        }
    }

    public Object downloadFile(String key) {
        try {
            log.info("Downloading file from AWS S3 bucket: {}", key);
            ResponseBytes<GetObjectResponse> objectBytes = s3Client
                    .getObjectAsBytes(GetObjectRequest.builder().bucket(bucketName).key(key).build());
            log.info("file: {} is downloaded from AWS S3 bucket", key);
            return Base64.getEncoder().encodeToString(objectBytes.asByteArray());
        } catch (Exception ex) {
            log.error("Exception while downloading file from AWS S3: {}", ex.getMessage());
            throw new GenericException("File not found in s3 bucket: " + ex.getMessage());
        }
    }

    public String deleteFile(String fileName, String folderName) {
        final String key = folderName + "/" + fileName;
        try {
            log.info("Deleting file from AWS S3 bucket: {}", key);
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
            log.info("file: {} is deleted from AWS S3 bucket", key);
            return "File deleted successfully";
        } catch (Exception ex) {
            log.error("Exception while deleting file from AWS S3: {}", ex.getMessage());
            throw new GenericException("Error while deleting file from s3 bucket: " + ex.getMessage());
        }
    }

}