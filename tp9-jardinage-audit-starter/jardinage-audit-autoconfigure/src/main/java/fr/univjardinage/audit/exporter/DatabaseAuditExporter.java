package fr.univjardinage.audit.exporter;

import fr.univjardinage.audit.autoconfigure.AuditProperties;
import fr.univjardinage.audit.model.AuditEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Exporter DATABASE simplifié pour le TP.
 *
 * Une implémentation réelle utiliserait une entité JPA et un repository.
 */
@Slf4j
@RequiredArgsConstructor
public class DatabaseAuditExporter implements AuditExporter {

    private final AuditProperties properties;
    private final List<AuditEvent> events = new ArrayList<>();

    @Override
    public synchronized void export(List<AuditEvent> auditEvents) {
        events.addAll(auditEvents);

        log.info(
                "Stored {} audit events in the database exporter",
                auditEvents.size()
        );
    }

    @Override
    public synchronized List<AuditEvent> findByEntity(
            String entityType,
            Long entityId
    ) {
        return events.stream()
                .filter(event -> entityType.equals(event.getEntityType()))
                .filter(event -> entityId.equals(event.getEntityId()))
                .toList();
    }
}
