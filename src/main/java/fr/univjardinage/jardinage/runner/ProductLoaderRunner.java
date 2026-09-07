package fr.univjardinage.jardinage.runner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
 @Slf4j
 @Component
 @Order (3)
 public class ProductLoaderRunner implements CommandLineRunner {

@Override
 public void run(String ... args) throws Exception {
log.info("[ORDRE 3] Chargement des produits de jardinage");

  List <String > products = Arrays.asList(
  "Rosier grimpant - Categorie: Plantes - Prix: 15.99 EUR",
 "Tondeuse electrique - Categorie: Outils - Prix: 299.99 EUR",
  "Engrais bio 5kg - Categorie: Engrais - Prix: 12.50 EUR",
  "Terreau universel 50L - Categorie: Terre - Prix: 8.90 EUR",
  " S c a t e u r professionnel - Categorie: Outils - Prix: 45.00 EUR"
  );

   log.info("Nombre de produits a charger : {}", products.size());

   products.forEach(product -> {
   log.debug("Produit charge : {}", product);
   });
  log.info("Tous les produits ont ete charges avec succes");
  }
}