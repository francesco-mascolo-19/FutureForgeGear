package unit;

import control.LoginServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitLoginServlet {

    private LoginServlet loginServlet;

    @BeforeEach
    void setUp() {
        loginServlet = new LoginServlet();
    }

    // validateEmail


    @Test
    void validateEmail_emailCorretta_returnsTrue() {
        System.out.println("TEST: validateEmail -> email corretta");
        assertTrue(loginServlet.validateEmail("user@example.com"));
    }

    @Test
    void validateEmail_emailErrata_returnsFalse() {
        System.out.println("TEST: validateEmail -> email errata");
        assertFalse(loginServlet.validateEmail("wrong@example.com"));
    }


    // validatePassword


    @Test
    void validatePassword_passwordCorretta_returnsTrue() {
        System.out.println("TEST: validatePassword -> password corretta (formato)");
        assertTrue(LoginServlet.validatePassword("correctPassword123"));
    }

    @Test
    void validatePassword_passwordErrata_returnsFalse() {
        System.out.println("TEST: validatePassword -> password errata (formato)");
        assertFalse(LoginServlet.validatePassword("wrongPassword"));
    }

    // validateLogin


    @Test
    void validateLogin_emailEPasswordCorretti_returnsTrue() {
        System.out.println("TEST: validateLogin -> email e password corretti");
        assertTrue(loginServlet.validateLogin(
                "user@example.com",
                "correctPassword123"
        ));
    }

    @Test
    void validateLogin_emailErrata_returnsFalse() {
        System.out.println("TEST: validateLogin -> email errata");
        assertFalse(loginServlet.validateLogin(
                "wrong@example.com",
                "correctPassword123"
        ));
    }

    @Test
    void validateLogin_passwordErrata_returnsFalse() {
        System.out.println("TEST: validateLogin -> password errata");
        assertFalse(loginServlet.validateLogin(
                "user@example.com",
                "wrongPassword"
        ));
    }

    @Test
    void validateLogin_emailEPasswordErrati_returnsFalse() {
        System.out.println("TEST: validateLogin -> email e password errati");
        assertFalse(loginServlet.validateLogin(
                "wrong@example.com",
                "wrongPassword"
        ));
    }


    // password (P) - casi mancanti


    @Test
    void validateLogin_passwordAssociataAllEmailNelDatabase_ifEmailPresente_returnsTrue() {
        System.out.println("TEST: password (P) -> associata all'email nel database (email presente)");
        assertTrue(loginServlet.validateLogin("user@example.com", "correctPassword123"));
    }

    @Test
    void validateLogin_passwordNonAssociataAllEmailNelDatabase_returnsFalse() {
        System.out.println("TEST: password (P) -> NON associata all'email nel database (email presente)");
        assertFalse(loginServlet.validateLogin("user@example.com", "notTheRightPassword123"));
    }

    @Test
    void validateLogin_passwordNonInseritaNelForm_null_returnsFalse() {
        System.out.println("TEST: password (P) -> password non inserita nel form (null)");
        assertFalse(loginServlet.validateLogin("user@example.com", null));
    }

    @Test
    void validateLogin_passwordNonInseritaNelForm_vuota_returnsFalse() {
        System.out.println("TEST: password (P) -> password non inserita nel form (vuota)");
        assertFalse(loginServlet.validateLogin("user@example.com", ""));
    }
}
