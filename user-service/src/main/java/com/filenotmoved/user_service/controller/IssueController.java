package com.filenotmoved.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filenotmoved.user_service.dto.IssueRequestDto;
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
    public ResponseEntity<Issues> postIssue(@Valid @ModelAttribute IssueRequestDto requestDto) {
        Issues createdIssue = issueService.createIssue(requestDto);
        return new ResponseEntity<>(createdIssue, HttpStatus.OK);
    }
}
