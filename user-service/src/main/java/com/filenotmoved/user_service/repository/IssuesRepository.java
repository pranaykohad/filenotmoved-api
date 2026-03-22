package com.filenotmoved.user_service.repository;

import java.util.List;

import org.geolatte.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.filenotmoved.user_service.entity.Issues;

@Repository
public interface IssuesRepository extends JpaRepository<Issues, Long>, JpaSpecificationExecutor<Issues> {

    @Query(value = """
            SELECT * FROM issues
            WHERE ST_Distance_Sphere(location, POINT(:lng, :lat)) <= :radius
            """, countQuery = """
            SELECT COUNT(*) FROM issues
            WHERE ST_Distance_Sphere(location, POINT(:lng, :lat)) <= :radius
            """, nativeQuery = true)
    Page<Issues> findNearbyIssues(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius,
            Pageable pageable);
}
