package com.filenotmoved.user_service.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableConfig {

    private TableConfig() {
    }

    public static final List<String> ISSUE_TABLE_COlUMNS = new ArrayList<>(
            Arrays.asList("description", "location",
                    "locality", "city", "issueType", "createdBy", "createAt"));

    public static final int PAGE_SIZE = 20;

}
