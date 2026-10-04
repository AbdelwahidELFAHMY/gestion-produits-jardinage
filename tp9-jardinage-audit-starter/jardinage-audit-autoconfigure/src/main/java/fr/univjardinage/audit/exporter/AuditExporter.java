package fr.univjardinage.audit.exporter;

import fr.univjardinage.audit.model.AuditEvent;

import java.util.List;

/**
 * Contrat commun pour tous les formats d'export des audits.
 */
public interface AuditExporter {

    void export(List<AuditEvent> events);

    List<AuditEvent> findByEntity(String entityType, Long entityId);
}
