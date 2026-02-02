package integration;

import control.RegisterServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.UserManagement.Cliente;
import model.UserManagement.Indirizzo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import remoteInterfaces.UserServiceRemote;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationRegisterServlet {

    private static class TestableRegisterServlet extends RegisterServlet {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableRegisterServlet servlet;

    private UserServiceRemote userService;

    private HttpServletRequest request;
    private HttpServletResponse response;

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
        servlet = new TestableRegisterServlet();

        userService = mock(UserServiceRemote.class);

        inject(servlet, "userService", userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void doPost_casoOk_chiamaAddUser_e_redirectHome() throws Exception {
        when(request.getParameter("name")).thenReturn("Mario");
        when(request.getParameter("surname")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@example.com");
        when(request.getParameter("password")).thenReturn("Password1");

        when(request.getParameter("stato")).thenReturn("Italia");
        when(request.getParameter("provincia")).thenReturn("NA");
        when(request.getParameter("citta")).thenReturn("Napoli");
        when(request.getParameter("via")).thenReturn("Via Roma");
        when(request.getParameter("numCivico")).thenReturn("10");
        when(request.getParameter("cap")).thenReturn("80100");

        servlet.doPostPublic(request, response);

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(userService, times(1)).addUser(captor.capture());

        Cliente saved = captor.getValue();
        assertNotNull(saved);

        assertEquals("Mario", saved.getNome());
        assertEquals("Rossi", saved.getCognome());
        assertEquals("mario.rossi@example.com", saved.getEmail());
        assertEquals("Password1", saved.getPassword());

        Indirizzo ind = saved.getIndirizzo();
        assertNotNull(ind);
        assertEquals("Italia", ind.getStato());
        assertEquals("NA", ind.getProvincia());
        assertEquals("Napoli", ind.getCitta());
        assertEquals("Via Roma", ind.getVia());
        assertEquals(10, ind.getNumCivico());
        assertEquals(80100, ind.getCAP());

        verify(response).sendRedirect("home.jsp");
    }

    @Test
    void doPost_numCivicoNonNumerico_lanciaNumberFormatException() throws Exception {
        when(request.getParameter("name")).thenReturn("Mario");
        when(request.getParameter("surname")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@example.com");
        when(request.getParameter("password")).thenReturn("Password1");

        when(request.getParameter("stato")).thenReturn("Italia");
        when(request.getParameter("provincia")).thenReturn("NA");
        when(request.getParameter("citta")).thenReturn("Napoli");
        when(request.getParameter("via")).thenReturn("Via Roma");
        when(request.getParameter("numCivico")).thenReturn("X");
        when(request.getParameter("cap")).thenReturn("80100");

        assertThrows(NumberFormatException.class, () -> servlet.doPostPublic(request, response));

        verify(userService, never()).addUser(any());
        verify(response, never()).sendRedirect("home.jsp");
    }

    @Test
    void doPost_capNonNumerico_lanciaNumberFormatException() throws Exception {
        when(request.getParameter("name")).thenReturn("Mario");
        when(request.getParameter("surname")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@example.com");
        when(request.getParameter("password")).thenReturn("Password1");

        when(request.getParameter("stato")).thenReturn("Italia");
        when(request.getParameter("provincia")).thenReturn("NA");
        when(request.getParameter("citta")).thenReturn("Napoli");
        when(request.getParameter("via")).thenReturn("Via Roma");
        when(request.getParameter("numCivico")).thenReturn("10");
        when(request.getParameter("cap")).thenReturn("ABC");

        assertThrows(NumberFormatException.class, () -> servlet.doPostPublic(request, response));

        verify(userService, never()).addUser(any());
        verify(response, never()).sendRedirect("home.jsp");
    }
}
