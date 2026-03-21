package com.filenotmoved.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.filenotmoved.user_service.dto.IssuesDto;
import com.filenotmoved.user_service.dto.IssuesRequestDto;
import com.filenotmoved.user_service.dto.IssuesResponseDto;
import com.filenotmoved.user_service.dto.SearchRequest;
import com.filenotmoved.user_service.service.IssueService;
import com.filenotmoved.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("add-issue")
    public ResponseEntity<IssuesDto> postIssue(@Valid @ModelAttribute IssuesRequestDto requestDto,
            @Valid @RequestParam String phoneNumber) {
        userService.getExistingActiveAppUser(phoneNumber);
        final IssuesDto createdIssue = issueService.createIssue(requestDto);
        return new ResponseEntity<>(createdIssue, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("get-issue")
    public ResponseEntity<IssuesResponseDto> getIssues(@RequestBody SearchRequest searchRequest,
            @Valid @RequestParam String phoneNumber) {
        userService.getExistingActiveAppUser(phoneNumber);
        return new ResponseEntity<>(issueService.getIssues(searchRequest), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("get-issue-by-id/{issueId}")
    public ResponseEntity<IssuesDto> getIssueByIdAndResolution(@Valid @PathVariable Long issueId,
            @Valid @RequestParam String phoneNumber, @Valid String resolution) {
        userService.getExistingActiveAppUser(phoneNumber);
        return new ResponseEntity<>(issueService.getIssueByIdAndResolution(issueId, resolution), HttpStatus.OK);
    }

}
