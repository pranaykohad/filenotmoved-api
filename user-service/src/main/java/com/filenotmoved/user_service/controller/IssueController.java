package com.filenotmoved.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import com.filenotmoved.user_service.dto.IssuesRequestDto;
import com.filenotmoved.user_service.dto.IssuesResponseDto;
import com.filenotmoved.user_service.dto.SearchRequest;
import com.filenotmoved.user_service.entity.Issues;
import com.filenotmoved.user_service.service.IssueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("post")
    public ResponseEntity<Issues> postIssue(@Valid @ModelAttribute IssuesRequestDto requestDto) {
        Issues createdIssue = issueService.createIssue(requestDto);
        return new ResponseEntity<>(createdIssue, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("photo")
    public ResponseEntity<IssuesResponseDto> getIssues(@RequestBody SearchRequest searchRequest) {
        return new ResponseEntity<>(issueService.getIssues(searchRequest), HttpStatus.OK);
    }

}
