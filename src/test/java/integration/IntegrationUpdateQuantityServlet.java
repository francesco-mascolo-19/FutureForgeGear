package integration;

import control.UpdateQuantityServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import remoteInterfaces.CartServiceRemote;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationUpdateQuantityServlet {

    private static class TestableUpdateQuantityServlet extends UpdateQuantityServlet {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableUpdateQuantityServlet servlet;

    private CartServiceRemote cartService;

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
        servlet = new TestableUpdateQuantityServlet();

        cartService = mock(CartServiceRemote.class);
        inject(servlet, "cartService", cartService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void doPost_cartPresente_aggiornaQuantita_aggiornaSessione_eRedirect() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("3");

        Cart cart = mock(Cart.class);
        when(session.getAttribute("cart")).thenReturn(cart);

        when(cart.calculateTotal()).thenReturn(123.45);

        servlet.doPostPublic(request, response);

        verify(cart).updateProductQuantity(10, 3);
        verify(session).setAttribute("cartTotal", 123.45);
        verify(session).setAttribute("cart", cart);

        verify(response).sendRedirect("cart.jsp");

        verifyNoInteractions(cartService);
    }

    @Test
    void doPost_cartNull_nonAggiornaSessione_maRedirect() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(request.getParameter("quantity")).thenReturn("3");

        when(session.getAttribute("cart")).thenReturn(null);

        servlet.doPostPublic(request, response);

        verify(session, never()).setAttribute(eq("cartTotal"), any());
        verify(session, never()).setAttribute(eq("cart"), any());

        verify(response).sendRedirect("cart.jsp");
        verifyNoInteractions(cartService);
    }

    @Test
    void doPost_parametriNonNumerici_lanciaNumberFormatException() {
        when(request.getParameter("productId")).thenReturn("abc");
        when(request.getParameter("quantity")).thenReturn("3");

        assertThrows(NumberFormatException.class, () -> servlet.doPostPublic(request, response));
    }
}