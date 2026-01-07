package database;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class DatabaseProducer{
    @Produces
    @PersistenceContext(unitName="FutureForgeGearPU")
    private EntityManager em;
}