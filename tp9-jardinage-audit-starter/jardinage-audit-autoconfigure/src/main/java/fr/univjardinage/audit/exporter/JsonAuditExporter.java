package fr.univjardinage.audit.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univjardinage.audit.autoconfigure.AuditProperties;
import fr.univjardinage.audit.model.AuditEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

/**
 * Exporte les événements d'audit au format JSON.
 */
@Slf4j
@RequiredArgsConstructor
public class JsonAuditExporter implements AuditExporter {

    private final AuditProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public void export(List<AuditEvent> events) {
        Path directory = Paths.get(properties.getLogDirectory());
        Path file = directory.resolve(
                "audit-" + LocalDate.now() + ".json"
        );

        try {
            Files.createDirectories(directory);

            String json = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(events);

            Files.writeString(
                    file,
                    json + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            log.info(
                    "Exported {} audit events to {}",
                    events.size(),
                    file
            );
        } catch (IOException exception) {
            log.error(
                    "Failed to export audit events to {}",
                    file,
                    exception
            );
        }
    }

    @Override
    public List<AuditEvent> findByEntity(
            String entityType,
            Long entityId
    ) {
        // Lecture depuis les fichiers JSON laissée volontairement simplifiée
        // conformément au sujet du TP.
        return List.of();
    }
}
