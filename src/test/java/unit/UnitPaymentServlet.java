package unit;

import control.PaymentServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.ItemCartDTO;
import model.OrderManagement.Ordine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CreditCardService;
import service.PayPalService;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.*;

class UnitPaymentServlet {

    //in java doPost é protected quindi di conseguenza non sará testabile essendo in un package diverso quindi nel test chiami il metodo tramite una sottoclasse
    private static class TestablePaymentServlet extends PaymentServlet {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestablePaymentServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private PayPalService payPalService;
    private CreditCardService creditCardService;

    @BeforeEach
    void setUp() {
        servlet = new TestablePaymentServlet();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        payPalService = mock(PayPalService.class);
        creditCardService = mock(CreditCardService.class);

        when(request.getSession()).thenReturn(session);

        inject(servlet, "PayPalservice", payPalService);
        inject(servlet, "cardService", creditCardService);
    }

    private void inject(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile iniettare '" + fieldName + "' nel test", e);
        }
    }

    private Ordine anyOrder() {
        return new Ordine(1L, 10.0, List.of(new ItemCartDTO(1, 1)));
    }

    @Test
    void doPost_quandoOrderNull_redirectCartErrorNoOrder() throws Exception {
        when(session.getAttribute("order")).thenReturn(null);

        servlet.doPostPublic(request, response);

        verify(response).sendRedirect("cart.jsp?error=noorder");
        verify(response, never()).sendRedirect("ConfermaAcquisto.jsp");
        verify(response, never()).sendRedirect("ErrorePagamento.jsp");
    }

    @Test
    void doPost_paypal_conCredenzialiVuote_redirectErrore_e_nonChiamaServizi() throws Exception {
        when(session.getAttribute("order")).thenReturn(anyOrder());
        when(session.getAttribute("cartTotal")).thenReturn(20.0);

        when(request.getParameter("paymentMethod")).thenReturn("paypal");
        when(request.getParameter("paypalEmail")).thenReturn("");
        when(request.getParameter("paypalPassword")).thenReturn("");

        servlet.doPostPublic(request, response);

        verify(session).setAttribute(eq("message"), anyString());
        verify(payPalService, never()).init(anyString(), anyString());
        verify(payPalService, never()).effettuaPagamento(anyDouble());

        verify(response).sendRedirect("ErrorePagamento.jsp");
        verify(session, never()).removeAttribute("order");
        verify(session, never()).removeAttribute("cartTotal");
    }

    @Test
    void doPost_paypal_pagamentoOk_redirectConferma_e_pulisceSessione() throws Exception {
        when(session.getAttribute("order")).thenReturn(anyOrder());
        when(session.getAttribute("cartTotal")).thenReturn(20.0);

        when(request.getParameter("paymentMethod")).thenReturn("paypal");
        when(request.getParameter("paypalEmail")).thenReturn("a@b.com");
        when(request.getParameter("paypalPassword")).thenReturn("pwd");

        when(payPalService.effettuaPagamento(20.0)).thenReturn(1);

        servlet.doPostPublic(request, response);

        verify(payPalService).init("a@b.com", "pwd");
        verify(payPalService).effettuaPagamento(20.0);

        verify(session).setAttribute("message", "Il pagamento con PayPal è avvenuto con successo");
        verify(session).removeAttribute("order");
        verify(session).removeAttribute("cartTotal");
        verify(response).sendRedirect("ConfermaAcquisto.jsp");
    }

    @Test
    void doPost_paypal_pagamentoKo_redirectErrore_e_nonPulisceSessione() throws Exception {
        when(session.getAttribute("order")).thenReturn(anyOrder());
        when(session.getAttribute("cartTotal")).thenReturn(20.0);

        when(request.getParameter("paymentMethod")).thenReturn("paypal");
        when(request.getParameter("paypalEmail")).thenReturn("a@b.com");
        when(request.getParameter("paypalPassword")).thenReturn("pwd");

        when(payPalService.effettuaPagamento(20.0)).thenReturn(0);

        servlet.doPostPublic(request, response);

        verify(session).setAttribute("message", "Il pagamento non è andato a buon fine");
        verify(response).sendRedirect("ErrorePagamento.jsp");
        verify(session, never()).removeAttribute("order");
        verify(session, never()).removeAttribute("cartTotal");
    }

    @Test
    void doPost_creditcard_conParametriNull_redirectErrore_e_nonChiamaServizi() throws Exception {
        when(session.getAttribute("order")).thenReturn(anyOrder());
        when(session.getAttribute("cartTotal")).thenReturn(30.0);

        when(request.getParameter("paymentMethod")).thenReturn("creditcard");
        when(request.getParameter("titolare")).thenReturn("Mario Rossi");
        when(request.getParameter("numeroCarta")).thenReturn(null);
        when(request.getParameter("dataScadenza")).thenReturn("12/30");
        when(request.getParameter("CVVCarta")).thenReturn("123");

        servlet.doPostPublic(request, response);

        verify(creditCardService, never()).init(anyString(), anyString(), anyString(), anyString());
        verify(creditCardService, never()).effettuaPagamento(anyDouble());

        verify(session).setAttribute(eq("message"), anyString());
        verify(response).sendRedirect("ErrorePagamento.jsp");
    }

    @Test
    void doPost_creditcard_pagamentoOk_redirectConferma_e_pulisceSessione() throws Exception {
        when(session.getAttribute("order")).thenReturn(anyOrder());
        when(session.getAttribute("cartTotal")).thenReturn(30.0);

        when(request.getParameter("paymentMethod")).thenReturn("creditcard");
        when(request.getParameter("titolare")).thenReturn("Mario Rossi");
        when(request.getParameter("numeroCarta")).thenReturn("1111222233334444");
        when(request.getParameter("dataScadenza")).thenReturn("12/30");
        when(request.getParameter("CVVCarta")).thenReturn("123");

        when(creditCardService.effettuaPagamento(30.0)).thenReturn(1);

        servlet.doPostPublic(request, response);

        verify(creditCardService).init("Mario Rossi", "1111222233334444", "12/30", "123");
        verify(creditCardService).effettuaPagamento(30.0);

        verify(session).setAttribute("message", "Il pagamento con la carta di credito è avvenuto con successo");
        verify(session).removeAttribute("order");
        verify(session).removeAttribute("cartTotal");
        verify(response).sendRedirect("ConfermaAcquisto.jsp");
    }

    @Test
    void doPost_paymentMethodSconosciuto_redirectErrore() throws Exception {
        when(session.getAttribute("order")).thenReturn(anyOrder());
        when(session.getAttribute("cartTotal")).thenReturn(10.0);

        when(request.getParameter("paymentMethod")).thenReturn("bonifico");

        servlet.doPostPublic(request, response);

        verify(response).sendRedirect("ErrorePagamento.jsp");
    }
}
