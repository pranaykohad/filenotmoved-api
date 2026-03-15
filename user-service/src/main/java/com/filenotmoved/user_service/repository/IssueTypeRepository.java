package com.filenotmoved.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filenotmoved.user_service.entity.IssueType;

@Repository
public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {
}
