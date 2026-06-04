package com.seathappens.audit.entity;

import com.seathappens.audit.event.AuditAction;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLogDocument {

    @Id
    private String id;

    private AuditAction action;

    private String actorUserId;

    private String entityType;

    private String entityId;

    private String correlationId;

    private String occurredAt;

    private Map<String, String> metadata;

}
