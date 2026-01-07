package model;


import model.OrderManagement.Cart;
import model.OrderManagement.ItemCart;
import model.OrderManagement.Prodotto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class AcquistoDAO {

    private static DataSource ds;

    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            ds = (DataSource) envCtx.lookup("jdbc/ingrosso");

        } catch (NamingException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public AcquistoDAO() {
    }

    public void inserimentoaq(String provincia, String indirizzo, String cap, String citta, Cart cart, String email,
                              String pagamento, String numeroCarta, String titolareCarta, String scadenzaCarta, String cvv) {
        Date dataCorrente = new Date();
        java.sql.Date dataOrdine = new java.sql.Date(dataCorrente.getTime());
        double prezzotot = 0;

        // MODIFICA QUI: cart ha getItems(), non getProducts()
        // Ogni ItemCart contiene un Prodotto
        for (ItemCart item : cart.getItems()) {
            // Ottieni il prodotto dall'ItemCart
            Prodotto p = item.getProdotto(); // Supponendo che ItemCart abbia questo metodo
            if (p != null && p.getPrezzo() != null) {
                prezzotot += p.getPrezzo();  // getPrezzo() restituisce Double
            }
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String sql1 = "INSERT INTO Ordine (dataOrdine, totale, stato, email, indirizzo, citta, provincia, cap)" +
                "VALUES (?, ?, 'In Preparazione', ?, ?, ?, ?, ?)";
        String sql2 = "SELECT MAX(numeroOrdine) AS numeroOrdineMax FROM ordine";
        String sql3 = "INSERT INTO composizione(IVA, totale, codP, numeroO)" +
                "VALUES(?, ?, ?, ?)";
        String deleteSQL = "UPDATE product SET quantita = quantita - 1 WHERE id = ?"; // Corretto: decrementa quantità
        String sql4 = "INSERT INTO pagamento (tipo, titolare, numero_carta, scadenza, CVV, n_Ordine)" +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setDate(1, dataOrdine);
            preparedStatement.setDouble(2, prezzotot);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, indirizzo);
            preparedStatement.setString(5, citta);
            preparedStatement.setString(6, provincia);
            preparedStatement.setString(7, cap);
            preparedStatement.executeUpdate();

            // Ottieni l'ultimo ID inserito
            ResultSet resultSet = preparedStatement.executeQuery(sql2);
            int numeroOrdineMax = -1;
            if (resultSet.next()) {
                numeroOrdineMax = resultSet.getInt("numeroOrdineMax");
            }

            // Inserisci i prodotti dell'ordine
            for (ItemCart item : cart.getItems()) {
                Prodotto p = item.getProdotto();
                if (p != null) {
                    preparedStatement = connection.prepareStatement(sql3);
                    preparedStatement.setDouble(1, 0); // IVA - da calcolare se necessario
                    preparedStatement.setDouble(2, p.getPrezzo());
                    preparedStatement.setInt(3, p.getId());
                    preparedStatement.setInt(4, numeroOrdineMax);
                    preparedStatement.executeUpdate();
                }
            }

            // Aggiorna le quantità in magazzino
            for (ItemCart item : cart.getItems()) {
                Prodotto p = item.getProdotto();
                if (p != null) {
                    preparedStatement = connection.prepareStatement(deleteSQL);
                    preparedStatement.setInt(1, p.getId());
                    preparedStatement.executeUpdate();
                }
            }

            // Inserisci i dati di pagamento
            preparedStatement = connection.prepareStatement(sql4);
            preparedStatement.setString(1, pagamento);
            preparedStatement.setString(2, titolareCarta);
            preparedStatement.setString(3, numeroCarta);
            preparedStatement.setString(4, scadenzaCarta);
            preparedStatement.setString(5, cvv);
            preparedStatement.setInt(6, numeroOrdineMax);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}