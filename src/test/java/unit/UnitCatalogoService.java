package unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Catalogo;
import model.OrderManagement.Prodotto;
import static org.junit.jupiter.api.Assertions.*;

class UnitCatalogoService{

    private Catalogo catalogo;

    @BeforeEach
    void setUp() {
        catalogo = new Catalogo();
    }

    @Test
    void validateNameChange_emptyName_returnsFalse() {
        assertFalse(catalogo.validateNameChange(""));
    }

    @Test
    void validateNameChange_nonEmptyName_returnsTrue() {
        assertTrue(catalogo.validateNameChange("mouse"));
    }

    @Test
    void validatePriceChange_zeroOrNegative_returnsFalse() {
        assertFalse(catalogo.validatePriceChange(0.0));
        assertFalse(catalogo.validatePriceChange(-1.0));
    }

    @Test
    void validatePriceChange_positive_returnsTrue() {
        assertTrue(catalogo.validatePriceChange(10.0));
    }

    @Test
    void validateAddToMagazzino_whenAlreadyInMagazzino_returnsFalse() {
        Prodotto p = new Prodotto();
        p.setInMagazzino(true);

        assertFalse(catalogo.validateAddToMagazzino(p));
    }

    @Test
    void validateAddToMagazzino_whenNotInMagazzino_returnsTrue() {
        Prodotto p = new Prodotto();
        p.setInMagazzino(false);

        assertTrue(catalogo.validateAddToMagazzino(p));
    }

    @Test
    void validateRemoveFromMagazzino_whenInMagazzino_returnsTrue() {
        Prodotto p = new Prodotto();
        p.setInMagazzino(true);

        assertTrue(catalogo.validateRemoveFromMagazzino(p));
    }

    @Test
    void validateRemoveFromMagazzino_whenNotInMagazzino_returnsFalse() {
        Prodotto p = new Prodotto();
        p.setInMagazzino(false);

        assertFalse(catalogo.validateRemoveFromMagazzino(p));
    }

    @Test
    void validateAddToCatalogo_whenAlreadyInCatalogo_returnsFalse() {
        Prodotto p = new Prodotto();
        p.setInCatalogo(true);

        assertFalse(catalogo.validateAddToCatalogo(p));
    }

    @Test
    void validateAddToCatalogo_whenNotInCatalogo_returnsTrue() {
        Prodotto p = new Prodotto();
        p.setInCatalogo(false);

        assertTrue(catalogo.validateAddToCatalogo(p));
    }

    @Test
    void validateRemoveFromCatalogo_whenInCatalogo_returnsTrue() {
        Prodotto p = new Prodotto();
        p.setInCatalogo(true);

        assertTrue(catalogo.validateRemoveFromCatalogo(p));
    }

    @Test
    void validateRemoveFromCatalogo_whenNotInCatalogo_returnsFalse() {
        Prodotto p = new Prodotto();
        p.setInCatalogo(false);

        assertFalse(catalogo.validateRemoveFromCatalogo(p));
    }
}