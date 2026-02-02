package unit.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;

public abstract class JpaH2TestBase {

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeAll
    static void initEmf() {
        emf = Persistence.createEntityManagerFactory("FutureForgeGearPU-test");
    }

    @AfterAll
    static void closeEmf() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void openEm() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    void closeEm() {
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // ogni test pulito
            }
        } finally {
            if (em != null) em.close();
        }
    }

    /** Commit quando vuoi rendere "visibile" la scrittura nel DB durante il test */
    protected void commitAndRestartTx() {
        em.getTransaction().commit();
        em.getTransaction().begin();
    }

    /** Sostituisce @PersistenceContext nei test: inietta em nel field "em" del service */
    protected static void injectEntityManager(Object service, EntityManager em) {
        try {
            Field f = service.getClass().getDeclaredField("em");
            f.setAccessible(true);
            f.set(service, em);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile iniettare EntityManager nel service.", e);
        }
    }
}
