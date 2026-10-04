package fr.univjardinage.audit.annotation;

import fr.univjardinage.audit.autoconfigure.AuditAutoConfiguration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Active explicitement le starter d'audit Jardinage.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AuditAutoConfiguration.class)
public @interface EnableAudit {

    /**
     * Format d'export déclaré par l'application.
     */
    String exportFormat() default "JSON";

    /**
     * Indique si le traitement asynchrone est souhaité.
     */
    boolean async() default false;

    /**
     * Répertoire de stockage des logs.
     */
    String logDirectory() default "./audit-logs";
}
