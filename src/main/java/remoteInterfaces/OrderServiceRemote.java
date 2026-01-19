package remoteInterfaces;

import model.OrderManagement.ItemCartDTO;
import model.OrderManagement.Ordine;
import enumerativeTypes.Stato;
import model.UserManagement.GestoreOrdini;


import java.sql.Date;
import java.util.List;
import jakarta.ejb.Remote;

@Remote
public interface OrderServiceRemote {
    Ordine addOrder(Ordine order);
    Ordine findOrderById(int id);
    List<Ordine> findAllOrders();
    List<Ordine> findOrdersByGestore(long userId);
    List<GestoreOrdini> findAllGestoreOrdini();
    void updateOrder(Ordine order);
    void removeOrder(int id);
    List<Ordine> findOrdersByCostumer(long userId);
    List<Ordine> findByPrize(Double prezzo);
    List<Ordine> findByDate(Date date);
    List<Ordine> findByState(Stato stato);

    List<ItemCartDTO> deserializeItems(List<String> serializedItems);
}