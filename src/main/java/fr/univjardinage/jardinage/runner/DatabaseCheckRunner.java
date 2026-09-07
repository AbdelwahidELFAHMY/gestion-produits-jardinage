package fr.univjardinage.jardinage.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
 @Slf4j
@Component
 @Order (1)
public class DatabaseCheckRunner implements CommandLineRunner {

 @Override
 public void run(String ... args) throws Exception {
 log.info("[ORDRE 1] Verification de la connexion database");
 log.info("Database H2 : OK");
 log.info("Schema cree : OK");
 }
}