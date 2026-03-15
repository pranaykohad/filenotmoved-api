package com.filenotmoved.user_service.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.filenotmoved.user_service.dto.IssueRequestDto;
import com.filenotmoved.user_service.entity.Issues;
import com.filenotmoved.user_service.repository.IssuesRepository;
import com.filenotmoved.user_service.service.aws.AwsS3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueService {

    private final IssuesRepository issuesRepository;
    private final AwsS3Service awsS3Service;

    @Value("${aws.s3.folder-name.issues}")
    private String issueFolderName;

    public Issues createIssue(IssueRequestDto requestDto) {
        log.info("Creating new issue in city: {}", requestDto.getCity());

        String imageKey = null;
        if (requestDto.getPhoto() != null && !requestDto.getPhoto().isEmpty()) {
            String fileName = UUID.randomUUID().toString();
            imageKey = awsS3Service.uploadFile(requestDto.getPhoto(), issueFolderName, fileName);
            log.info("Uploaded issue photo to S3 with key: {}", imageKey);

            Issues issue = Issues.builder()
                    .description(requestDto.getDescription())
                    .location(requestDto.getLocation())
                    .locality(requestDto.getLocality())
                    .city(requestDto.getCity())
                    .issueType(requestDto.getIssueType())
                    .imageKey(imageKey)
                    .build();

            Issues savedIssue = issuesRepository.save(issue);
            log.info("Issue created successfully with ID: {}", savedIssue.getId());
            return savedIssue;
        } else {
            throw new IllegalArgumentException("Photo is required");
        }
    }
}
