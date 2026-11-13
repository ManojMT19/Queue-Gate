package com.fairwaiting.logger_service.repository;

import com.fairwaiting.logger_service.entity.QueueEventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEventRecordRepository extends JpaRepository<QueueEventRecord, String> {
}