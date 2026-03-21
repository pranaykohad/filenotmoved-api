package com.filenotmoved.user_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssuesDto {

    private Long id;

    private String description;

    private String location;

    private String locality;

    private String city;

    private String createdBy;

    private LocalDateTime createAt;

    private Long issueType;

    private Object originalImage;

    private Object mediumImage;

    private Object thumbnailImage;

}
