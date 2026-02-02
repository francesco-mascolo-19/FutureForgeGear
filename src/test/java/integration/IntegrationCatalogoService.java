package integration;

import enumerativeTypes.Categoria;
import jakarta.persistence.NoResultException;
import model.OrderManagement.Prodotto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Catalogo;
import unit.jpa.JpaH2TestBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationCatalogoService extends JpaH2TestBase {

    private Catalogo service;

    private void log(String m) { System.out.println(m); }

    private Categoria anyCategoria() {
        return Categoria.values()[0];
    }

    private Prodotto newProdotto(String nome, double prezzo, Categoria categoria,
                                 boolean inCatalogo, boolean inMagazzino, long fornitoreId) {
        Prodotto p = new Prodotto();
        p.setNome(nome);
        p.setDescrizione("Descrizione test: " + nome);
        p.setPrezzo(prezzo);
        p.setCategoria(categoria);
        p.setDisponibilita(10);
        p.setInCatalogo(inCatalogo);
        p.setInMagazzino(inMagazzino);
        p.setFornitore(fornitoreId);
        return p;
    }

    @BeforeEach
    void setUp() {
        service = new Catalogo();
        injectEntityManager(service, em);

        em.createQuery("delete from Prodotto").executeUpdate();
        commitAndRestartTx();
    }

    // ------------------------
    // CRUD BASE
    // ------------------------

    @Test
    void addProduct_persiste_e_getProducts_vedeIlProdotto() {
        System.out.println("crud: add + list");

        Prodotto p = newProdotto("Mouse", 19.99, anyCategoria(), true, true, 1L);

        service.addProduct(p);
        commitAndRestartTx();

        List<Prodotto> all = service.getProducts();
        assertEquals(1, all.size());
        assertEquals("Mouse", all.get(0).getNome());
    }

    @Test
    void updateProduct_merge_aggiornaDavvero() {
        System.out.println("crud: merge");

        Prodotto p = newProdotto("Tastiera", 49.99, anyCategoria(), true, true, 2L);
        em.persist(p);
        commitAndRestartTx();

        Prodotto managed = em.find(Prodotto.class, p.getId());
        assertNotNull(managed);

        managed.setPrezzo(59.99);
        service.updateProduct(managed);
        commitAndRestartTx();

        Prodotto reloaded = em.find(Prodotto.class, p.getId());
        assertEquals(59.99, reloaded.getPrezzo());
    }

    @Test
    void removeProduct_rimuoveSeEntityManaged() {
        System.out.println("crud: remove");

        Prodotto p = newProdotto("Cuffie", 29.99, anyCategoria(), true, true, 3L);
        em.persist(p);
        commitAndRestartTx();

        Prodotto managed = em.find(Prodotto.class, p.getId());
        assertNotNull(managed);

        service.removeProduct(managed);
        commitAndRestartTx();

        Prodotto shouldBeNull = em.find(Prodotto.class, p.getId());
        assertNull(shouldBeNull);
    }

    // ------------------------
    // TOGGLE SU ENTITY
    // ------------------------

    @Test
    void add_remove_catalogo_suEntity_funzionano() {
        System.out.println("toggle: catalogo (entity)");

        Prodotto p = newProdotto("Stampante", 120.0, anyCategoria(), false, true, 4L);
        em.persist(p);
        commitAndRestartTx();

        Prodotto managed = em.find(Prodotto.class, p.getId());
        assertFalse(managed.isInCatalogo());

        service.addProductToCatalogo(managed);
        commitAndRestartTx();

        Prodotto reloaded1 = em.find(Prodotto.class, p.getId());
        assertTrue(reloaded1.isInCatalogo());

        service.removeProductFromCatalogo(reloaded1);
        commitAndRestartTx();

        Prodotto reloaded2 = em.find(Prodotto.class, p.getId());
        assertFalse(reloaded2.isInCatalogo());
    }

    @Test
    void add_remove_magazzino_suEntity_funzionano_e_remove_setta_anche_catalogo_false() {
        System.out.println("toggle: magazzino (entity)");

        Prodotto p = newProdotto("SSD", 99.0, anyCategoria(), true, false, 5L);
        em.persist(p);
        commitAndRestartTx();

        Prodotto managed = em.find(Prodotto.class, p.getId());
        assertFalse(managed.isInMagazzino());

        service.addProductToMagazzino(managed);
        commitAndRestartTx();

        Prodotto reloaded1 = em.find(Prodotto.class, p.getId());
        assertTrue(reloaded1.isInMagazzino());

        service.removeProductFromMagazzino(reloaded1);
        commitAndRestartTx();

        Prodotto reloaded2 = em.find(Prodotto.class, p.getId());
        assertFalse(reloaded2.isInMagazzino());
        assertFalse(reloaded2.isInCatalogo());
    }

    // ------------------------
    // UPDATE CON VALIDAZIONE
    // ------------------------

    @Test
    void updateProductName_conNomeVuoto_nonModificaNiente_e_nonToccaDB() {
        System.out.println("nome non valido");

        Prodotto p = newProdotto("NomeVecchio", 100.0, anyCategoria(), true, true, 6L);
        em.persist(p);
        commitAndRestartTx();

        int id = p.getId();

        service.updateProductName((long) id, "");
        commitAndRestartTx();

        Prodotto after = em.find(Prodotto.class, id);
        assertEquals("NomeVecchio", after.getNome());
    }

    @Test
    void updateProductPrice_conPrezzoInvalido_nonModificaNiente() {
        System.out.println("prezzo non valido");

        Prodotto p = newProdotto("Prod", 100.0, anyCategoria(), true, true, 7L);
        em.persist(p);
        commitAndRestartTx();

        int id = p.getId();

        service.updateProductPrice((long) id, 0);
        commitAndRestartTx();

        Prodotto after = em.find(Prodotto.class, id);
        assertEquals(100.0, after.getPrezzo());
    }

    @Test
    void updateProductName_conIdLong_valido_aggiornaNome() {
        System.out.println("nome valido");

        Prodotto p = newProdotto("Old", 10.0, anyCategoria(), true, true, 8L);
        em.persist(p);
        commitAndRestartTx();

        long idLong = (long) p.getId();

        service.updateProductName(idLong, "NewName");
        commitAndRestartTx();

        Prodotto after = em.find(Prodotto.class, p.getId());
        assertEquals("NewName", after.getNome());
    }

    @Test
    void updateProductDesc_conIdLong_valido_aggiornaDescrizione() {
        System.out.println("descrizione valida");

        Prodotto p = newProdotto("X", 10.0, anyCategoria(), true, true, 9L);
        em.persist(p);
        commitAndRestartTx();

        long idLong = (long) p.getId();

        service.updateProductDesc(idLong, "Nuova descrizione");
        commitAndRestartTx();

        Prodotto after = em.find(Prodotto.class, p.getId());
        assertEquals("Nuova descrizione", after.getDescrizione());
    }

    @Test
    void updateProductPrice_conIdLong_valido_aggiornaPrezzo() {
        System.out.println("prezzo valido");

        Prodotto p = newProdotto("Y", 10.0, anyCategoria(), true, true, 10L);
        em.persist(p);
        commitAndRestartTx();

        long idLong = (long) p.getId();

        service.updateProductPrice(idLong, 99.0);
        commitAndRestartTx();

        Prodotto after = em.find(Prodotto.class, p.getId());
        assertEquals(99.0, after.getPrezzo());
    }

    // ------------------------
    // QUERY / NAMED QUERY
    // ------------------------

    @Test
    void getProductsInCatalogo_ritornaSoloQuelliInCatalogo() {
        System.out.println("query: inCatalogo");

        em.persist(newProdotto("InCat", 10.0, anyCategoria(), true, false, 11L));
        em.persist(newProdotto("OutCat", 20.0, anyCategoria(), false, true, 11L));
        commitAndRestartTx();

        List<Prodotto> inCatalogo = service.getProductsInCatalogo();
        assertEquals(1, inCatalogo.size());
        assertTrue(inCatalogo.get(0).isInCatalogo());
        assertEquals("InCat", inCatalogo.get(0).getNome());
    }

    @Test
    void getProductsInMagazzino_ritornaSoloQuelliInMagazzino() {
        System.out.println("query: inMagazzino");

        em.persist(newProdotto("InMag", 10.0, anyCategoria(), false, true, 12L));
        em.persist(newProdotto("OutMag", 20.0, anyCategoria(), true, false, 12L));
        commitAndRestartTx();

        List<Prodotto> inMag = service.getProductsInMagazzino();
        assertEquals(1, inMag.size());
        assertTrue(inMag.get(0).isInMagazzino());
        assertEquals("InMag", inMag.get(0).getNome());
    }

    @Test
    void findByName_ok_e_nomeInesistente_vuoto() {
        System.out.println("query: findByName");

        em.persist(newProdotto("Monitor", 150.0, anyCategoria(), true, true, 13L));
        commitAndRestartTx();

        List<Prodotto> found = service.findByName("Monitor");
        assertEquals(1, found.size());
        assertEquals("Monitor", found.get(0).getNome());

        List<Prodotto> empty = service.findByName("NON_ESISTE");
        assertTrue(empty.isEmpty());
    }

    @Test
    void findProductByID_esiste_ok_nonEsiste_NoResult() {
        System.out.println("query: byId (NoResult)");

        Prodotto p = newProdotto("Router", 60.0, anyCategoria(), true, true, 14L);
        em.persist(p);
        commitAndRestartTx();

        int id = p.getId();

        Prodotto found = service.findProductByID(id);
        assertNotNull(found);
        assertEquals("Router", found.getNome());

        assertThrows(NoResultException.class, () -> service.findProductByID(999999));
    }

    @Test
    void findProductByFornitore_ok() {
        System.out.println("query: byFornitore");

        long forn1 = 100L;
        long forn2 = 200L;

        em.persist(newProdotto("A", 10.0, anyCategoria(), true, true, forn1));
        em.persist(newProdotto("B", 20.0, anyCategoria(), true, true, forn1));
        em.persist(newProdotto("C", 30.0, anyCategoria(), true, true, forn2));
        commitAndRestartTx();

        List<Prodotto> byF1 = service.findProductByFornitore(forn1);
        assertEquals(2, byF1.size());

        List<Prodotto> byF2 = service.findProductByFornitore(forn2);
        assertEquals(1, byF2.size());
    }

    @Test
    void findByMinusPrize_e_findByMajorPrize_funzionano() {
        System.out.println("query: price range");

        em.persist(newProdotto("P1", 10.0, anyCategoria(), true, true, 300L));
        em.persist(newProdotto("P2", 50.0, anyCategoria(), true, true, 300L));
        em.persist(newProdotto("P3", 100.0, anyCategoria(), true, true, 300L));
        commitAndRestartTx();

        List<Prodotto> min50 = service.findByMinusPrize(50.0);
        assertEquals(2, min50.size());

        List<Prodotto> maj50 = service.findByMajorPrize(50.0);
        assertEquals(2, maj50.size());

        List<Prodotto> empty = service.findByMajorPrize(1000.0);
        assertTrue(empty.isEmpty());
    }

    @Test
    void findByCategory_ok_e_vuoto_seCategoriaAssente() {
        System.out.println("query: category");

        em.persist(newProdotto("PC Fisso", 500.0, Categoria.FISSI, true, true, 400L));
        em.persist(newProdotto("Laptop", 900.0, Categoria.PORTATILI, true, true, 400L));
        commitAndRestartTx();

        List<Prodotto> fissi = service.findByCategory(Categoria.FISSI);
        assertEquals(1, fissi.size());
        assertEquals("PC Fisso", fissi.get(0).getNome());

        List<Prodotto> perif = service.findByCategory(Categoria.PERIFERICHE);
        assertTrue(perif.isEmpty());
    }

    // ------------------------
    // METODI CON ID
    // ------------------------

    @Test
    void add_remove_catalogo_conIdLong_funzionano() {
        System.out.println("toggle: catalogo (id)");

        Prodotto p = newProdotto("X", 10.0, anyCategoria(), false, false, 500L);
        em.persist(p);
        commitAndRestartTx();

        long idLong = (long) p.getId();

        service.addProductToCatalogo(idLong);
        commitAndRestartTx();

        Prodotto afterAdd = em.find(Prodotto.class, p.getId());
        assertTrue(afterAdd.isInCatalogo());

        service.removeProductFromCatalogo(idLong);
        commitAndRestartTx();

        Prodotto afterRemove = em.find(Prodotto.class, p.getId());
        assertFalse(afterRemove.isInCatalogo());
    }

    @Test
    void add_remove_magazzino_conIdLong_funzionano() {
        System.out.println("toggle: magazzino (id)");

        Prodotto p = newProdotto("Y", 10.0, anyCategoria(), false, false, 600L);
        em.persist(p);
        commitAndRestartTx();

        long idLong = (long) p.getId();

        service.addProductToMagazzino(idLong);
        commitAndRestartTx();

        Prodotto afterAdd = em.find(Prodotto.class, p.getId());
        assertTrue(afterAdd.isInMagazzino());

        service.removeProductFromMagazzino(idLong);
        commitAndRestartTx();

        Prodotto afterRemove = em.find(Prodotto.class, p.getId());
        assertFalse(afterRemove.isInMagazzino());
        assertFalse(afterRemove.isInCatalogo());
    }
}
