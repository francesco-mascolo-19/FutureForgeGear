package control;

import enumerativeTypes.Ruolo;
import enumerativeTypes.Stato;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.Ordine;
import model.UserManagement.Utente;
import remoteInterfaces.OrderServiceRemote;

import java.io.IOException;

@WebServlet("/cambiaStatoOrdine")
public class cambiaStatoOrdine extends HttpServlet {

    @EJB
    OrderServiceRemote orderService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Sessione non esistente.\"}");
            return;
        }

        Utente utente = (Utente) session.getAttribute("loggedUser");
        if (utente == null) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Utente non autenticato.\"}");
            return;
        }

        if (!utente.getRuolo().equals(Ruolo.GESTOREORDINI)) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Ruolo non corretto\"}");
            return;
        }

        String statoParam = request.getParameter("nuovoStato");
        String idOrdineParam = request.getParameter("idOrdine");

        if (statoParam == null || idOrdineParam == null) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Parametri mancanti\"}");
            return;
        }

        try {
            int idOrdine = Integer.parseInt(idOrdineParam);

            Ordine ordine = orderService.findOrderById(idOrdine);
            if (ordine == null) {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Ordine non trovato\"}");
                return;
            }

            Stato statoNuovo;
            try {
                statoNuovo = Stato.valueOf(statoParam);
            } catch (IllegalArgumentException ex) {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Stato non valido\"}");
                return;
            }

            Stato statoAttuale = ordine.getStato();

            // ✅ controllo transizione
            if (!isTransizioneConsentita(statoAttuale, statoNuovo)) {
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Transizione di stato non consentita\"}");
                return;
            }

            ordine.setStato(statoNuovo);
            orderService.updateOrder(ordine);

            response.getWriter().write("{\"status\":\"success\"}");

        } catch (NumberFormatException e) {
            response.getWriter().write("{\"status\":\"error\",\"message\":\"ID ordine non valido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Errore nell'elaborazione della richiesta\"}");
        }
    }

    private boolean isTransizioneConsentita(Stato attuale, Stato nuovo) {
        if (attuale == null || nuovo == null) return false;

        // Se vuoi vietare anche "nessun cambio"
        if (attuale == nuovo) return false;

        // Regole richieste:
        // - consegnato NON può tornare indietro
        if (attuale == Stato.CONSEGNATO) {
            return false;
        }

        // - delivery NON può tornare a preparation
        if (attuale == Stato.DELIVERY && nuovo == Stato.PREPARATION) {
            return false;
        }

        // Regole "naturali" forward:
        if (attuale == Stato.PREPARATION && nuovo == Stato.DELIVERY) return true;
        if (attuale == Stato.DELIVERY && nuovo == Stato.CONSEGNATO) return true;

        // (Opzionale) se vuoi consentire preparation -> consegnato, scommenta:
        // if (attuale == Stato.PREPARATION && nuovo == Stato.CONSEGNATO) return true;

        return false;
    }
}
