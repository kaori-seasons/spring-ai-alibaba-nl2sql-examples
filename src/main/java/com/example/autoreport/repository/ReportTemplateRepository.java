package com.example.autoreport.repository;

import com.example.autoreport.entity.ReportStatus;
import com.example.autoreport.entity.ReportTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    List<ReportTemplate> findByStatusAndCronExpressionIsNotNull(ReportStatus status);

    Page<ReportTemplate> findByNameContainingOrDescriptionContaining(
            String name, String description, Pageable pageable);

    @Query("SELECT rt FROM ReportTemplate rt WHERE rt.status = :status")
    List<ReportTemplate> findActiveTemplates(@Param("status") ReportStatus status);

    @Query("SELECT COUNT(rt) FROM ReportTemplate rt WHERE rt.status = :status")
    long countByStatus(@Param("status") ReportStatus status);
}
