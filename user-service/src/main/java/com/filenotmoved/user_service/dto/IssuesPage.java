package com.filenotmoved.user_service.dto;

import java.util.List;

import com.filenotmoved.user_service.constant.TableConfig;

import lombok.Data;

@Data
public class IssuesPage {

    private int totalRecords;
    private int filteredRecords;
    private int totalPages;
    private int currentPage;
    private int rowStartIndex;
    private int rowEndIndex;
    private List<Integer> displayPagesIndex;
    private int recordsPerPage = TableConfig.PAGE_SIZE;

}
