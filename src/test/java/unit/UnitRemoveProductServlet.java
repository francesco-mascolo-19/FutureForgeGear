package unit;

import control.RemoveProductServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/*
  NOTE:
  doPost è protected, quindi per testarlo da package "unit"
  usiamo una sottoclasse che espone un metodo public.
*/
class UnitRemoveProductServlet {

    private static class TestableRemoveProductServlet extends RemoveProductServlet {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableRemoveProductServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        servlet = new TestableRemoveProductServlet();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void doPost_cartPresente_rimuoveProdotto_aggiornaSessione_eRedirect() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");

        Cart cart = mock(Cart.class);
        when(session.getAttribute("cart")).thenReturn(cart);

        when(cart.calculateTotal()).thenReturn(99.99);

        servlet.doPostPublic(request, response);

        verify(cart).removeItem(10);
        verify(session).setAttribute("cart", cart);
        verify(session).setAttribute("cartTotal", 99.99);
        verify(response).sendRedirect("cart.jsp");
    }

    @Test
    void doPost_cartNull_nonChiamaRemoveItem_maFaRedirect() throws Exception {
        when(request.getParameter("productId")).thenReturn("10");
        when(session.getAttribute("cart")).thenReturn(null);
        assertThrows(NullPointerException.class, () -> servlet.doPostPublic(request, response));
    }

    @Test
    void doPost_productIdNonNumerico_lanciaNumberFormatException() {
        when(request.getParameter("productId")).thenReturn("abc");

        assertThrows(NumberFormatException.class, () -> servlet.doPostPublic(request, response));
    }
}