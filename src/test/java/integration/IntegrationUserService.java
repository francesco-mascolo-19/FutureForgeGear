package integration;

import enumerativeTypes.Ruolo;
import jakarta.persistence.NoResultException;
import model.UserManagement.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import unit.jpa.JpaH2TestBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationUserService extends JpaH2TestBase {

    private UserService service;

    private Ruolo anyRuolo() {
        return Ruolo.values()[0];
    }

    private Ruolo adminRuoloOrFallback() {
        for (Ruolo r : Ruolo.values()) {
            if (r.name().equalsIgnoreCase("ADMIN")) return r;
        }
        return anyRuolo();
    }

    private Utente newUtente(String nome, String cognome, String email, String password, Ruolo ruolo) {
        return new Utente(nome, cognome, email, password, ruolo);
    }

    @BeforeEach
    void setUp() {
        service = new UserService();
        injectEntityManager(service, em);

        try { em.createQuery("delete from Utente").executeUpdate(); } catch (Exception ignored) {}
        commitAndRestartTx();
    }

    // ------------------------
    // CRUD
    // ------------------------

    @Test
    void addUser_creazioneUtente() {
        System.out.println("Creazione utente");

        Utente u = newUtente("Mario", "Rossi", "mario@test.it", "pass", anyRuolo());

        service.addUser(u);
        commitAndRestartTx();

        long id = u.getId();
        Utente found = service.findUserById(id);
        assertNotNull(found);
        assertEquals("mario@test.it", found.getEmail());
    }

    @Test
    void updateUser_aggiornamentoUtente() {
        System.out.println("Aggiornamento utente");

        Utente u = newUtente("Luigi", "Verdi", "luigi@test.it", "pass", anyRuolo());
        em.persist(u);
        commitAndRestartTx();

        long id = u.getId();
        Utente managed = em.find(Utente.class, id);
        assertNotNull(managed);

        managed.setNome("LuigiNew");
        service.updateUser(managed);
        commitAndRestartTx();

        Utente reloaded = em.find(Utente.class, id);
        assertNotNull(reloaded);
        assertEquals("LuigiNew", reloaded.getNome());
    }

    @Test
    void removeUser_rimozioneUtente() {
        System.out.println("Rimozione utente");

        Utente u = newUtente("Anna", "Bianchi", "anna@test.it", "pass", anyRuolo());
        em.persist(u);
        commitAndRestartTx();

        long id = u.getId();
        Utente managed = em.find(Utente.class, id);
        assertNotNull(managed);

        service.removeUser(managed);
        commitAndRestartTx();

        Utente shouldBeNull = em.find(Utente.class, id);
        assertNull(shouldBeNull);
    }

    // ------------------------
    // FIND
    // ------------------------

    @Test
    void findUserById_ricercaPerId() {
        System.out.println("Ricerca utente per ID");

        Utente u = newUtente("Piero", "Neri", "piero@test.it", "pass", anyRuolo());
        em.persist(u);
        commitAndRestartTx();

        long id = u.getId();
        Utente found = service.findUserById(id);
        assertNotNull(found);
        assertEquals("piero@test.it", found.getEmail());
    }

    @Test
    void findUserByEmail_ricercaPerEmail() {
        System.out.println("Ricerca utente per email");

        em.persist(newUtente("Giulia", "Blu", "giulia@test.it", "pass", anyRuolo()));
        commitAndRestartTx();

        Utente found = service.findUserByEmail("giulia@test.it");
        assertNotNull(found);
        assertEquals("Giulia", found.getNome());
    }

    @Test
    void findUserByEmail_emailNonTrovata() {
        System.out.println("Email utente non trovata");

        assertThrows(NoResultException.class, () -> service.findUserByEmail("inesistente@test.it"));
    }

    @Test
    void findAllUsers_listaUtenti() {
        System.out.println("Lista utenti");

        em.persist(newUtente("U1", "X", "u1@test.it", "pass", anyRuolo()));
        em.persist(newUtente("U2", "Y", "u2@test.it", "pass", anyRuolo()));
        commitAndRestartTx();

        List<Utente> all = service.findAllUsers();
        assertNotNull(all);
        assertEquals(2, all.size());
    }

    // ------------------------
    // LOGIN / RUOLI
    // ------------------------

    @Test
    void isLogged_loginUtente() {
        System.out.println("Login utente");

        em.persist(newUtente("Carlo", "Z", "carlo@test.it", "pass", anyRuolo()));
        commitAndRestartTx();

        Utente input = new Utente();
        input.setEmail("carlo@test.it");

        assertTrue(service.isLogged(input));
    }

    @Test
    void isLogged_loginUtenteInesistente() {
        System.out.println("Login utente inesistente");

        Utente input = new Utente();
        input.setEmail("ghost@test.it");

        assertThrows(NoResultException.class, () -> service.isLogged(input));
    }

    @Test
    void isAdmin_verificaAdmin() {
        System.out.println("Verifica admin");

        Ruolo admin = adminRuoloOrFallback();
        em.persist(newUtente("Root", "Admin", "root@test.it", "pass", admin));
        commitAndRestartTx();

        Utente input = new Utente();
        input.setEmail("root@test.it");

        assertFalse(service.isAdmin(input));
    }
}
