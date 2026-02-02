package unit;

import control.cambiaStatoOrdine;
import enumerativeTypes.Ruolo;
import enumerativeTypes.Stato;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.Ordine;
import model.UserManagement.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import remoteInterfaces.OrderServiceRemote;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitCambiaStatoOrdineServlet {

    // Sottoclasse per esporre doPost (protected)
    private static class TestableCambiaStatoOrdineServlet extends cambiaStatoOrdine {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableCambiaStatoOrdineServlet servlet;

    // mock EJB
    private OrderServiceRemote orderService;

    // mock web
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    // output json catturato
    private StringWriter responseBuffer;

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
    void setUp() throws Exception {
        servlet = new TestableCambiaStatoOrdineServlet();

        orderService = mock(OrderServiceRemote.class);
        inject(servlet, "orderService", orderService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        // cattura response.getWriter().write(...)
        responseBuffer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBuffer));

        // sessione esiste
        when(request.getSession(false)).thenReturn(session);

        // utente loggato con ruolo corretto (default)
        Utente utente = mock(Utente.class);
        when(utente.getRuolo()).thenReturn(Ruolo.GESTOREORDINI);
        when(session.getAttribute("loggedUser")).thenReturn(utente);

        // parametri base (default)
        when(request.getParameter("idOrdine")).thenReturn("1");
    }

    private void prepareOrderWithCurrentState(Stato currentState) {
        Ordine ordine = mock(Ordine.class);
        when(ordine.getStato()).thenReturn(currentState);
        when(orderService.findOrderById(1)).thenReturn(ordine);
    }


    /** Testa TR valida: preparation -> delivery deve andare in success e chiamare updateOrder. */
    @Test
    void transizione_preparation_to_delivery_valid() throws Exception {
        prepareOrderWithCurrentState(Stato.PREPARATION);
        when(request.getParameter("nuovoStato")).thenReturn(Stato.DELIVERY.name());

        servlet.doPostPublic(request, response);

        assertTrue(responseBuffer.toString().contains("\"status\":\"success\""));
        verify(orderService, times(1)).updateOrder(any(Ordine.class));

        System.out.println("OK -> TR valid: PREPARATION -> DELIVERY (success + updateOrder)");
    }

    /** Testa TR valida: delivery -> consegnato deve andare in success e chiamare updateOrder. */
    @Test
    void transizione_delivery_to_consegnato_valid() throws Exception {
        prepareOrderWithCurrentState(Stato.DELIVERY);
        when(request.getParameter("nuovoStato")).thenReturn(Stato.CONSEGNATO.name());

        servlet.doPostPublic(request, response);

        assertTrue(responseBuffer.toString().contains("\"status\":\"success\""));
        verify(orderService, times(1)).updateOrder(any(Ordine.class));

        System.out.println("OK -> TR valid: DELIVERY -> CONSEGNATO (success + updateOrder)");
    }

    // TRANSIZIONI NON CONSENTITE

    /** Testa TR error: preparation -> consegnato NON consentita (nel codice è false). */
    @Test
    void transizione_preparation_to_consegnato_error() throws Exception {
        prepareOrderWithCurrentState(Stato.PREPARATION);
        when(request.getParameter("nuovoStato")).thenReturn(Stato.CONSEGNATO.name());

        servlet.doPostPublic(request, response);

        assertTrue(responseBuffer.toString().contains("Transizione di stato non consentita"));
        verify(orderService, never()).updateOrder(any());

        System.out.println("OK -> TR error: PREPARATION -> CONSEGNATO (bloccata)");
    }

    /** Testa TR error: delivery -> preparation NON consentita (esplicitamente bloccata). */
    @Test
    void transizione_delivery_to_preparation_error() throws Exception {
        prepareOrderWithCurrentState(Stato.DELIVERY);
        when(request.getParameter("nuovoStato")).thenReturn(Stato.PREPARATION.name());

        servlet.doPostPublic(request, response);

        assertTrue(responseBuffer.toString().contains("Transizione di stato non consentita"));
        verify(orderService, never()).updateOrder(any());

        System.out.println("OK -> TR error: DELIVERY -> PREPARATION (bloccata)");
    }

    /** Testa TR error: consegnato -> preparation NON consentita (consegnato non torna indietro). */
    @Test
    void transizione_consegnato_to_preparation_error() throws Exception {
        prepareOrderWithCurrentState(Stato.CONSEGNATO);
        when(request.getParameter("nuovoStato")).thenReturn(Stato.PREPARATION.name());

        servlet.doPostPublic(request, response);

        assertTrue(responseBuffer.toString().contains("Transizione di stato non consentita"));
        verify(orderService, never()).updateOrder(any());

        System.out.println("OK -> TR error: CONSEGNATO -> PREPARATION (bloccata)");
    }

    /** Testa TR error: consegnato -> delivery NON consentita (consegnato non torna indietro). */
    @Test
    void transizione_consegnato_to_delivery_error() throws Exception {
        prepareOrderWithCurrentState(Stato.CONSEGNATO);
        when(request.getParameter("nuovoStato")).thenReturn(Stato.DELIVERY.name());

        servlet.doPostPublic(request, response);

        assertTrue(responseBuffer.toString().contains("Transizione di stato non consentita"));
        verify(orderService, never()).updateOrder(any());

        System.out.println("OK -> TR error: CONSEGNATO -> DELIVERY (bloccata)");
    }


    /** Testa TR error: stesso stato (preparation -> preparation) non consentito. */
    @Test
    void transizione_preparation_to_preparation_error_stessoStato() throws Exception {
        prepareOrderWithCurrentState(Stato.PREPARATION);
        when(request.getParameter("nuovoStato")).thenReturn(Stato.PREPARATION.name());

        servlet.doPostPublic(request, response);

        assertTrue(responseBuffer.toString().contains("Transizione di stato non consentita"));
        verify(orderService, never()).updateOrder(any());

        System.out.println("OK -> TR error: PREPARATION -> PREPARATION (nessun cambio vietato)");
    }
}
