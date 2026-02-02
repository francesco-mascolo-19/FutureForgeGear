package integration;

import control.CheckoutServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.Cart;
import model.OrderManagement.ItemCart;
import model.OrderManagement.Ordine;
import model.OrderManagement.Prodotto;
import model.UserManagement.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import remoteInterfaces.CatalogoRemote;
import service.OrderService;
import unit.jpa.JpaH2TestBase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IntegrationCheckoutServlet extends JpaH2TestBase {

    private static class TestableCheckoutServlet extends CheckoutServlet {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableCheckoutServlet servlet;

    private OrderService orderService;

    private CatalogoRemote catalogoRemote;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private void inject(Object target, String fieldName, Object value) {
        try {
            Class<?> c = target.getClass();
            Field f = null;

            while (c != null) {
                try {
                    f = c.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException ignored) {
                    c = c.getSuperclass();
                }
            }

            if (f == null) {
                throw new NoSuchFieldException(fieldName);
            }

            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile iniettare '" + fieldName + "'", e);
        }
    }

    @BeforeEach
    void setUp() {
        servlet = new TestableCheckoutServlet();

        orderService = new OrderService();
        injectEntityManager(orderService, em);

        catalogoRemote = mock(CatalogoRemote.class);

        inject(servlet, "orderService", orderService);
        inject(servlet, "catalogoRemote", catalogoRemote);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        em.createQuery("delete from Ordine").executeUpdate();
        commitAndRestartTx();
    }

    @Test
    void doPost_casoOk_persisteOrdineInH2_e_redirectSpedizione() throws Exception {
        Utente utente = mock(Utente.class);
        when(utente.getId()).thenReturn(500L);

        Cart cart = mock(Cart.class);

        ItemCart item1 = mock(ItemCart.class);
        Prodotto prod1 = mock(Prodotto.class);
        when(prod1.getId()).thenReturn(10);
        when(item1.getProdotto()).thenReturn(prod1);
        when(item1.getQuantity()).thenReturn(1);

        ItemCart item2 = mock(ItemCart.class);
        Prodotto prod2 = mock(Prodotto.class);
        when(prod2.getId()).thenReturn(20);
        when(item2.getProdotto()).thenReturn(prod2);
        when(item2.getQuantity()).thenReturn(3);

        when(cart.getItems()).thenReturn(List.of(item1, item2));
        when(cart.calculateTotal()).thenReturn(99.99);

        List<Ordine> ordersInSession = new ArrayList<>();
        when(session.getAttribute("orders")).thenReturn(ordersInSession);

        when(session.getAttribute("loggedUser")).thenReturn(utente);
        when(session.getAttribute("cart")).thenReturn(cart);

        servlet.doPostPublic(request, response);

        commitAndRestartTx();

        List<Ordine> all = em.createNamedQuery("Ordine.TROVA_TUTTI", Ordine.class)
                .getResultList();

        assertEquals(1, all.size());

        Ordine saved = all.get(0);
        assertNotNull(saved.getId());
        assertEquals(500L, saved.getUserId());
        assertEquals(99.99, saved.getTotale(), 0.0001);

        assertNotNull(saved.getItems());
        assertEquals(2, saved.getItems().size());

        verify(session).setAttribute(eq("order"), any(Ordine.class));
        verify(session).setAttribute(eq("orders"), same(ordersInSession));
        verify(session).setAttribute(eq("items"), anyList());
        verify(session).removeAttribute("cart");

        verify(response).sendRedirect("Spedizione.jsp");
    }
}
