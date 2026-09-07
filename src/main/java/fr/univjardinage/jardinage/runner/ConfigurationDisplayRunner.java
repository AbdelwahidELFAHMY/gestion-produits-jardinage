package fr.univjardinage.jardinage.runner;

 import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
 @Slf4j
 @Component
 @Order (2)
 public class ConfigurationDisplayRunner implements CommandLineRunner {

 @Value("${spring.application.name:gestion -jardinage}")
 private String applicationName;

 @Override
 public void run(String ... args) throws Exception {
log.info("[ORDRE 2] Affichage de la configuration");
log.info("Nom de l’application : {}", applicationName);
log.info("Profil actif : default");
  log.info("Port du serveur : 8080");
  }
 }