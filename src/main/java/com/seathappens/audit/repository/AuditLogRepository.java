package com.seathappens.audit.repository;

import com.seathappens.audit.entity.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLogDocument, String> {
}
