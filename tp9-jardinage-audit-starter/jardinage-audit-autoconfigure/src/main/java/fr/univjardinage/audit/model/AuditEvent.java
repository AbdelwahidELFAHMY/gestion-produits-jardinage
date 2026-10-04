package fr.univjardinage.audit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Événement représentant une opération auditée.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    private LocalDateTime timestamp;
    private String action;
    private String entityType;
    private Long entityId;
    private String username;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
}
