package control;

import enumerativeTypes.Ruolo;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.RequestManagement.ProductRequest;
import model.UserManagement.Utente;
import remoteInterfaces.CatalogoRemote;
import remoteInterfaces.RequestServiceRemote;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/RichiestaProdottoServlet")
public class RichiestaProdottoServlet extends HttpServlet {

    @EJB
    RequestServiceRemote requestService;

    @EJB
    CatalogoRemote catalogo;

    private boolean isNullish(String s) {
        return s == null || s.isBlank() || "null".equalsIgnoreCase(s) || "undefined".equalsIgnoreCase(s);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sessione non esistente.");
            return;
        }

        Utente utente = (Utente) session.getAttribute("loggedUser");
        if (utente == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autenticato.");
            return;
        }

        if (!utente.getRuolo().equals(Ruolo.MAGAZZINIERE)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ruolo non autorizzato.");
            return;
        }

        Long userID = utente.getId();

        String idParam = request.getParameter("idProd");              // (puoi anche eliminarlo, vedi sotto)
        String idParamFornitore = request.getParameter("idFornitore");
        String idParamProd = request.getParameter("idProd");      // ✅ CORRETTO
        String quantityParam = request.getParameter("quantity");
        String note = request.getParameter("note");               // opzionale

        if (idParamFornitore == null || idParamProd == null || quantityParam == null ||
                idParamFornitore.isBlank() || idParamProd.isBlank() || quantityParam.isBlank() ||
                "null".equalsIgnoreCase(idParamFornitore) || "null".equalsIgnoreCase(idParamProd)) {
            response.sendError(400, "Errore: Dati mancanti o non validi.");
            return;
        }

        try {
            long idFornitore = Long.parseLong(idParamFornitore);
            int idProd = Integer.parseInt(idParamProd);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                response.sendError(400, "Errore: Dati mancanti o non validi.");
                return;
            }

            ProductRequest productRequest = new ProductRequest(userID, idFornitore, LocalDateTime.now(), idProd, quantity, note);
            requestService.addRequest(productRequest);

            response.getWriter().write("OK");
        } catch (NumberFormatException e) {
            response.sendError(400, "Errore: Dati mancanti o non validi.");
        }
    }
}
