package control;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderManagement.*;
import model.UserManagement.Utente;
import remoteInterfaces.CatalogoRemote;
import remoteInterfaces.OrderServiceRemote;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @EJB
    private OrderServiceRemote orderService;

    @EJB
    private CatalogoRemote catalogoRemote;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("loggedUser");
        Cart cart = (Cart) session.getAttribute("cart");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendRedirect("card.jsp?error=empty");
            return;
        }

        List<ItemCartDTO> itemsDTO = cart.getItems().stream()
                .map(item -> new ItemCartDTO(item.getProdotto().getId(), item.getQuantity()))
                .collect(Collectors.toList());

        Ordine order = orderService.addOrder(new Ordine(utente.getId(), cart.calculateTotal(),itemsDTO));
        System.out.println("Ordine creato con ID: " + order.getId());
        session.setAttribute("order", order);

        List<Ordine> orders = (List<Ordine>) session.getAttribute("orders");
        orders.add(order);
        session.setAttribute("orders", orders);

        List<ItemCart> items = cart.getItems();
        request.setAttribute("itemsCart", items);

        session.removeAttribute("cart");

        response.sendRedirect("confirmOrder.jsp");
    }
}