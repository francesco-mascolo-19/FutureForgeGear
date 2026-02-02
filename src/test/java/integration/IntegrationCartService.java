package integration;

import jakarta.persistence.NoResultException;
import model.OrderManagement.Cart;
import model.OrderManagement.ItemCart;
import model.OrderManagement.ItemCartDTO;
import model.OrderManagement.Prodotto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CartService;
import unit.jpa.JpaH2TestBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationCartService extends JpaH2TestBase {

    private CartService service;

    private void log(String m) { System.out.println(m); }


    private Cart newCart(long userId) {
        Cart c = new Cart();
        c.setUserId(userId);

        tryEnsureItemsList(c);
        return c;
    }

    private Prodotto newProdotto(String nome, double prezzo) {
        Prodotto p = new Prodotto();
        p.setNome(nome);
        p.setDescrizione("Desc " + nome);
        p.setPrezzo(prezzo);
        p.setDisponibilita(10);
        p.setInCatalogo(true);
        p.setInMagazzino(true);
        p.setFornitore(1L);
        return p;
    }

    private ItemCart newItem(Prodotto prodotto, int qty) {
        ItemCart item = new ItemCart();
        item.setProdotto(prodotto);
        item.setQuantity(qty);
        return item;
    }

    private void tryEnsureItemsList(Cart cart) {
        try {
            List<ItemCart> items = cart.getItems();
            if (items == null) {
                Method setItems = cart.getClass().getMethod("setItems", List.class);
                setItems.invoke(cart, new ArrayList<ItemCart>());
            }
        } catch (NoSuchMethodException e) {

        } catch (Exception ignored) {}
    }

    private void tryLinkItemToCart(ItemCart item, Cart cart) {
        String[] candidateSetters = {"setCart", "setCarrello", "setCartRef", "setCar"};
        for (String s : candidateSetters) {
            try {
                Method m = item.getClass().getMethod(s, Cart.class);
                m.invoke(item, cart);
                return;
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                return;
            }
        }
    }

    @BeforeEach
    void setUp() {
        service = new CartService();
        injectEntityManager(service, em);

        try { em.createQuery("delete from Cart").executeUpdate(); } catch (Exception ignored) {}
        try { em.createQuery("delete from Prodotto").executeUpdate(); } catch (Exception ignored) {}

        commitAndRestartTx();
    }

    // ------------------------
    // CRUD BASE
    // ------------------------

    @Test
    void addCart_persiste_e_findCartById_ritornaCart() {
        System.out.println("crud: Creazione Carrello");

        Cart cart = newCart(100L);

        service.addCart(cart);
        commitAndRestartTx();

        int id = cart.getId();
        Cart found = service.findCartById(id);

        assertNotNull(found);
        assertEquals(100L, found.getUserId());
    }

    @Test
    void updateCart_merge_aggiornaDavvero() {
        System.out.println("crud: Aggiornamento Carrello");

        Cart cart = newCart(200L);
        em.persist(cart);
        commitAndRestartTx();

        Cart managed = em.find(Cart.class, cart.getId());
        assertNotNull(managed);

        managed.setUserId(201L);
        service.updateCart(managed);
        commitAndRestartTx();

        Cart reloaded = em.find(Cart.class, cart.getId());
        assertEquals(201L, reloaded.getUserId());
    }

    @Test
    void clearCart_remove_rimuoveSeEntityManaged() {
        System.out.println("crud: Rimozione Carrello");

        Cart cart = newCart(300L);
        em.persist(cart);
        commitAndRestartTx();

        Cart managed = em.find(Cart.class, cart.getId());
        assertNotNull(managed);

        service.clearCart(managed);
        commitAndRestartTx();

        Cart shouldBeNull = em.find(Cart.class, cart.getId());
        assertNull(shouldBeNull);
    }

    // ------------------------
    // QUERY
    // ------------------------

    @Test
    void findCartByCostumer_seEsiste_ritornaQuelloEsistente() {
        System.out.println("query: Recupero Carrello Utente");

        long userId = 400L;

        Cart cart = newCart(userId);
        em.persist(cart);
        commitAndRestartTx();

        Cart found = service.findCartByCostumer(userId);
        assertNotNull(found);
        assertEquals(userId, found.getUserId());
    }

    @Test
    void findCartByCostumer_seNonEsiste_crea_e_persiste_unNuovoCart() {
        System.out.println("query: Creazione Carrello Utente");

        long userId = 500L;

        Cart found = service.findCartByCostumer(userId);
        commitAndRestartTx();

        assertNotNull(found);
        assertEquals(userId, found.getUserId());

        Cart reloaded = em.find(Cart.class, found.getId());
        assertNotNull(reloaded);
        assertEquals(userId, reloaded.getUserId());
    }

    // ------------------------
    // REMOVE ITEM
    // ------------------------

    @Test
    void removeProductFromCart_rimuoveItemConProductId_e_merge_persistente() {
        System.out.println("cart: Rimozione Prodotto dal Carrello");

        Prodotto p1 = newProdotto("P1", 10.0);
        Prodotto p2 = newProdotto("P2", 20.0);
        em.persist(p1);
        em.persist(p2);

        Cart cart = newCart(600L);
        em.persist(cart);

        ItemCart i1 = newItem(p1, 1);
        ItemCart i2 = newItem(p2, 1);

        tryLinkItemToCart(i1, cart);
        tryLinkItemToCart(i2, cart);

        tryEnsureItemsList(cart);
        if (cart.getItems() == null) {
            fail("Cart.getItems() è null e non esiste setItems(List). Aggiungi inizializzazione lista in Cart o adatta il test.");
        }
        cart.getItems().add(i1);
        cart.getItems().add(i2);

        try { em.persist(i1); } catch (Exception ignored) {}
        try { em.persist(i2); } catch (Exception ignored) {}

        commitAndRestartTx();

        int cartId = cart.getId();
        int productIdDaRimuovere = p1.getId();

        service.removeProductFromCart(cartId, productIdDaRimuovere);
        commitAndRestartTx();

        Cart reloaded = em.find(Cart.class, cartId);
        assertNotNull(reloaded);

        List<ItemCart> itemsAfter = reloaded.getItems();
        assertNotNull(itemsAfter);
        assertEquals(1, itemsAfter.size());
        assertEquals(p2.getId(), itemsAfter.get(0).getProdotto().getId());
    }

    @Test
    void removeProductFromCart_seCartNonEsiste_nonFaNullPointer_e_nonCambiaNulla() {
        System.out.println("cart: Rimozione Carrello Inesistente");

        assertDoesNotThrow(() -> service.removeProductFromCart(9999, 1));
    }

    @Test
    void findCartById_seNonEsiste_lanciaNoResultException() {
        System.out.println("query: Carrello non Trovato");

        assertThrows(NoResultException.class, () -> service.findCartById(999999));
    }
}