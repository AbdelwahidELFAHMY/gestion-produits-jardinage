package fr.univjardinage.jardinage.runner;

 import lombok.extern.slf4j.Slf4j;
 import org.springframework.boot.CommandLineRunner;
 import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
 import org.springframework.core.annotation.Order;
 import org.springframework.stereotype.Component;

 @Slf4j
 @Component
 @Order (4)
 @ConditionalOnProperty(
 name = "app.demo.data.enabled",
 havingValue = "true",
 matchIfMissing = false
 )
 public class DemoDataRunner implements CommandLineRunner {

 @Override
 public void run(String ... args) throws Exception {
 log.info("[ORDRE 4 - CONDITIONNEL] Chargement des donnees de demonstration");
 log.info("ATTENTION : Mode DEMO active");
 log.info("Donnees fictives chargees pour les tests");
  }
}