package integration;

import enumerativeTypes.Stato;
import model.OrderManagement.ItemCartDTO;
import model.OrderManagement.Ordine;
import model.UserManagement.GestoreOrdini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import service.OrderService;
import unit.jpa.JpaH2TestBase;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationOrderService extends JpaH2TestBase {

    private OrderService service;

    private void log(String m) {
        System.out.println(m);
    }

    private List<ItemCartDTO> anyItems() {
        return List.of(new ItemCartDTO(1, 2), new ItemCartDTO(5, 1));
    }

    private Ordine newOrdine(long userId, double totale) {
        return new Ordine(userId, totale, anyItems());
    }

    private Stato statoA() {
        return Stato.values()[0];
    }

    private Stato statoBdiversoDa(Stato a) {
        for (Stato s : Stato.values()) {
            if (s != a) return s;
        }
        return a;

        @BeforeEach
        void setUp () {
            service = new OrderService();
            injectEntityManager(service, em);

            try {
                em.createNativeQuery("DELETE FROM Ordine_items").executeUpdate();
            } catch (Exception ignored) {
            }
            try {
                em.createQuery("delete from Ordine").executeUpdate();
            } catch (Exception ignored) {
            }

            try {
                em.createQuery("delete from GestoreOrdini").executeUpdate();
            } catch (Exception ignored) {
            }

            commitAndRestartTx();
        }

        // ------------------------
        // CRUD BASE
        // ------------------------

        @Test
        void addOrder_persiste_e_generaid () {
            log("crud: addOrder + id");

            Ordine o = newOrdine(10L, 50.0);

            Ordine saved = service.addOrder(o);
            commitAndRestartTx();

            assertNotNull(saved.getId());

            Ordine reloaded = em.find(Ordine.class, saved.getId());
            assertNotNull(reloaded);
            assertEquals(50.0, reloaded.getTotale());
            assertEquals(10L, reloaded.getUserId());
            assertNotNull(reloaded.getItems());
            assertFalse(reloaded.getItems().isEmpty());
        }

        @Test
        void findOrderById_esiste_ok_nonEsiste_null () {
            log("crud: findOrderById");

            Ordine o = newOrdine(11L, 70.0);
            em.persist(o);
            commitAndRestartTx();

            int id = o.getId().intValue();

            Ordine found = service.findOrderById(id);
            assertNotNull(found);

            Ordine notFound = service.findOrderById(999999);
            assertNull(notFound);
        }

        @Test
        void findAllOrders_ritornaTutti () {
            log("crud: findAllOrders");

            em.persist(newOrdine(1L, 10.0));
            em.persist(newOrdine(2L, 20.0));
            commitAndRestartTx();

            List<Ordine> all = service.findAllOrders();
            assertEquals(2, all.size());
        }

        @Test
        void updateOrder_merge_aggiornaTotale () {
            log("crud: updateOrder (merge)");

            Ordine o = newOrdine(12L, 99.0);
            em.persist(o);
            commitAndRestartTx();

            Ordine managed = em.find(Ordine.class, o.getId());
            managed.setTotale(199.0);

            service.updateOrder(managed);
            commitAndRestartTx();

            Ordine reloaded = em.find(Ordine.class, o.getId());
            assertEquals(199.0, reloaded.getTotale());
        }

        @Test
        void removeOrder_rimuoveDavvero () {
            log("crud: removeOrder");

            Ordine o = newOrdine(13L, 33.0);
            em.persist(o);
            commitAndRestartTx();

            int id = o.getId().intValue();
            assertNotNull(service.findOrderById(id));

            service.removeOrder(id);
            commitAndRestartTx();

            assertNull(service.findOrderById(id));
        }

        // ------------------------
        // QUERY / NAMED QUERY
        // ------------------------

        @Test
        void findOrdersByCostumer_ritornaSoloQuelliDelCliente () {
            log("query: TROVA_PER_UTENTE");

            long u1 = 500L;
            long u2 = 600L;

            em.persist(newOrdine(u1, 10.0));
            em.persist(newOrdine(u1, 20.0));
            em.persist(newOrdine(u2, 30.0));
            commitAndRestartTx();

            List<Ordine> byU1 = service.findOrdersByCostumer(u1);
            assertEquals(2, byU1.size());
            assertTrue(byU1.stream().allMatch(o -> o.getUserId().equals(u1)));

            List<Ordine> byU2 = service.findOrdersByCostumer(u2);
            assertEquals(1, byU2.size());
            assertTrue(byU2.stream().allMatch(o -> o.getUserId().equals(u2)));
        }

        @Test
        void findOrdersByGestore_ritornaSoloQuelliDelGestore () {
            log("query: TROVA_PER_ID_GESTORE");

            long g1 = 1000L;
            long g2 = 2000L;

            Ordine a = newOrdine(1L, 10.0);
            a.setIdGestore(g1);
            em.persist(a);

            Ordine b = newOrdine(2L, 20.0);
            b.setIdGestore(g1);
            em.persist(b);

            Ordine c = newOrdine(3L, 30.0);
            c.setIdGestore(g2);
            em.persist(c);

            commitAndRestartTx();

            List<Ordine> byG1 = service.findOrdersByGestore(g1);
            assertEquals(2, byG1.size());
            assertTrue(byG1.stream().allMatch(o -> o.getIdGestore() != null && o.getIdGestore().equals(g1)));

            List<Ordine> byG2 = service.findOrdersByGestore(g2);
            assertEquals(1, byG2.size());
            assertTrue(byG2.stream().allMatch(o -> o.getIdGestore() != null && o.getIdGestore().equals(g2)));
        }

        @Test
        void findByPrize_ritornaSoloQuelliConTotaleEsatto () {
            log("query: TROVA_PER_TOTALE");

            em.persist(newOrdine(1L, 10.0));
            em.persist(newOrdine(2L, 10.0));
            em.persist(newOrdine(3L, 99.0));
            commitAndRestartTx();

            List<Ordine> ten = service.findByPrize(10.0);
            assertEquals(2, ten.size());
            assertTrue(ten.stream().allMatch(o -> o.getTotale() == 10.0));
        }

        @Test
        void findByState_ritornaSoloQuelliConStato () {
            log("query: TROVA_PER_STATO");

            Stato target = statoA();
            Stato other = statoBdiversoDa(target);

            Ordine a = newOrdine(1L, 10.0);
            a.setStato(target);
            em.persist(a);

            Ordine b = newOrdine(2L, 20.0);
            b.setStato(target);
            em.persist(b);

            Ordine c = newOrdine(3L, 30.0);
            c.setStato(other);
            em.persist(c);

            commitAndRestartTx();

            List<Ordine> found = service.findByState(target);
            assertEquals(2, found.size());
            assertTrue(found.stream().allMatch(o -> o.getStato() == target));
        }

        @Test
        void findByDate_conFirmaAttuale_DateVsLocalDateTime_lanciaEccezione () {
            log("query: TROVA_PER_DATA (BUG tipo parametro)");

            em.persist(newOrdine(1L, 10.0));
            commitAndRestartTx();

            Date sqlDate = Date.valueOf(LocalDate.now());

            assertThrows(IllegalArgumentException.class, () -> service.findByDate(sqlDate));
        }

        // ------------------------
        // GESTORE ORDINI
        // ------------------------

        @Test
        void findAllGestoreOrdini_ritornaTuttiIGestori () {
            log("query: GestoreOrdini.TROVA_TUTTI");

            em.persist(new GestoreOrdini("Mario", "Rossi", "mario@x.it", "pwd"));
            em.persist(new GestoreOrdini("Luigi", "Verdi", "luigi@x.it", "pwd"));
            commitAndRestartTx();

            List<GestoreOrdini> all = service.findAllGestoreOrdini();
            assertEquals(2, all.size());
        }
    }
}
