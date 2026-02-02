package unit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.OrderManagement.Cart;
import model.OrderManagement.ItemCart;
import model.OrderManagement.Prodotto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CartService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UnitCartService {

    private CartService service;
    private EntityManager em;

    @BeforeEach
    void setUp() throws Exception {
        service = new CartService();
        em = mock(EntityManager.class);

        // injection manuale dell'EntityManager mock
        Field f = CartService.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(service, em);
    }

    @Test
    void findCartByCostumer_quandoEsiste_ritornaCart_e_nonPersist() {
        System.out.println("[UNIT] findCartByCostumer: cart ESISTENTE -> ritorna cart, niente persist");

        long userId = 123L;

        @SuppressWarnings("unchecked")
        TypedQuery<Cart> q = (TypedQuery<Cart>) mock(TypedQuery.class);
        Cart existing = mock(Cart.class);

        when(em.createNamedQuery("Cart.TROVA_COSTUMER", Cart.class)).thenReturn(q);
        when(q.setParameter("userId", userId)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(existing);

        Cart result = service.findCartByCostumer(userId);

        assertSame(existing, result);
        verify(em, never()).persist(any(Cart.class));
    }

    @Test
    void findCartByCostumer_quandoNonEsiste_creaNuovoCart_e_persist() {
        System.out.println("[UNIT] findCartByCostumer: cart NON ESISTENTE -> crea nuovo cart e persist");

        long userId = 456L;

        @SuppressWarnings("unchecked")
        TypedQuery<Cart> q = (TypedQuery<Cart>) mock(TypedQuery.class);

        when(em.createNamedQuery("Cart.TROVA_COSTUMER", Cart.class)).thenReturn(q);
        when(q.setParameter("userId", userId)).thenReturn(q);
        when(q.getSingleResult()).thenThrow(new NoResultException());

        Cart result = service.findCartByCostumer(userId);

        assertNotNull(result);
        verify(em, times(1)).persist(any(Cart.class));
    }

    @Test
    void removeProductFromCart_seCartNull_nonFaMerge() {
        System.out.println("[UNIT] removeProductFromCart: cart NULL -> nessuna operazione");

        int cartId = 1;
        int productId = 99;

        when(em.find(Cart.class, cartId)).thenReturn(null);

        service.removeProductFromCart(cartId, productId);

        verify(em, never()).merge(any());
    }

    @Test
    void removeProductFromCart_rimuoveSoloProdottoCorretto_e_faMerge() {
        System.out.println("[UNIT] removeProductFromCart: rimozione item corretto + merge");

        int cartId = 1;
        int productIdDaRimuovere = 10;

        Cart cart = mock(Cart.class);

        // lista reale per testare removeIf
        List<ItemCart> items = new ArrayList<>();

        ItemCart itemDaRimuovere = mock(ItemCart.class);
        Prodotto prodDaRimuovere = mock(Prodotto.class);
        when(prodDaRimuovere.getId()).thenReturn(productIdDaRimuovere);
        when(itemDaRimuovere.getProdotto()).thenReturn(prodDaRimuovere);

        ItemCart itemDaTenere = mock(ItemCart.class);
        Prodotto prodDaTenere = mock(Prodotto.class);
        when(prodDaTenere.getId()).thenReturn(999);
        when(itemDaTenere.getProdotto()).thenReturn(prodDaTenere);

        items.add(itemDaRimuovere);
        items.add(itemDaTenere);

        when(em.find(Cart.class, cartId)).thenReturn(cart);
        when(cart.getItems()).thenReturn(items);

        service.removeProductFromCart(cartId, productIdDaRimuovere);

        assertEquals(1, items.size());
        assertEquals(999, items.get(0).getProdotto().getId());

        verify(em, times(1)).merge(cart);
    }
}