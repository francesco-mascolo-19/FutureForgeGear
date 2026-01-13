package model;

import enumerativeTypes.Categoria;
import model.OrderManagement.Ordine; // Questa è la tua Entity JPA
import model.OrderManagement.Prodotto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class OrdineDAO {
    private static DataSource ds;
    private static final String TABLE_NAME = "Ordine";

    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            ds = (DataSource) envCtx.lookup("jdbc/ingrosso");

        } catch (NamingException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public List<Ordine> getOrdini(String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Ordine> ordini = new ArrayList<>();
        // Suppongo che la tua tabella abbia un campo per l'email dell'utente
        String selectSQL = "SELECT * FROM " + TABLE_NAME + " WHERE Email_cliente = ? ORDER BY data DESC";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Ordine ordine = mapResultSetToOrdine(rs);
                ordini.add(ordine);
            }

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

        return ordini;
    }

    public Ordine getOrdineByNumero(int numeroOrdine) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String selectSQL = "SELECT * FROM " + TABLE_NAME + " WHERE id_ordine = ?";
        Ordine ordine = null;

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, numeroOrdine);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                ordine = mapResultSetToOrdine(rs);
            }

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

        return ordine != null ? ordine : new Ordine();
    }

    public void aggiornaStatoOrdine(int numeroOrdine, String nuovoStato) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String updateSQL = "UPDATE " + TABLE_NAME + " SET stato = ? WHERE id_ordine = ?";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, nuovoStato);
            preparedStatement.setInt(2, numeroOrdine);

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

    public List<Prodotto> getProdotti(int idO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Prodotto> prodotti = new ArrayList<>();
        String selectSQL = "SELECT p.idProdotto, p.Quantita, p.Prezzo, p.Nome, p.Descrizione, p.Categoria, p.Sconto, p.Foto, d.quantita as quantita_ordinata " +
                "FROM Prodotto p " +
                "JOIN DettagliOrdine d ON p.idProdotto = d.idProdotto " +
                "WHERE d.idOrdine = ?";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, idO);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Prodotto prodotto = new Prodotto();
                prodotto.setId(rs.getInt("idProdotto"));
                prodotto.setPrezzo(rs.getDouble("Prezzo"));
                prodotto.setNome(rs.getString("Nome"));
                prodotto.setDescrizione(rs.getString("Descrizione"));

                // Conversione da String a Categoria enum
                String categoriaString = rs.getString("Categoria");
                Categoria categoria = Categoria.valueOf(categoriaString);
                prodotto.setCategoria(categoria);


                prodotti.add(prodotto);
            }
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

        return prodotti;
    }

    public int doSave(Ordine ordine) throws SQLException {
        int idOrdineGenerato = -1;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        // Suppongo che la tua tabella abbia campi compatibili con l'Entity
        String insertSQL = "INSERT INTO Ordine (totale, user_id, data, stato, items) VALUES (?, ?, ?, ?, ?)";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setDouble(1, ordine.getTotale());
            preparedStatement.setLong(2, ordine.getUserId());

            // Converti LocalDateTime a java.sql.Timestamp
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(ordine.getDate());
            preparedStatement.setTimestamp(3, timestamp);

            preparedStatement.setString(4, ordine.getStato().toString());

            // Serializza gli items in JSON (come fa la tua Entity)
            if (ordine.getItems() != null) {
                StringBuilder itemsJson = new StringBuilder("[");
                for (String item : ordine.getItems()) {
                    itemsJson.append(item).append(",");
                }
                if (ordine.getItems().size() > 0) {
                    itemsJson.deleteCharAt(itemsJson.length() - 1);
                }
                itemsJson.append("]");
                preparedStatement.setString(5, itemsJson.toString());
            } else {
                preparedStatement.setString(5, "[]");
            }

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("righe inserite: " + rowsInserted);

            // Recupera l'ID generato
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                idOrdineGenerato = generatedKeys.getInt(1);
                System.out.println("Ultimo ID inserito: " + idOrdineGenerato);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (generatedKeys != null)
                    generatedKeys.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

        return idOrdineGenerato;
    }

    // Metodo vecchio per compatibilità
    public int doSave(String indirizzo, double totale, String stato, int numProdotti, int Iva, String Email, String citta, int CAP, String provincia) throws SQLException {
        // Non posso creare un Ordine JPA con questi campi
        // Forse dovresti usare un'altra classe per questa operazione
        System.out.println("ATTENZIONE: Metodo deprecato. Usa doSave(Ordine ordine) invece.");
        return -1;
    }

    public List<Ordine> getAllOrdini() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<Ordine> ordini = new ArrayList<>();
        String selectSQL = "SELECT * FROM " + TABLE_NAME + " ORDER BY data DESC";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Ordine ordine = mapResultSetToOrdine(rs);
                ordini.add(ordine);
            }

        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return ordini;
    }

    // Metodo helper per mappare il ResultSet all'Entity Ordine JPA
    private Ordine mapResultSetToOrdine(ResultSet rs) throws SQLException {
        Ordine ordine = new Ordine();

        // Mappa i campi dal database all'Entity
        // ATTENZIONE: La tua Entity Ordine JPA ha campi diversi dalla tabella!
        // Suppongo che la tabella abbia questi campi:
        // id_ordine, totale, user_id, data, stato, items

        ordine.setId(rs.getLong("id_ordine"));
        ordine.setTotale(rs.getDouble("totale"));

        // Controlla se il campo è user_id o userId
        try {
            ordine.setUserId(rs.getLong("user_id"));
        } catch (SQLException e) {
            ordine.setUserId(rs.getLong("userId"));
        }


        // Imposta lo stato
        String statoString = rs.getString("stato");
        if (statoString != null) {
            try {
                enumerativeTypes.Stato stato = enumerativeTypes.Stato.valueOf(statoString);
                ordine.setStato(stato);
            } catch (IllegalArgumentException e) {
                ordine.setStato(enumerativeTypes.Stato.PREPARATION);
            }
        }

        // Gestisci gli items (JSON array come stringa)
        String itemsJson = rs.getString("items");
        if (itemsJson != null && !itemsJson.isEmpty()) {
            // Parsa il JSON array in una lista di stringhe
            // Questo è un parsing semplice, potresti aver bisogno di una libreria JSON
            itemsJson = itemsJson.trim();
            if (itemsJson.startsWith("[") && itemsJson.endsWith("]")) {
                itemsJson = itemsJson.substring(1, itemsJson.length() - 1);
                String[] itemsArray = itemsJson.split(",");
                List<String> itemsList = new ArrayList<>();
                for (String item : itemsArray) {
                    if (!item.trim().isEmpty()) {
                        itemsList.add(item.trim());
                    }
                }
                ordine.setItems(itemsList);
            }
        }

        return ordine;
    }

    // Metodo per salvare i dettagli dell'ordine
    public void salvaDettagliOrdine(int idOrdine, int idProdotto, int quantita, double prezzoUnitario) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String insertSQL = "INSERT INTO DettagliOrdine (idOrdine, idProdotto, quantita, prezzo_unitario) VALUES (?, ?, ?, ?)";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(insertSQL);

            preparedStatement.setInt(1, idOrdine);
            preparedStatement.setInt(2, idProdotto);
            preparedStatement.setInt(3, quantita);
            preparedStatement.setDouble(4, prezzoUnitario);

            preparedStatement.executeUpdate();

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