package fr.univjardinage.jardinage.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializerRunner implements CommandLineRunner {

 @Override
public void run(String ... args) throws Exception {
     log.info("===========================================");
     log.info("Demarrage de l’initialisation des donnees");
     log.info("===========================================");

     // Simulation de chargement de donnees
     log.info("Chargement des categories de produits ...");
     Thread.sleep(500);
     log.info("Categories chargees : Plantes , Outils , Engrais");

     log.info("Chargement des produits initiaux ...");
     Thread.sleep(500);
     log.info("10 produits charges avec succes");

     log.info("===========================================");
     log.info("Initialisation terminee avec succes");
     log.info("===========================================");
 }
}