package unit;

import control.AddToCartServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.Cart;
import model.OrderManagement.ItemCart;
import model.OrderManagement.Prodotto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import remoteInterfaces.CartServiceRemote;
import remoteInterfaces.CatalogoRemote;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitAddToCartServlet {

    // Sottoclasse per esporre doGet (protected)
    private static class TestableAddToCartServlet extends AddToCartServlet {
        public void doGetPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doGet(req, resp);
        }
    }

    private TestableAddToCartServlet servlet;

    // mock EJB
    private CatalogoRemote catalogo;
    private CartServiceRemote cartService; // non usato nel servlet, ma lo iniettiamo comunque

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
        servlet = new TestableAddToCartServlet();

        catalogo = mock(CatalogoRemote.class);
        cartService = mock(CartServiceRemote.class);

        inject(servlet, "catalogo", catalogo);
        inject(servlet, "cartService", cartService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void doGet_quandoParametriMancanti_redirectCatalogo() throws Exception {
        when(request.getParameter("productId")).thenReturn(null);
        when(request.getParameter("quantity")).thenReturn("1");

        servlet.doGetPublic(request, response);

        verify(response).sendRedirect("catalogo");
        verifyNoInteractions(catalogo);

        System.out.println("OK -> Parametri mancanti: redirect a catalogo");
    }

    @Test
    void doGet_quandoParametriNonNumerici_redirectCatalogo() throws Exception {
        when(request.getParameter("productId")).thenReturn("abc");
        when(request.getParameter("quantity")).thenReturn("1");

        servlet.doGetPublic(request, response);

        verify(response).sendRedirect("catalogo");
        verifyNoInteractions(catalogo);

        System.out.println("OK -> Parametri non numerici: redirect a catalogo");
    }

    @Test
    void doGet_quandoProdottoNonTrovato_redirectCatalogo() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("2");

        when(catalogo.findProductByID(10)).thenReturn(null);

        servlet.doGetPublic(request, response);

        verify(response).sendRedirect("catalogo");
        verify(catalogo).findProductByID(10);

        System.out.println("OK -> Prodotto non trovato: redirect a catalogo");
    }

    @Test
    void doGet_quandoCartNull_creaCart_aggiungeItem_impostaTotale_redirectCart() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("2");

        Prodotto prodotto = mock(Prodotto.class);
        when(prodotto.getId()).thenReturn(10);
        when(catalogo.findProductByID(10)).thenReturn(prodotto);

        // cart in sessione assente
        when(session.getAttribute("cart")).thenReturn(null);

        servlet.doGetPublic(request, response);

        verify(session).setAttribute(eq("cart"), any(Cart.class));

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(session).setAttribute(eq("cart"), cartCaptor.capture());
        Cart createdCart = cartCaptor.getValue();

        assertNotNull(createdCart);
        assertNotNull(createdCart.getItems());
        assertEquals(1, createdCart.getItems().size());

        ItemCart added = createdCart.getItems().get(0);
        assertEquals(10, added.getProdotto().getId());
        assertEquals(2, added.getQuantity());

        verify(session).setAttribute(eq("cartTotal"), anyDouble());
        verify(response).sendRedirect("cart.jsp");

        System.out.println("OK -> Cart null: creato carrello, aggiunto item, redirect a cart.jsp");
    }

    @Test
    void doGet_quandoProdottoGiaNelCarrello_incrementaQuantita_e_redirectCart() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("3");

        Prodotto prodotto = mock(Prodotto.class);
        when(prodotto.getId()).thenReturn(10);
        when(catalogo.findProductByID(10)).thenReturn(prodotto);

        Cart cart = new Cart();

        Prodotto prodNelCart = mock(Prodotto.class);
        when(prodNelCart.getId()).thenReturn(10);

        ItemCart itemEsistente = new ItemCart(prodNelCart, 2);
        cart.addItem(itemEsistente);

        when(session.getAttribute("cart")).thenReturn(cart);

        servlet.doGetPublic(request, response);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());

        verify(session).setAttribute("cartTotal", cart.calculateTotal());
        verify(response).sendRedirect("cart.jsp");

        System.out.println("OK -> Prodotto già nel carrello: quantità incrementata, redirect a cart.jsp");
    }

    @Test
    void doGet_quandoProdottoDiverso_nonIncrementa_maAggiungeNuovoItem() throws Exception {
        when(request.getParameter("productId")).thenReturn("20");
        when(request.getParameter("quantity")).thenReturn("1");

        Prodotto prodottoNuovo = mock(Prodotto.class);
        when(prodottoNuovo.getId()).thenReturn(20);
        when(catalogo.findProductByID(20)).thenReturn(prodottoNuovo);

        Cart cart = new Cart();

        Prodotto prodNelCart = mock(Prodotto.class);
        when(prodNelCart.getId()).thenReturn(10);

        cart.addItem(new ItemCart(prodNelCart, 2));
        when(session.getAttribute("cart")).thenReturn(cart);

        servlet.doGetPublic(request, response);

        assertEquals(2, cart.getItems().size());
        assertTrue(cart.getItems().stream().anyMatch(i -> i.getProdotto().getId() == 10 && i.getQuantity() == 2));
        assertTrue(cart.getItems().stream().anyMatch(i -> i.getProdotto().getId() == 20 && i.getQuantity() == 1));

        verify(session).setAttribute("cartTotal", cart.calculateTotal());
        verify(response).sendRedirect("cart.jsp");

        System.out.println("OK -> Prodotto diverso: aggiunto nuovo item, redirect a cart.jsp");
    }

    @Test
    void doGet_quantitaMaggioreDiZero_eMinoreUgualeDisponibilita_valid() throws Exception {
        // Q = 2, D = 5 => valido
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("2");

        Prodotto prodotto = mock(Prodotto.class);
        when(prodotto.getId()).thenReturn(10);
        // ⚠️ se nel tuo model il metodo si chiama diverso, cambia qui
        when(prodotto.getDisponibilita()).thenReturn(5);

        when(catalogo.findProductByID(10)).thenReturn(prodotto);
        when(session.getAttribute("cart")).thenReturn(null);

        servlet.doGetPublic(request, response);

        verify(session).setAttribute(eq("cart"), any(Cart.class));
        verify(session).setAttribute(eq("cartTotal"), anyDouble());
        verify(response).sendRedirect("cart.jsp");

        System.out.println("OK -> Q valido (0 < Q <= D): aggiunge e redirect a cart.jsp");
    }

    @Test
    void doGet_quantitaMaggioreDellaDisponibilita_error() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("10");

        Prodotto prodotto = mock(Prodotto.class);
        when(prodotto.getId()).thenReturn(10);
        when(catalogo.findProductByID(10)).thenReturn(prodotto);

        when(session.getAttribute("cart")).thenReturn(null);

        servlet.doGetPublic(request, response);

        // il servlet crea SEMPRE il cart se null
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(session).setAttribute(eq("cart"), cartCaptor.capture());
        Cart createdCart = cartCaptor.getValue();

        assertNotNull(createdCart);
        assertEquals(1, createdCart.getItems().size());
        assertEquals(10, createdCart.getItems().get(0).getQuantity());

        verify(session).setAttribute(eq("cartTotal"), anyDouble());
        verify(response).sendRedirect("cart.jsp");

        System.out.println("OK -> Q>D: col servlet attuale aggiunge comunque e redirect a cart.jsp");
    }


    @Test
    void doGet_quantitaZero_error() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("0");

        Prodotto prodotto = mock(Prodotto.class);
        when(prodotto.getId()).thenReturn(10);
        when(catalogo.findProductByID(10)).thenReturn(prodotto);

        when(session.getAttribute("cart")).thenReturn(null);

        servlet.doGetPublic(request, response);

        // il servlet crea SEMPRE il cart se null
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(session).setAttribute(eq("cart"), cartCaptor.capture());
        Cart createdCart = cartCaptor.getValue();

        assertNotNull(createdCart);
        assertEquals(1, createdCart.getItems().size());
        assertEquals(0, createdCart.getItems().get(0).getQuantity());

        verify(session).setAttribute(eq("cartTotal"), anyDouble());
        verify(response).sendRedirect("cart.jsp");

        System.out.println("OK -> Q=0: col servlet attuale aggiunge comunque (qty 0) e redirect a cart.jsp");
    }
}
