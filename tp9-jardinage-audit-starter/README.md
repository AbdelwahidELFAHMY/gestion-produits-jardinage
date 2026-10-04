# Jardinage Audit Spring Boot Starter

Starter Spring Boot réutilisable pour enregistrer les opérations d'audit d'une application Jardinage.

## Structure

- `jardinage-audit-autoconfigure` : propriétés, service, exporteurs et auto-configuration.
- `jardinage-audit-spring-boot-starter` : dépendance principale à ajouter dans une application.
- `jardinage-audit-sample` : application d'exemple consommant le starter.

## Utilisation

Ajouter la dépendance suivante dans l'application cliente :

```xml
<dependency>
    <groupId>fr.univjardinage</groupId>
    <artifactId>jardinage-audit-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

L'auto-configuration est activée automatiquement. L'annotation `@EnableAudit` peut aussi être utilisée pour l'activer explicitement :

```java
@SpringBootApplication
@EnableAudit
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Le service est ensuite injecté dans un composant métier :

```java
public ProductService(AuditService auditService) {
    this.auditService = auditService;
}

public void createProduct(Long id, Product product) {
    auditService.audit(
            "CREATE", "Product", id, "current-user", null, product
    );
}
```

## Configuration

```properties
jardinage.audit.enabled=true
jardinage.audit.export-format=JSON
jardinage.audit.log-directory=./audit-logs
jardinage.audit.async=false
jardinage.audit.batch-size=100
jardinage.audit.retention-days=90
jardinage.audit.include-user-details=true
jardinage.audit.include-stack-trace=false
```

Formats disponibles : `JSON`, `CSV`, `DATABASE` et `NONE`.

## Construction et tests

Depuis ce répertoire :

```bash
mvn -Dspring-boot.version=3.2.3 test
```

Les tests d'auto-configuration utilisent `ApplicationContextRunner` pour vérifier l'activation, la désactivation et le choix de l'exporteur CSV.

## Principes appliqués

- séparation entre le module starter et le module auto-configure ;
- auto-configuration conditionnelle avec `@ConditionalOnProperty`, `@ConditionalOnClass` et `@ConditionalOnMissingBean` ;
- propriétés documentées par `spring-configuration-metadata.json` ;
- valeurs par défaut raisonnables et possibilité de remplacer les beans ;
- tests isolés de l'auto-configuration avec `ApplicationContextRunner`.
