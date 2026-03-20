package com.filenotmoved.user_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class IssuesResponseDto {

    private ColumnConfigDto columnConfig;
    private List<IssuesDto> issuesList;
    private IssuesPage issuesPage;
}
