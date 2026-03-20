package com.filenotmoved.user_service.dto;

import java.util.List;
import com.filenotmoved.user_service.constant.SortOrder;

import lombok.Data;

@Data
public class SearchRequest {

    private List<SearchFilter> searchFilters;
    private int currentPage;
    private String sortColumn;
    private SortOrder sortOrder;

}
