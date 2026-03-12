package control;

import enumerativeTypes.Ruolo;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.OrderManagement.Ordine;
import model.UserManagement.Utente;
import remoteInterfaces.OrderServiceRemote;
import remoteInterfaces.UserServiceRemote;

import java.io.IOException;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private UserServiceRemote userService;

    @EJB
    private OrderServiceRemote orderService;

    private static final String CORRECT_EMAIL = "user@example.com";
    private static final String CORRECT_PASSWORD = "correctPassword123";

    public boolean validateEmail(String email) {
        return CORRECT_EMAIL.equals(email);
    }

    public static boolean validatePassword(String password) {
        return CORRECT_PASSWORD.equals(password);
    }

    public boolean validateLogin(String email, String password) {
        return validateEmail(email) && validatePassword(password);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Utente> clienti = userService.findAllUsers();
        for (Utente u : clienti) {
            System.out.println(u.toString());
        }

        request.setAttribute("clienti", clienti);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // Verifica l'esistenza dell'utente nel database
            Utente loggedUser = userService.findUserByEmail(email);

            // Se l'utente esiste e la password è corretta
            if (loggedUser != null && loggedUser.getPassword().equals(password)) {

                request.getSession().setAttribute("loggedUser", loggedUser);

                // Recupera gli ordini a suo carico
                List<Ordine> orders = orderService.findOrdersByCostumer(loggedUser.getId());
                System.out.println("\n ecco che ordini ho trovato per l'utente " + loggedUser.getNome());
                for (Ordine o : orders) {
                    System.out.println(o.toString());
                }
                request.getSession().setAttribute("orders", orders);

            } else {
                // Login fallito -> forward e STOP
                request.setAttribute("loginError", "Login fallito. Email o password errati.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // Redirect basato sul ruolo (solo se login OK)
            if (loggedUser.getRuolo() == Ruolo.CLIENTE) {
                response.sendRedirect(request.getContextPath() + "/home2.jsp");
            } else {
                System.out.println(loggedUser.toString());
                response.sendRedirect(request.getContextPath() + "/Profile.jsp");
            }

        } catch (Exception e) {
            // Qualsiasi errore (es. utente non trovato) -> forward e STOP
            request.setAttribute("loginError", "Login fallito. Email o password errati.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
    }
}