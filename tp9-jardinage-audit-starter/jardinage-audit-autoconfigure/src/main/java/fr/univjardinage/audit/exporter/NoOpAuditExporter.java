package fr.univjardinage.audit.exporter;

import fr.univjardinage.audit.model.AuditEvent;

import java.util.List;

/**
 * Exporter silencieux utilisé lorsque l'export est désactivé avec NONE.
 */
public class NoOpAuditExporter implements AuditExporter {

    @Override
    public void export(List<AuditEvent> events) {
        // Aucun export volontairement.
    }

    @Override
    public List<AuditEvent> findByEntity(
            String entityType,
            Long entityId
    ) {
        return List.of();
    }
}
