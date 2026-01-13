package control;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.Cart;
import model.OrderManagement.ItemCart;
import model.OrderManagement.Prodotto;
import model.UserManagement.Utente;
import remoteInterfaces.CartServiceRemote;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());

    @EJB
    private CartServiceRemote cartService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utente loggedUser = (Utente) session.getAttribute("loggedUser");

        // 1. Controllo autenticazione
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Verifica che cartService sia stato iniettato correttamente
        if (cartService == null) {
            LOGGER.log(Level.SEVERE, "CartService EJB not injected properly");
            request.setAttribute("errorMessage", "Errore nel sistema. Riprovare più tardi.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            // 3. Gestione del carrello
            Cart sessionCart = (Cart) session.getAttribute("cart");

            if (sessionCart == null || sessionCart.getId() == 0) {
                // Se non c'è carrello in sessione o è nuovo, cerca nel database
                Cart dbCart = cartService.findCartByCostumer(loggedUser.getId());

                if (dbCart != null) {
                    sessionCart = dbCart;
                } else {
                    // Se non esiste nel DB, crea un nuovo carrello vuoto
                    sessionCart = new Cart();
                    // Imposta l'ID dell'utente se il tuo modello lo supporta
                    // sessionCart.setClienteId(loggedUser.getId());
                }

                // Salva in sessione
                session.setAttribute("cart", sessionCart);
            }

            // 4. Calcola il totale del carrello
            double total = calculateCartTotal(sessionCart);
            session.setAttribute("cartTotal", total);

            // 5. Forward alla JSP
            request.getRequestDispatcher("/cart.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving cart for user: " + loggedUser.getId(), e);
            request.setAttribute("errorMessage", "Errore nel recupero del carrello: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utente loggedUser = (Utente) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            Cart sessionCart = (Cart) session.getAttribute("cart");

            if ("add".equals(action)) {
                String productId = request.getParameter("productId");
                String quantity = request.getParameter("quantity");

                if (productId != null && quantity != null) {
                    // Crea o recupera il carrello
                    if (sessionCart == null || sessionCart.getId() == 0) {
                        Cart dbCart = cartService.findCartByCostumer(loggedUser.getId());
                        if (dbCart != null) {
                            sessionCart = dbCart;
                        } else {
                            sessionCart = new Cart();
                            // Salva il nuovo carrello
                            cartService.addCart(sessionCart);
                        }
                    }

                    // Simulazione di aggiunta prodotto (adatta al tuo modello)
                    // In un'implementazione reale, useresti cartService
                    Prodotto prodotto = new Prodotto(); // Dovresti recuperarlo dal DB
                    prodotto.setId(Integer.parseInt(productId));

                    ItemCart item = new ItemCart();
                    item.setProdotto(prodotto);
                    item.setQuantity(Integer.parseInt(quantity));

                    sessionCart.getItems().add(item);
                    cartService.updateCart(sessionCart);
                }

            } else if ("update".equals(action)) {
                String productId = request.getParameter("productId");
                String quantity = request.getParameter("quantity");

                if (productId != null && quantity != null && sessionCart != null) {
                    // Aggiorna quantità nel carrello
                    for (ItemCart item : sessionCart.getItems()) {
                        if (item.getProdotto().getId() == Integer.parseInt(productId)) {
                            item.setQuantity(Integer.parseInt(quantity));
                            break;
                        }
                    }
                    cartService.updateCart(sessionCart);
                }

            } else if ("remove".equals(action)) {
                String productId = request.getParameter("productId");

                if (productId != null && sessionCart != null) {
                    // Rimuovi prodotto dal carrello
                    sessionCart.getItems().removeIf(
                            item -> item.getProdotto().getId() == Integer.parseInt(productId)
                    );
                    cartService.updateCart(sessionCart);
                }

            } else if ("clear".equals(action)) {
                if (sessionCart != null) {
                    sessionCart.getItems().clear();
                    cartService.clearCart(sessionCart);
                }
            }

            // Dopo qualsiasi operazione, ricarica e calcola totale
            if (sessionCart != null && sessionCart.getId() != 0) {
                Cart updatedCart = cartService.findCartById(sessionCart.getId());
                if (updatedCart != null) {
                    sessionCart = updatedCart;
                }
            }

            double total = calculateCartTotal(sessionCart);
            session.setAttribute("cartTotal", total);
            session.setAttribute("cart", sessionCart);

            // Redirect per evitare re-invio del form
            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format", e);
            request.setAttribute("errorMessage", "Formato dei dati non valido");
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing cart operation", e);
            request.setAttribute("errorMessage", "Errore nell'operazione: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private double calculateCartTotal(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return 0.0;
        }

        double total = 0.0;
        for (ItemCart item : cart.getItems()) {
            if (item.getProdotto() != null) {
                total += item.getProdotto().getPrezzo() * item.getQuantity();
            }
        }
        return total;
    }
}