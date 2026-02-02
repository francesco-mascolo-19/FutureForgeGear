package unit;

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
import remoteInterfaces.OrderServiceRemote;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UnitCheckoutServlet {

    // Sottoclasse per esporre doPost (protected)
    private static class TestableCheckoutServlet extends CheckoutServlet {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableCheckoutServlet servlet;

    // mock delle dipendenze EJB
    private OrderServiceRemote orderService;
    private CatalogoRemote catalogoRemote;

    // mock web
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private void inject(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile iniettare '" + fieldName + "'", e);
        }
    }

    @BeforeEach
    void setUp() {
        servlet = new TestableCheckoutServlet();

        orderService = mock(OrderServiceRemote.class);
        catalogoRemote = mock(CatalogoRemote.class);

        inject(servlet, "orderService", orderService);
        inject(servlet, "catalogoRemote", catalogoRemote);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void doPost_quandoUtenteNull_redirectLogin() throws Exception {
        when(session.getAttribute("loggedUser")).thenReturn(null);

        servlet.doPostPublic(request, response);

        verify(response).sendRedirect("login.jsp");
        verify(orderService, never()).addOrder(any());
        verify(session, never()).setAttribute(eq("order"), any());
    }

    @Test
    void doPost_quandoCartNull_redirectCartEmpty() throws Exception {
        Utente utente = mock(Utente.class);
        when(session.getAttribute("loggedUser")).thenReturn(utente);
        when(session.getAttribute("cart")).thenReturn(null);

        servlet.doPostPublic(request, response);

        verify(response).sendRedirect("cart.jsp?error=empty");
        verify(orderService, never()).addOrder(any());
    }

    @Test
    void doPost_quandoCartVuoto_redirectCartEmpty() throws Exception {
        Utente utente = mock(Utente.class);
        Cart cart = mock(Cart.class);

        when(session.getAttribute("loggedUser")).thenReturn(utente);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(cart.getItems()).thenReturn(List.of()); // vuoto

        servlet.doPostPublic(request, response);

        verify(response).sendRedirect("cart.jsp?error=empty");
        verify(orderService, never()).addOrder(any());
    }

    @Test
    void doPost_casoOk_creaOrdine_settaSessione_pulisceCarrello_redirectSpedizione() throws Exception {
        // utente loggato
        Utente utente = mock(Utente.class);
        when(utente.getId()).thenReturn(10L);

        // carrello con 1 item
        Cart cart = mock(Cart.class);
        ItemCart itemCart = mock(ItemCart.class);
        Prodotto prodotto = mock(Prodotto.class);

        when(session.getAttribute("loggedUser")).thenReturn(utente);
        when(session.getAttribute("cart")).thenReturn(cart);

        when(cart.getItems()).thenReturn(List.of(itemCart));
        when(itemCart.getProdotto()).thenReturn(prodotto);
        when(prodotto.getId()).thenReturn(99); // nel tuo progetto Prodotto sembra avere id int
        when(itemCart.getQuantity()).thenReturn(2);

        when(cart.calculateTotal()).thenReturn(123.45);

        // lista ordini già in sessione (evita NPE)
        List<Ordine> existingOrders = new ArrayList<>();
        when(session.getAttribute("orders")).thenReturn(existingOrders);

        // mock ritorno addOrder
        Ordine returned = new Ordine();
        returned.setId(1L);
        when(orderService.addOrder(any(Ordine.class))).thenReturn(returned);

        servlet.doPostPublic(request, response);

        // verifica chiamata service
        verify(orderService).addOrder(any(Ordine.class));

        // session attributes principali
        verify(session).setAttribute("order", returned);
        verify(session).setAttribute(eq("orders"), same(existingOrders));
        verify(session).setAttribute(eq("items"), anyList());

        // carrello rimosso
        verify(session).removeAttribute("cart");

        // redirect finale
        verify(response).sendRedirect("Spedizione.jsp");
    }
}
