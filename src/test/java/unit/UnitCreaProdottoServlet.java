package unit;

import control.CreaProdottoServlet;
import enumerativeTypes.Categoria;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import remoteInterfaces.CatalogoRemote;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitCreaProdottoServlet {

    // Sottoclasse per esporre doPost (protected)
    private static class TestableCreaProdottoServlet extends CreaProdottoServlet {
        public void doPostPublic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            super.doPost(req, resp);
        }
    }

    private TestableCreaProdottoServlet servlet;

    // mock EJB
    private CatalogoRemote catalogo;

    // mock web
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Part filePart;

    // per catturare response.getWriter().write(...)
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
        servlet = new TestableCreaProdottoServlet();

        catalogo = mock(CatalogoRemote.class);
        inject(servlet, "catalogo", catalogo);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filePart = mock(Part.class);

        when(request.getPart("immagine")).thenReturn(filePart);

        // writer finto per catturare le write JSON
        responseBuffer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBuffer));

        // evita NPE quando chiama filePart.getInputStream()
        when(filePart.getInputStream()).thenReturn(InputStream.nullInputStream());
    }


    /** N valido: nome non nullo -> deve chiamare catalogo.addProduct e fare redirect. */
    @Test
    void doPost_nomeValido_valid() throws Exception {
        when(request.getParameter("nome")).thenReturn("ProdottoTest");
        when(request.getParameter("descrizione")).thenReturn("desc");
        when(request.getParameter("categoria")).thenReturn(Categoria.values()[0].name());
        when(request.getParameter("prezzo")).thenReturn("1.00"); // valido per il tuo range

        servlet.doPostPublic(request, response);

        verify(catalogo, times(1)).addProduct(any());
        verify(response, times(1)).sendRedirect("Profile.jsp");

        System.out.println("OK -> N valido: addProduct chiamato e redirect Profile.jsp");
    }

    /** N nullo: nome null -> deve uscire subito, non chiamare addProduct, non redirect. */
    @Test
    void doPost_nomeNullo_error() throws Exception {
        when(request.getParameter("nome")).thenReturn(null);
        when(request.getParameter("descrizione")).thenReturn("desc");
        when(request.getParameter("categoria")).thenReturn(Categoria.values()[0].name());
        when(request.getParameter("prezzo")).thenReturn("1.00");

        servlet.doPostPublic(request, response);

        verify(catalogo, never()).addProduct(any());
        verify(response, never()).sendRedirect(anyString());

        // ha scritto un JSON di errore (nel tuo codice è incompleto, ma almeno scrive qualcosa)
        assertTrue(responseBuffer.toString().contains("\"status\":\"Errore\""));

        System.out.println("OK -> N nullo: non addProduct, non redirect, risposta errore scritta");
    }


    /** PR valid: prezzo > 0 (e compatibile con validazione 0.99..999.99) -> ok. */
    @Test
    void doPost_prezzoMaggioreDiZero_valid() throws Exception {
        when(request.getParameter("nome")).thenReturn("ProdottoTest");
        when(request.getParameter("descrizione")).thenReturn("desc");
        when(request.getParameter("categoria")).thenReturn(Categoria.values()[0].name());
        when(request.getParameter("prezzo")).thenReturn("10.50");

        servlet.doPostPublic(request, response);

        verify(catalogo, times(1)).addProduct(any());
        verify(response, times(1)).sendRedirect("Profile.jsp");

        System.out.println("OK -> PR > 0: addProduct chiamato e redirect Profile.jsp");
    }

    /** PR error: prezzo <= 0 -> deve uscire subito, non chiamare addProduct, non redirect. */
    @Test
    void doPost_prezzoMinoreUgualeZero_error() throws Exception {
        when(request.getParameter("nome")).thenReturn("ProdottoTest");
        when(request.getParameter("descrizione")).thenReturn("desc");
        when(request.getParameter("categoria")).thenReturn(Categoria.values()[0].name());
        when(request.getParameter("prezzo")).thenReturn("0");

        servlet.doPostPublic(request, response);

        verify(catalogo, never()).addProduct(any());
        verify(response, never()).sendRedirect(anyString());
        assertTrue(responseBuffer.toString().contains("\"status\":\"Errore\""));

        System.out.println("OK -> PR <= 0: non addProduct, non redirect, risposta errore scritta");
    }
}
