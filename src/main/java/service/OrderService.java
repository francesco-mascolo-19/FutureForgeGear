package service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import model.OrderManagement.Ordine;
import enumerativeTypes.Stato;
import remoteInterfaces.OrderServiceRemote;

import java.sql.Date;
import java.util.List;

@Stateless
public class OrderService implements OrderServiceRemote {

    @PersistenceContext(unitName = "FutureForgeGearPU")
    private EntityManager em;

    @Override
    public void addOrder(Ordine order) {
        em.persist(order);
    }

    @Override
    public Ordine findOrderById(int id) {
        // Converti int a Long per la ricerca
        return em.find(Ordine.class, (long) id);
    }

    @Override
    public List<Ordine> findAllOrders() {
        TypedQuery<Ordine> query = em.createNamedQuery("Ordine.TROVA_TUTTI", Ordine.class);
        return query.getResultList();
    }

    @Override
    public void updateOrder(Ordine order) {
        em.merge(order);
    }

    @Override
    public void removeOrder(int id) {
        Ordine order = em.find(Ordine.class, (long) id);
        if (order != null) {
            em.remove(order);
        }
    }

    @Override
    public List<Ordine> findOrdersByCostumer(int userId) {
        TypedQuery<Ordine> query = em.createNamedQuery("Ordine.TROVA_PER_UTENTE", Ordine.class);
        query.setParameter("userId", (long) userId);
        return query.getResultList();
    }

    @Override
    public List<Ordine> findByPrize(Double prezzo) {
        TypedQuery<Ordine> query = em.createNamedQuery("Ordine.TROVA_PER_TOTALE", Ordine.class);
        query.setParameter("totale", prezzo);
        return query.getResultList();
    }

    @Override
    public List<Ordine> findByDate(Date date) {
        TypedQuery<Ordine> query = em.createNamedQuery("Ordine.TROVA_PER_DATA", Ordine.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<Ordine> findByState(Stato stato) {
        TypedQuery<Ordine> query = em.createNamedQuery("Ordine.TROVA_PER_STATO", Ordine.class);
        query.setParameter("stato", stato);
        return query.getResultList();
    }
}