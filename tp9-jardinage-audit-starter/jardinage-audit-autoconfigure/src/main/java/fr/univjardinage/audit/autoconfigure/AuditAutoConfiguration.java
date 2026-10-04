package fr.univjardinage.audit.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univjardinage.audit.exporter.AuditExporter;
import fr.univjardinage.audit.exporter.CsvAuditExporter;
import fr.univjardinage.audit.exporter.DatabaseAuditExporter;
import fr.univjardinage.audit.exporter.JsonAuditExporter;
import fr.univjardinage.audit.exporter.NoOpAuditExporter;
import fr.univjardinage.audit.service.AuditService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration du starter d'audit Jardinage.
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(AuditProperties.class)
@ConditionalOnProperty(
        prefix = "jardinage.audit",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class AuditAutoConfiguration {

    public AuditAutoConfiguration() {
        log.info("Initializing Jardinage Audit Auto-Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditService auditService(
            AuditProperties properties,
            AuditExporter exporter
    ) {
        log.info(
                "Creating AuditService with export format: {}",
                properties.getExportFormat()
        );

        return new AuditService(properties, exporter);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "jardinage.audit",
            name = "export-format",
            havingValue = "JSON",
            matchIfMissing = true
    )
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnMissingBean(AuditExporter.class)
    public AuditExporter jsonAuditExporter(
            AuditProperties properties,
            ObjectMapper objectMapper
    ) {
        log.info("Creating JSON AuditExporter");
        return new JsonAuditExporter(properties, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "jardinage.audit",
            name = "export-format",
            havingValue = "CSV"
    )
    @ConditionalOnMissingBean(AuditExporter.class)
    public AuditExporter csvAuditExporter(
            AuditProperties properties
    ) {
        log.info("Creating CSV AuditExporter");
        return new CsvAuditExporter(properties);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "jardinage.audit",
            name = "export-format",
            havingValue = "DATABASE"
    )
    @ConditionalOnClass(name = "jakarta.persistence.EntityManager")
    @ConditionalOnMissingBean(AuditExporter.class)
    public AuditExporter databaseAuditExporter(
            AuditProperties properties
    ) {
        log.info("Creating Database AuditExporter");
        return new DatabaseAuditExporter(properties);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "jardinage.audit",
            name = "export-format",
            havingValue = "NONE"
    )
    @ConditionalOnMissingBean(AuditExporter.class)
    public AuditExporter noOpAuditExporter() {
        log.info("Creating disabled AuditExporter");
        return new NoOpAuditExporter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnProperty(
            prefix = "jardinage.audit",
            name = "export-format",
            havingValue = "JSON",
            matchIfMissing = true
    )
    public ObjectMapper auditObjectMapper() {
        log.info("Creating default ObjectMapper for audit");
        return new ObjectMapper().findAndRegisterModules();
    }
}
