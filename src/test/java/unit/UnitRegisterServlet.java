package unit;

import control.RegisterServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitRegisterServlet {

    private RegisterServlet registerServlet;

    @BeforeEach
    void setUp() {
        registerServlet = new RegisterServlet();
    }

    // validateEmail

    @Test
    void validateEmail_emailNull_returnsFalse() {
        assertFalse(registerServlet.validateEmail(null));
    }

    @Test
    void validateEmail_emailVuota_returnsFalse() {
        assertFalse(registerServlet.validateEmail(""));
    }

    @Test
    void validateEmail_emailTroppoCorta_sotto7Caratteri_returnsFalse() {
        // 6 caratteri: sotto soglia (es: a@b.it = 6)
        assertFalse(registerServlet.validateEmail("a@b.it"));
    }

    @Test
    void validateEmail_emailConCaratteriSpecialiNonAmmessi_returnsFalse() {
        // spazio e '!' sono esempi tipici di caratteri non ammessi
        assertFalse(registerServlet.validateEmail("user!@example.com"));
        assertFalse(registerServlet.validateEmail("user @example.com"));
    }

    @Test
    void validateEmail_emailConPuntiConsecutivi_returnsFalse() {
        assertFalse(registerServlet.validateEmail("nome..cognome@example.com"));
        assertFalse(registerServlet.validateEmail("user@example..com"));
    }

    @Test
    void validateEmail_emailSenzaAt_returnsFalse() {
        assertFalse(registerServlet.validateEmail("user.example.com"));
    }

    @Test
    void validateEmail_emailConPuntoFinale_returnsFalse() {
        assertFalse(registerServlet.validateEmail("user@example.com."));
    }

    @Test
    void validateEmail_emailValidaSemplice_returnsTrue() {
        assertTrue(registerServlet.validateEmail("user@example.com"));
    }

    @Test
    void validateEmail_emailValidaConPuntoNelNome_returnsTrue() {
        assertTrue(registerServlet.validateEmail("nome.cognome@example.com"));
    }

    // validatePassword


    @Test
    void validatePassword_passwordNull_returnsFalse() {
        assertFalse(registerServlet.validatePassword(null));
    }

    @Test
    void validatePassword_passwordVuota_returnsFalse() {
        assertFalse(registerServlet.validatePassword(""));
    }

    @Test
    void validatePassword_passwordSenzaMaiuscola_returnsFalse() {
        assertFalse(registerServlet.validatePassword("abcd1"));
    }

    @Test
    void validatePassword_passwordSenzaNumero_returnsFalse() {
        assertFalse(registerServlet.validatePassword("Abcdef"));
    }

    @Test
    void validatePassword_passwordTroppoCorta_returnsFalse() {
        // min 4 caratteri, ma deve avere maiuscola e numero: "A1a" è 3
        assertFalse(registerServlet.validatePassword("A1a"));
    }

    @Test
    void validatePassword_passwordTroppoLunga_returnsFalse() {
        // 21 caratteri (max 20), con maiuscola e numero
        assertFalse(registerServlet.validatePassword("A12345678901234567890"));
    }

    @Test
    void validatePassword_passwordValidaMinima_returnsTrue() {
        assertTrue(registerServlet.validatePassword("Abc1"));
    }

    @Test
    void validatePassword_passwordValida_returnsTrue() {
        assertTrue(registerServlet.validatePassword("Password1"));
    }

    @Test
    void validatePassword_passwordConCaratteriSpeciali_returnsFalse() {
        // regex: solo lettere e numeri
        assertFalse(registerServlet.validatePassword("Passw0rd!"));
    }

    // stampa finale


    @Test
    void fineSuite_println() {
        System.out.println("UnitRegisterServlet: test completati.");
        assertTrue(true);
    }
}
