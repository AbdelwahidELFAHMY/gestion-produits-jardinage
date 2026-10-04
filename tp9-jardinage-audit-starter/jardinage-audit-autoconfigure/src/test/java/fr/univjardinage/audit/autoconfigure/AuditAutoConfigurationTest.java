package fr.univjardinage.audit.autoconfigure;

import fr.univjardinage.audit.service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class AuditAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of(AuditAutoConfiguration.class)
                    );

    @Test
    void autoConfigurationIsEnabledByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AuditService.class);
            assertThat(context).hasBean("jsonAuditExporter");
        });
    }

    @Test
    void autoConfigurationCanBeDisabled() {
        contextRunner
                .withPropertyValues("jardinage.audit.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(AuditService.class);
                    assertThat(context).doesNotHaveBean("jsonAuditExporter");
                });
    }

    @Test
    void csvExporterIsSelectedWhenConfigured() {
        contextRunner
                .withPropertyValues(
                        "jardinage.audit.enabled=true",
                        "jardinage.audit.export-format=CSV"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(AuditService.class);
                    assertThat(context).hasBean("csvAuditExporter");
                    assertThat(context).doesNotHaveBean("jsonAuditExporter");
                });
    }
}
