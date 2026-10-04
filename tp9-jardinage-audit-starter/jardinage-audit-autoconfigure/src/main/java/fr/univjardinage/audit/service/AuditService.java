package fr.univjardinage.audit.service;

import fr.univjardinage.audit.autoconfigure.AuditProperties;
import fr.univjardinage.audit.exporter.AuditExporter;
import fr.univjardinage.audit.model.AuditEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service principal d'enregistrement des événements d'audit.
 *
 * Cette classe ne porte pas @Service : elle sera créée par
 * AuditAutoConfiguration à l'étape 5.
 */
@Slf4j
@RequiredArgsConstructor
public class AuditService {

    private final AuditProperties properties;
    private final AuditExporter exporter;
    private final List<AuditEvent> eventBuffer = new ArrayList<>();

    /**
     * Enregistre une opération d'audit.
     *
     * @param action opération effectuée, par exemple CREATE ou UPDATE
     * @param entityType type de l'entité auditée
     * @param entityId identifiant de l'entité
     * @param username utilisateur à l'origine de l'opération
     * @param oldValue ancienne valeur, éventuellement null
     * @param newValue nouvelle valeur, éventuellement null
     */
    public void audit(
            String action,
            String entityType,
            Long entityId,
            String username,
            Object oldValue,
            Object newValue
    ) {
        AuditEvent event = AuditEvent.builder()
                .timestamp(LocalDateTime.now())
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .username(username)
                .oldValue(oldValue != null ? oldValue.toString() : null)
                .newValue(newValue != null ? newValue.toString() : null)
                .build();

        log.debug("Audit event : {}", event);

        if (properties.isAsync()) {
            auditAsync(event);
        } else {
            auditSync(event);
        }
    }

    /**
     * Version simplifiée du traitement asynchrone demandé par le TP.
     */
    private void auditAsync(AuditEvent event) {
        eventBuffer.add(event);

        if (eventBuffer.size() >= properties.getBatchSize()) {
            flush();
        }
    }

    private void auditSync(AuditEvent event) {
        eventBuffer.add(event);

        if (eventBuffer.size() >= properties.getBatchSize()) {
            flush();
        }
    }

    /**
     * Exporte les événements actuellement en mémoire.
     */
    public void flush() {
        if (eventBuffer.isEmpty()) {
            return;
        }

        exporter.export(new ArrayList<>(eventBuffer));
        eventBuffer.clear();
    }

    /**
     * Récupère l'historique d'une entité.
     */
    public List<AuditEvent> getAuditHistory(
            String entityType,
            Long entityId
    ) {
        return exporter.findByEntity(entityType, entityId);
    }
}
