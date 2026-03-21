package com.filenotmoved.user_service.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.filenotmoved.user_service.constant.CommonConstants;
import com.filenotmoved.user_service.constant.TableConfig;
import com.filenotmoved.user_service.dto.ColumnConfigDto;
import com.filenotmoved.user_service.dto.IssuesDto;
import com.filenotmoved.user_service.dto.IssuesPage;
import com.filenotmoved.user_service.dto.IssuesRequestDto;
import com.filenotmoved.user_service.dto.IssuesResponseDto;
import com.filenotmoved.user_service.dto.SearchRequest;
import com.filenotmoved.user_service.entity.Issues;
import com.filenotmoved.user_service.exception.custom.FileSizeExceedsException;
import com.filenotmoved.user_service.exception.custom.GenericException;
import com.filenotmoved.user_service.mapper.GenericMapper;
import com.filenotmoved.user_service.repository.IssuesRepository;
import com.filenotmoved.user_service.service.aws.AwsS3Service;
import com.filenotmoved.user_service.util.Helper;
import com.filenotmoved.user_service.util.ImageUtil;
import com.filenotmoved.user_service.util.UserSpecificationHelper;

import io.jsonwebtoken.lang.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueService {

    private final IssuesRepository issuesRepository;
    private final AwsS3Service awsS3Service;
    private final ModelMapper modelMapper;

    @Value("${aws.s3.folder-name.issues}")
    private String issueFolderName;

    public IssuesDto createIssue(IssuesRequestDto requestDto) {
        log.info("Creating new issue in city, locality: {}, {}", requestDto.getCity(), requestDto.getLocality());

        final MultipartFile issuePhoto = requestDto.getPhoto();
        Helper.validateFile(issuePhoto.getOriginalFilename(), issuePhoto.getSize());
        String fileName = UUID.randomUUID().toString();
        String extension = Helper.getFileExtension(issuePhoto.getOriginalFilename());
        String folderName = issueFolderName + "/" + fileName;
        try {

            final byte[] originalBytes = requestDto.getPhoto().getBytes();

            final byte[] mediumBytes = ImageUtil.resizeImage(
                    new ByteArrayInputStream(originalBytes), 1024, 1024);

            final byte[] thumbBytes = ImageUtil.resizeImage(
                    new ByteArrayInputStream(originalBytes), 300, 300);

            final String originalImageKey = awsS3Service.addFile(originalBytes, extension, issuePhoto.getContentType(),
                    folderName, fileName, "original");
            final String mediumImageKey = awsS3Service.addFile(mediumBytes, extension, issuePhoto.getContentType(),
                    folderName, fileName, "medium");
            final String thumbImageKey = awsS3Service.addFile(thumbBytes, extension, issuePhoto.getContentType(),
                    folderName, fileName, "thumb");

            log.info("Uploaded issue photo to S3 with key: {}", originalImageKey);
            log.info("Uploaded issue photo to S3 with key: {}", mediumImageKey);
            log.info("Uploaded issue photo to S3 with key: {}", thumbImageKey);

            final Issues issue = Issues.builder()
                    .description(requestDto.getDescription())
                    .location(Helper.parseLocation(requestDto.getLocation()))
                    .locality(requestDto.getLocality())
                    .city(requestDto.getCity())
                    .imageId(fileName)
                    .issueType(requestDto.getIssueType())
                    .originalKey(originalImageKey)
                    .mediumKey(mediumImageKey)
                    .thumbnailKey(thumbImageKey)
                    .build();

            final Issues savedIssue = issuesRepository.save(issue);
            final IssuesDto issuesDto = Helper.issueEntityToDto(savedIssue);
            log.info("Issue created successfully with ID: {}", savedIssue.getId());
            try {
                Object photo = awsS3Service.downloadFile(savedIssue.getThumbnailKey());
                issuesDto.setThumbnailImage(photo);
            } catch (Exception e) {
                log.error("Error fetching issue photo: {}", e.getMessage());
                throw new GenericException("Error fetching issue photo");
            }
            return issuesDto;
        } catch (Exception e) {
            log.error("Error creating issue: {}", e.getMessage());
            throw new GenericException("Error creating issue");
        }
    }

    public IssuesResponseDto getIssues(SearchRequest searchRequest) {
        final Pageable pageable = Helper.buildPage(searchRequest);
        final Specification<Issues> spec = UserSpecificationHelper.buildSpecification(searchRequest.getSearchFilters());
        Page<Issues> list = new PageImpl<>(new ArrayList<>());
        try {
            list = issuesRepository.findAll(spec, pageable);
            list.forEach(issue -> {
                try {
                    Object photo = awsS3Service.downloadFile(issue.getOriginalKey());
                    issue.setOriginalImage(photo);
                } catch (Exception e) {
                    log.error("Error fetching issue photo: {}", e.getMessage());
                    throw new GenericException("Error fetching issue photo");
                }
            });
            log.info("Issues fetched successfully: {}", list);
        } catch (Exception e) {
            log.error("Error fetching issues: {}", e.getMessage());
            throw new GenericException("Error fetching issues");
        }
        return pageableEventResponse(list, spec, searchRequest.getCurrentPage());
    }

    private IssuesResponseDto pageableEventResponse(Page<Issues> pagedEventList, Specification<Issues> spec,
            int currentPage) {
        final GenericMapper<IssuesDto, Issues> mapper = new GenericMapper<>(modelMapper,
                IssuesDto.class, Issues.class);
        final IssuesResponseDto eventDtoList = new IssuesResponseDto();
        final ColumnConfigDto columnConfig = new ColumnConfigDto(TableConfig.ISSUE_TABLE_COlUMNS);
        eventDtoList.setColumnConfig(columnConfig);
        eventDtoList.setIssuesList(mapper.entityToDto(pagedEventList.getContent()));
        eventDtoList.setIssuesPage(buildEventPage(spec, currentPage, pagedEventList.getContent().size()));
        return eventDtoList;
    }

    private IssuesPage buildEventPage(Specification<Issues> spec, int currentPage, int currentRecordSize) {
        final IssuesPage eventPage = new IssuesPage();
        final int filteredCount = getEventCount(spec);
        eventPage.setTotalPages((int) Math.ceil((double) filteredCount / TableConfig.PAGE_SIZE));
        eventPage.setFilteredRecords(filteredCount);
        eventPage.setTotalRecords(getEventCount(null));
        eventPage.setRecordsPerPage(TableConfig.PAGE_SIZE);
        eventPage.setCurrentPage(currentPage);
        final int rowStartIndex = currentPage * TableConfig.PAGE_SIZE;
        eventPage.setRowStartIndex(rowStartIndex + 1);
        eventPage.setRowEndIndex(rowStartIndex + currentRecordSize);
        eventPage.setDisplayPagesIndex(getDisplayPagesIndex(eventPage.getTotalPages(), eventPage.getCurrentPage()));
        return eventPage;
    }

    private int getEventCount(Specification<Issues> spec) {
        return spec != null ? (int) issuesRepository.count(spec) : (int) issuesRepository.count();
    }

    private List<Integer> getDisplayPagesIndex(int totalPages, int currentPage) {
        List<Integer> displayPagesIndex = new ArrayList<>();

        if (totalPages <= 0 || currentPage < 0 || currentPage >= totalPages) {
            final Integer[] array = { 1, 2, 3, 4, 5 };
            displayPagesIndex = new ArrayList<>(Arrays.asList(array));
        } else {
            int start = Math.max(0, Math.min(currentPage - 2, totalPages - 5));
            int end = Math.min(totalPages, start + 5);

            for (int i = start; i < end; i++) {
                displayPagesIndex.add(i);
            }
        }
        return displayPagesIndex;
    }
}
