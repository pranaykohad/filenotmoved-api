package com.filenotmoved.user_service.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.filenotmoved.user_service.util.PointSerializer;

import org.locationtech.jts.geom.Point;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "issues")
public class Issues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, columnDefinition = "geometry")
    private Point location;

    @Column(nullable = false)
    private String locality;

    @Column(nullable = false)
    private String city;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "issue_type", nullable = false)
    private Long issueType;

    @Column(name = "image_key")
    private String imageKey;

    @Transient
    private Object image;

    @PrePersist
    public void prePersist() {
        if (this.createdBy == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                this.createdBy = authentication.getName();
            } else {
                this.createdBy = "SYSTEM";
            }
        }
    }
}
