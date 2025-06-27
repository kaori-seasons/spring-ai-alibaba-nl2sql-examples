package com.example.autoreport.repository;

import com.example.autoreport.entity.ExecutionStatus;
import com.example.autoreport.entity.ReportExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportExecutionRepository extends JpaRepository<ReportExecution, Long> {

    Page<ReportExecution> findByTemplateId(Long templateId, Pageable pageable);

    List<ReportExecution> findByTemplateIdAndStatus(Long templateId, ExecutionStatus status);

    @Query("SELECT re FROM ReportExecution re WHERE re.executionTime BETWEEN :startTime AND :endTime")
    List<ReportExecution> findByExecutionTimeBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(re) FROM ReportExecution re WHERE re.templateId = :templateId AND re.status = :status")
    long countByTemplateIdAndStatus(@Param("templateId") Long templateId, @Param("status") ExecutionStatus status);
}