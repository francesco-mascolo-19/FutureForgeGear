package database;

import enumerativeTypes.Categoria;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.LocalBean;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import model.Prodotto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import model.User.Cliente;
import model.User.Utente;

@Singleton
@Startup
@DataSourceDefinition(
        name = "jdbc/FutureForgeGearDS",
        className = "com.mysql.cj.jdbc.Driver",
        url = "jdbc:mysql://localhost:3306/FutureForgeGearDB",
        user = "root",
        password = "Francesco03!",
        properties={"connectionAttributes=;create=true"}
)
@LocalBean
public class DatabasePopulator {

    @PersistenceContext(unitName = "FutureForgeGearPU")
    private EntityManager em;

    Prodotto p1=new Prodotto("Computer Gaming Ryzen 7 – RTX 4060", "PC da gaming ad alte prestazioni con processore Ryzen 7 di ultima generazione, ideale per giochi AAA in Full HD e 2K. Raffreddamento silenzioso e case RGB.", 1299.99,  null , Categoria.FISSI);
    Prodotto p2=new Prodotto("Computer da Ufficio Intel i5", "Desktop affidabile, silenzioso e a basso consumo, perfetto per studio, smart working e software da ufficio. Avvio rapido e massima stabilità.", 649.99,null ,Categoria.FISSI);
    Prodotto p3=new Prodotto("Workstation Creativa Ryzen 9", "Potente workstation progettata per editing video, rendering 3D e grafica professionale. Elevate prestazioni multi-core e memoria ad alta velocità.", 1799.9, null ,Categoria.FISSI);
    Prodotto p4=new Prodotto("Computer Compatto Mini-ITX", "PC compatto adatto a casa e ufficio, veloce e pratico, con consumi ridotti. Perfetto per navigazione, streaming e applicazioni leggere.", 499.99, null,Categoria.FISSI);
    Prodotto p5=new Prodotto("Gaming Budget Intel i3 – GTX 1650", "Desktop entry-level perfetto per gaming leggero ed e-sports. Buon equilibrio tra prestazioni e prezzo, ideale per Fortnite, Valorant e simili.", 749.99, null ,Categoria.FISSI);

    Utente cliente = new Utente("Francesco", "mascolo", "f.mascolo@gmail.com", "password", Ruolo.CLIENTE);
    // record

    @PostConstruct
    public void populateDB(){
        System.out.println("HO INIZIATO IL POPOLAMENTOOOOOOOOO!! \n");
        System.out.println(em);
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);
        em.persist(p5);
        em.persist(cliente);
    }

    @PreDestroy
    public void clearDB(){
        em.remove(p1);
        em.remove(p2);
        em.remove(p3);
        em.remove(p4);
        em.remove(p5);
        em.remove(cliente);
        em.clear();
    }
}