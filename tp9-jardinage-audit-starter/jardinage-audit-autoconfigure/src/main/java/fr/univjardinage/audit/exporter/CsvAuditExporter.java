package fr.univjardinage.audit.exporter;

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
 * Exporte les événements d'audit au format CSV.
 */
@Slf4j
@RequiredArgsConstructor
public class CsvAuditExporter implements AuditExporter {

    private static final String HEADER =
            "timestamp,action,entityType,entityId,username," +
                    "oldValue,newValue,ipAddress,userAgent";

    private final AuditProperties properties;

    @Override
    public void export(List<AuditEvent> events) {
        Path directory = Paths.get(properties.getLogDirectory());
        Path file = directory.resolve(
                "audit-" + LocalDate.now() + ".csv"
        );

        try {
            Files.createDirectories(directory);

            boolean newFile = Files.notExists(file);
            StringBuilder content = new StringBuilder();

            if (newFile) {
                content.append(HEADER)
                        .append(System.lineSeparator());
            }

            for (AuditEvent event : events) {
                content.append(csv(event.getTimestamp()))
                        .append(',')
                        .append(csv(event.getAction()))
                        .append(',')
                        .append(csv(event.getEntityType()))
                        .append(',')
                        .append(csv(event.getEntityId()))
                        .append(',')
                        .append(csv(event.getUsername()))
                        .append(',')
                        .append(csv(event.getOldValue()))
                        .append(',')
                        .append(csv(event.getNewValue()))
                        .append(',')
                        .append(csv(event.getIpAddress()))
                        .append(',')
                        .append(csv(event.getUserAgent()))
                        .append(System.lineSeparator());
            }

            Files.writeString(
                    file,
                    content.toString(),
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

    private String csv(Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString().replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    @Override
    public List<AuditEvent> findByEntity(
            String entityType,
            Long entityId
    ) {
        // Lecture depuis le fichier CSV laissée simplifiée pour le TP.
        return List.of();
    }
}
