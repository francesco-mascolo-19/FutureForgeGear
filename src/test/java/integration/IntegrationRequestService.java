package integration;

import enumerativeTypes.StatoRichiesta;
import jakarta.persistence.NoResultException;
import model.RequestManagement.OrderRequest;
import model.RequestManagement.ProductRequest;
import model.RequestManagement.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RequestService;
import unit.jpa.JpaH2TestBase;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationRequestService extends JpaH2TestBase {

    private RequestService service;

    private void log(String m) { System.out.println(m); }

    private ProductRequest newProductRequest(Long magazzId, Long fornId, LocalDateTime when,
                                             int prodId, int qty, String msg) {
        return new ProductRequest(magazzId, fornId, when, prodId, qty, msg);
    }

    private OrderRequest newOrderRequest(Long magazzId, Long gestoreOrdiniId, LocalDateTime when,
                                         Long ordineId, String msg) {
        return new OrderRequest(magazzId, gestoreOrdiniId, when, ordineId, msg);
    }

    @BeforeEach
    void setUp() {
        service = new RequestService();
        injectEntityManager(service, em);

        em.createQuery("delete from ProductRequest").executeUpdate();
        em.createQuery("delete from OrderRequest").executeUpdate();
        em.createQuery("delete from Request").executeUpdate();
        commitAndRestartTx();
    }

    // ------------------------
    // CRUD BASE
    // ------------------------

    @Test
    void addRequest_persiste_e_findAll_vedeLeRichieste() {
        log("crud: add + findAll");

        ProductRequest r1 = newProductRequest(10L, 100L, LocalDateTime.now().minusDays(1),
                1, 5, "msg1");
        OrderRequest r2 = newOrderRequest(11L, 101L, LocalDateTime.now(),
                999L, "msg2");

        service.addRequest(r1);
        service.addRequest(r2);
        commitAndRestartTx();

        List<Request> all = service.findAll();
        assertEquals(2, all.size());

        System.out.println("OK -> addRequest + findAll: persistenza e conteggio corretto");
    }

    @Test
    void updateRequest_merge_aggiornaDavvero_message() {
        log("crud: merge (update message)");

        ProductRequest r = newProductRequest(20L, 200L, LocalDateTime.now().minusHours(2),
                2, 1, "old");
        em.persist(r);
        commitAndRestartTx();

        Request managed = em.find(Request.class, r.getId());
        assertNotNull(managed);

        managed.setMessage("new-message");
        service.updateRequest(managed);
        commitAndRestartTx();

        Request reloaded = em.find(Request.class, r.getId());
        assertEquals("new-message", reloaded.getMessage());

        System.out.println("OK -> updateRequest: message aggiornato correttamente");
    }

    @Test
    void removeRequest_suProductRequest_rimuoveDavvero() {
        log("crud: remove ProductRequest");

        ProductRequest r = newProductRequest(30L, 300L, LocalDateTime.now(),
                3, 10, "to-remove");
        em.persist(r);
        commitAndRestartTx();

        Request managed = em.find(Request.class, r.getId());
        assertNotNull(managed);
        assertTrue(managed instanceof ProductRequest);

        service.removeRequest(managed);
        commitAndRestartTx();

        Request after = em.find(Request.class, r.getId());
        assertNull(after);

        System.out.println("OK -> removeRequest(ProductRequest): rimozione avvenuta");
    }

    @Test
    void removeRequest_suOrderRequest_rimuoveDavvero() {
        log("crud: remove OrderRequest");

        OrderRequest r = newOrderRequest(31L, 301L, LocalDateTime.now(),
                12345L, "to-remove");
        em.persist(r);
        commitAndRestartTx();

        Request managed = em.find(Request.class, r.getId());
        assertNotNull(managed);
        assertTrue(managed instanceof OrderRequest);

        service.removeRequest(managed);
        commitAndRestartTx();

        Request after = em.find(Request.class, r.getId());
        assertNull(after);

        System.out.println("OK -> removeRequest(OrderRequest): rimozione avvenuta");
    }

    // ------------------------
    // FIND / NAMED QUERY
    // ------------------------

    @Test
    void findById_esiste_ok_nonEsiste_NoResult() {
        log("query: findById (NoResult)");

        ProductRequest r = newProductRequest(40L, 400L, LocalDateTime.now(),
                4, 2, "findme");
        em.persist(r);
        commitAndRestartTx();

        Request found = service.findById((long) r.getId());
        assertNotNull(found);
        assertEquals(r.getId(), found.getId());

        assertThrows(NoResultException.class, () -> service.findById(999999L));

        System.out.println("OK -> findById: trovato esistente e NoResult su id inesistente");
    }

    @Test
    void findAll_ritornaTutto() {
        log("query: findAll");

        em.persist(newProductRequest(1L, 10L, LocalDateTime.now().minusDays(2), 1, 1, "a"));
        em.persist(newOrderRequest(2L, 20L, LocalDateTime.now().minusDays(1), 10L, "b"));
        em.persist(newProductRequest(3L, 30L, LocalDateTime.now(), 2, 2, "c"));
        commitAndRestartTx();

        List<Request> all = service.findAll();
        assertEquals(3, all.size());

        System.out.println("OK -> findAll: conteggio corretto");
    }

    @Test
    void findByMagazziniere_ritornaSoloQuelMagazziniere() {
        log("query: byMagazziniere");

        long mag1 = 1000L;
        long mag2 = 2000L;

        em.persist(newProductRequest(mag1, 10L, LocalDateTime.now().minusHours(3), 1, 1, "m1-1"));
        em.persist(newOrderRequest(mag1, 20L, LocalDateTime.now().minusHours(2), 10L, "m1-2"));
        em.persist(newProductRequest(mag2, 30L, LocalDateTime.now().minusHours(1), 2, 2, "m2-1"));
        commitAndRestartTx();

        List<Request> byMag1 = service.findByMagazziniere(mag1);
        assertEquals(2, byMag1.size());
        assertTrue(byMag1.stream().allMatch(r -> r.getMagazziniereID().equals(mag1)));

        List<Request> byMag2 = service.findByMagazziniere(mag2);
        assertEquals(1, byMag2.size());
        assertTrue(byMag2.stream().allMatch(r -> r.getMagazziniereID().equals(mag2)));

        System.out.println("OK -> findByMagazziniere: filtra correttamente per magazziniere");
    }

    @Test
    void findByDestinatario_ritornaSoloQuelDestinatario() {
        log("query: byDestinatario");

        long dest1 = 5000L;
        long dest2 = 6000L;

        em.persist(newProductRequest(1L, dest1, LocalDateTime.now().minusHours(3), 1, 1, "d1-1"));
        em.persist(newOrderRequest(2L, dest1, LocalDateTime.now().minusHours(2), 10L, "d1-2"));
        em.persist(newProductRequest(3L, dest2, LocalDateTime.now().minusHours(1), 2, 2, "d2-1"));
        commitAndRestartTx();

        List<Request> byDest1 = service.findByDestinatario(dest1);
        assertEquals(2, byDest1.size());
        assertTrue(byDest1.stream().allMatch(r -> r.getDestinatarioID().equals(dest1)));

        List<Request> byDest2 = service.findByDestinatario(dest2);
        assertEquals(1, byDest2.size());
        assertTrue(byDest2.stream().allMatch(r -> r.getDestinatarioID().equals(dest2)));

        System.out.println("OK -> findByDestinatario: filtra correttamente per destinatario");
    }

    @Test
    void findByDate_range_inclusivoStart_esclusivoEnd() {
        log("query: byDate range");

        LocalDateTime t0 = LocalDateTime.now().minusDays(10);
        LocalDateTime t1 = LocalDateTime.now().minusDays(5);
        LocalDateTime t2 = LocalDateTime.now().minusDays(1);

        em.persist(newProductRequest(1L, 1L, t0, 1, 1, "out-low"));

        em.persist(newProductRequest(1L, 1L, t1, 2, 1, "in-1"));
        em.persist(newOrderRequest(1L, 1L, t1.plusHours(1), 99L, "in-2"));
        em.persist(newProductRequest(1L, 1L, t2.minusMinutes(1), 3, 1, "in-3"));

        em.persist(newOrderRequest(1L, 1L, t2, 100L, "out-high"));

        commitAndRestartTx();

        List<Request> inRange = service.findByDate(t1, t2);
        assertEquals(3, inRange.size());
        assertTrue(inRange.stream().allMatch(r ->
                !r.getDataOra().isBefore(t1) && r.getDataOra().isBefore(t2)
        ));

        System.out.println("OK -> findByDate: range [start, end) rispettato");
    }

    @Test
    void findByPostDate_ritornaSoloDopoData() {
        log("query: dopo data");

        LocalDateTime pivot = LocalDateTime.now().minusDays(3);

        em.persist(newProductRequest(1L, 1L, pivot.minusSeconds(1), 1, 1, "before"));
        em.persist(newProductRequest(1L, 1L, pivot.plusSeconds(1), 2, 1, "after-1"));
        em.persist(newOrderRequest(1L, 1L, pivot.plusDays(1), 10L, "after-2"));
        commitAndRestartTx();

        List<Request> after = service.findByPostDate(pivot);
        assertEquals(2, after.size());
        assertTrue(after.stream().allMatch(r -> r.getDataOra().isAfter(pivot)));

        System.out.println("OK -> findByPostDate: solo date dopo pivot");
    }

    @Test
    void findByPreviousDate_ritornaSoloPrimaData() {
        log("query: prima data");

        LocalDateTime pivot = LocalDateTime.now().minusDays(3);

        em.persist(newProductRequest(1L, 1L, pivot.minusDays(1), 1, 1, "before-1"));
        em.persist(newOrderRequest(1L, 1L, pivot.minusSeconds(1), 10L, "before-2"));
        em.persist(newProductRequest(1L, 1L, pivot.plusSeconds(1), 2, 1, "after"));
        commitAndRestartTx();

        List<Request> before = service.findByPreviousDate(pivot);
        assertEquals(2, before.size());
        assertTrue(before.stream().allMatch(r -> r.getDataOra().isBefore(pivot)));

        System.out.println("OK -> findByPreviousDate: solo date prima pivot");
    }

    // ------------------------
    // METODI DI BUSINESS
    // ------------------------

    @Test
    void cambiaStato_accettaRichiesta_e_persistenza_statoDiventaAccettato() {
        log("business: cambiaStato");

        ProductRequest r = newProductRequest(70L, 700L, LocalDateTime.now(),
                7, 3, "state");
        em.persist(r);
        commitAndRestartTx();

        Request managed = em.find(Request.class, r.getId());
        assertNotNull(managed);
        assertEquals(StatoRichiesta.NON_ACCETTATO, managed.getStato());

        service.cambiaStato(managed);
        commitAndRestartTx();

        Request after = em.find(Request.class, r.getId());
        assertEquals(StatoRichiesta.ACCETTATO, after.getStato());

        System.out.println("OK -> cambiaStato: stato passato ad ACCETTATO e persistito");
    }

    // ------------------------
    // VALIDAZIONE QUANTITÀ RICHIESTA (Q)
    // ------------------------

    @Test
    void addRequest_productRequest_quantitaMinoreDiUno_nonValidaMaPersistita() {
        log("validation: Q < 1 (NOTA: service non valida, persiste comunque)");

        ProductRequest bad = newProductRequest(80L, 800L, LocalDateTime.now(),
                8, 0, "qty-zero");

        service.addRequest(bad);
        commitAndRestartTx();

        List<Request> all = service.findAll();
        assertEquals(1, all.size());
        assertTrue(all.get(0) instanceof ProductRequest);

        ProductRequest saved = (ProductRequest) all.get(0);
        assertEquals(0, saved.getQuantita());

        System.out.println("OK -> Q < 1: con service attuale NON viene bloccata, viene persistita");
    }

    @Test
    void addRequest_productRequest_quantitaMaggioreUgualeUno_valid() {
        log("validation: Q >= 1 (valid)");

        ProductRequest ok = newProductRequest(81L, 801L, LocalDateTime.now(),
                9, 1, "qty-one");

        service.addRequest(ok);
        commitAndRestartTx();

        List<Request> all = service.findAll();
        assertEquals(1, all.size());
        assertTrue(all.get(0) instanceof ProductRequest);

        ProductRequest saved = (ProductRequest) all.get(0);
        assertEquals(1, saved.getQuantita());

        System.out.println("OK -> Q >= 1: addRequest accetta e persiste");
    }
}
