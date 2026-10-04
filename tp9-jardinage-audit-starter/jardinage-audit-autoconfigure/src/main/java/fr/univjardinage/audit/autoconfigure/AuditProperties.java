package fr.univjardinage.audit.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration du starter d'audit Jardinage.
 *
 * Les propriétés sont préfixées par :
 * jardinage.audit
 */
@Data
@ConfigurationProperties(prefix = "jardinage.audit")
public class AuditProperties {

    /**
     * Active ou désactive l'audit.
     */
    private boolean enabled = true;

    /**
     * Format d'export des événements : JSON, CSV, DATABASE ou NONE.
     */
    private ExportFormat exportFormat = ExportFormat.JSON;

    /**
     * Répertoire de stockage des logs d'audit.
     */
    private String logDirectory = "./audit-logs";

    /**
     * Active le traitement asynchrone des événements.
     */
    private boolean async = false;

    /**
     * Nombre maximum d'événements avant un flush.
     */
    private int batchSize = 100;

    /**
     * Durée de conservation des logs, en jours.
     */
    private int retentionDays = 90;

    /**
     * Inclut les informations de l'utilisateur dans l'audit.
     */
    private boolean includeUserDetails = true;

    /**
     * Inclut la stacktrace lorsqu'une erreur est auditée.
     */
    private boolean includeStackTrace = false;

    public enum ExportFormat {
        JSON,
        CSV,
        DATABASE,
        NONE
    }
}
