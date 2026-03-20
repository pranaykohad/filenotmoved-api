package com.filenotmoved.user_service.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IssuesRequestDto {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Locality is required")
    private String locality;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Issue type ID is required")
    private Long issueType;

    @NotNull(message = "Issue photo is required")
    private MultipartFile photo;
}
