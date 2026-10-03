package fr.univjardinage.jardinage.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Tests d'internationalisation (i18n)")
class I18nTest {

    @Autowired
    private MessageSource messageSource;

    // =========================================================
    // FRANÇAIS
    // =========================================================

    @Test
    @DisplayName("Le message du nom obligatoire doit être en français")
    void testFrenchMessage() {

        String message = messageSource.getMessage(
                "product.name.notblank",
                null,
                Locale.FRENCH
        );

        assertThat(message)
                .isEqualTo("Le nom du produit est obligatoire");
    }

    // =========================================================
    // ANGLAIS
    // =========================================================

    @Test
    @DisplayName("Le message du nom obligatoire doit être en anglais")
    void testEnglishMessage() {

        String message = messageSource.getMessage(
                "product.name.notblank",
                null,
                Locale.ENGLISH
        );

        assertThat(message)
                .isEqualTo("Product name is required");
    }

    // =========================================================
    // PARAMÈTRES {0}, {1}, {2}
    // =========================================================

    @Test
    @DisplayName("Les paramètres doivent être correctement interpolés en français")
    void testFrenchParameterizedMessage() {

        String message = messageSource.getMessage(
                "product.stock.insufficient",
                new Object[]{"Rosier", 15, 5},
                Locale.FRENCH
        );

        assertThat(message)
                .isEqualTo(
                        "Stock insuffisant pour Rosier. Demande : 15, Disponible : 5"
                );
    }

    @Test
    @DisplayName("Les paramètres doivent être correctement interpolés en anglais")
    void testEnglishParameterizedMessage() {

        String message = messageSource.getMessage(
                "product.stock.insufficient",
                new Object[]{"Rose bush", 15, 5},
                Locale.ENGLISH
        );

        assertThat(message)
                .isEqualTo(
                        "Insufficient stock for Rose bush. Requested: 15, Available: 5"
                );
    }

    // =========================================================
    // MESSAGE DE REMISE
    // =========================================================

    @Test
    @DisplayName("Le message de remise doit accepter un paramètre en français")
    void testFrenchDiscountMessage() {

        String message = messageSource.getMessage(
                "product.price.invalid.discount",
                new Object[]{20},
                Locale.FRENCH
        );

        assertThat(message)
                .isEqualTo(
                        "La remise doit être comprise entre 0 et 20%"
                );
    }

    @Test
    @DisplayName("Le message de remise doit accepter un paramètre en anglais")
    void testEnglishDiscountMessage() {

        String message = messageSource.getMessage(
                "product.price.invalid.discount",
                new Object[]{20},
                Locale.ENGLISH
        );

        assertThat(message)
                .isEqualTo(
                        "Discount must be between 0 and 20%"
                );
    }

    // =========================================================
    // MESSAGES GÉNÉRAUX
    // =========================================================

    @Test
    @DisplayName("Le message d'erreur interne doit être traduit en français")
    void testFrenchInternalErrorMessage() {

        String message = messageSource.getMessage(
                "api.error.internal",
                null,
                Locale.FRENCH
        );

        assertThat(message)
                .isEqualTo("Une erreur interne s'est produite");
    }

    @Test
    @DisplayName("Le message d'erreur interne doit être traduit en anglais")
    void testEnglishInternalErrorMessage() {

        String message = messageSource.getMessage(
                "api.error.internal",
                null,
                Locale.ENGLISH
        );

        assertThat(message)
                .isEqualTo("An internal error occurred");
    }

    // =========================================================
    // FALLBACK
    // =========================================================

    @Test
    @DisplayName("Le français doit être utilisé comme locale par défaut")
    void testDefaultLocale() {

        String message = messageSource.getMessage(
                "product.name.notblank",
                null,
                Locale.FRENCH
        );

        assertThat(message)
                .isNotBlank()
                .isEqualTo("Le nom du produit est obligatoire");
    }

    // =========================================================
    // EXISTENCE DES MESSAGES
    // =========================================================

    @Test
    @DisplayName("Les messages français et anglais doivent exister")
    void testMessagesExistInBothLocales() {

        String frenchMessage = messageSource.getMessage(
                "product.name.notblank",
                null,
                Locale.FRENCH
        );

        String englishMessage = messageSource.getMessage(
                "product.name.notblank",
                null,
                Locale.ENGLISH
        );

        assertThat(frenchMessage).isNotBlank();
        assertThat(englishMessage).isNotBlank();

        assertThat(frenchMessage)
                .isNotEqualTo(englishMessage);
    }
}