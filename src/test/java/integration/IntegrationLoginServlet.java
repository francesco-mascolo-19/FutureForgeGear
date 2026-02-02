package integration;

import control.LoginServlet;
import enumerativeTypes.Ruolo;
import jakarta.persistence.NoResultException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.ItemCartDTO;
import model.OrderManagement.Ordine;
import model.UserManagement.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.OrderService;
import service.UserService;
import unit.jpa.JpaH2TestBase;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationLoginServlet extends JpaH2TestBase {

    private static class TestableLoginServlet extends LoginServlet {
        public void doGetPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doGet(req, resp);
        }
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableLoginServlet servlet;

    private UserService userService;
    private OrderService orderService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    private void inject(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile iniettare '" + fieldName + "'", e);
        }
    }

    private Utente newUtente(String nome, String cognome, String email, String password, Ruolo ruolo) {
        return new Utente(nome, cognome, email, password, ruolo);
    }

    private Ordine newOrdine(long userId, double totale) {
        return new Ordine(userId, totale, List.of(new ItemCartDTO(1, 2)));
    }

    @BeforeEach
    void setUp() {
        servlet = new TestableLoginServlet();

        userService = new UserService();
        orderService = new OrderService();
        injectEntityManager(userService, em);
        injectEntityManager(orderService, em);

        inject(servlet, "userService", userService);
        inject(servlet, "orderService", orderService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(dispatcher);
        when(request.getContextPath()).thenReturn("");

        em.createQuery("delete from Ordine").executeUpdate();
        em.createQuery("delete from Utente").executeUpdate();
        commitAndRestartTx();
    }

    // ------------------------
    // doGet() -> Integration
    // ------------------------

    @Test
    void doGet_caricaClienti_e_faForwardLoginJsp() throws Exception {
        em.persist(newUtente("Mario", "Rossi", "mario@x.it", "pwd", Ruolo.CLIENTE));
        em.persist(newUtente("Anna", "Bianchi", "anna@x.it", "pwd", Ruolo.CLIENTE));
        commitAndRestartTx();

        servlet.doGetPublic(request, response);

        verify(request).setAttribute(eq("clienti"), argThat(list -> ((List<?>) list).size() == 2));
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    // ------------------------
    // doPost() -> Integration
    // ------------------------

    @Test
    void doPost_utenteEsiste_passwordCorretta_ruoloCliente_redirectHome2_e_settaSessione() throws Exception {
        Utente u = newUtente("Luca", "Verdi", "luca@x.it", "pwd123", Ruolo.CLIENTE);
        em.persist(u);
        commitAndRestartTx();

        em.persist(newOrdine(u.getId(), 100.0));
        em.persist(newOrdine(u.getId(), 50.0));
        commitAndRestartTx();

        when(request.getParameter("email")).thenReturn("luca@x.it");
        when(request.getParameter("password")).thenReturn("pwd123");

        servlet.doPostPublic(request, response);

        verify(session).setAttribute(eq("loggedUser"), any(Utente.class));
        verify(session).setAttribute(eq("orders"), argThat(list -> ((List<?>) list).size() == 2));

        verify(response).sendRedirect("/home2.jsp");
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    void doPost_utenteEsiste_passwordCorretta_ruoloNonCliente_redirectProfile() throws Exception {
        Utente u = newUtente("Admin", "Root", "admin@x.it", "pwd", Ruolo.ADMIN);
        em.persist(u);
        commitAndRestartTx();

        when(request.getParameter("email")).thenReturn("admin@x.it");
        when(request.getParameter("password")).thenReturn("pwd");

        servlet.doPostPublic(request, response);

        verify(session).setAttribute(eq("loggedUser"), any(Utente.class));
        verify(session).setAttribute(eq("orders"), any());
        verify(response).sendRedirect("/Profile.jsp");
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    void doPost_passwordErrata_forwardLoginConErrore() throws Exception {
        Utente u = newUtente("Giulia", "Neri", "giulia@x.it", "pwdGIUSTA", Ruolo.CLIENTE);
        em.persist(u);
        commitAndRestartTx();

        when(request.getParameter("email")).thenReturn("giulia@x.it");
        when(request.getParameter("password")).thenReturn("pwdSBAGLIATA");

        servlet.doPostPublic(request, response);

        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void doPost_utenteNonEsiste_forwardLoginConErrore() throws Exception {
        when(request.getParameter("email")).thenReturn("inesistente@x.it");
        when(request.getParameter("password")).thenReturn("pwd");

        servlet.doPostPublic(request, response);

        verify(request).setAttribute(eq("loginError"), anyString());
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }
}
