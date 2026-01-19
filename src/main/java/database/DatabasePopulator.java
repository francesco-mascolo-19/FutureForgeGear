package database;

import enumerativeTypes.Categoria;
import enumerativeTypes.Ruolo;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.LocalBean;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import model.OrderManagement.Prodotto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import model.UserManagement.Cliente;
import model.UserManagement.Fornitore;
import model.UserManagement.Indirizzo;
import model.UserManagement.Utente;
import model.OrderManagement.ItemCartDTO;
import model.OrderManagement.Ordine;
import model.RequestManagement.OrderRequest;
import model.RequestManagement.Request;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;

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

    public void createProdotto(Prodotto prodotto, Fornitore fornitore){
        fornitore.addProdotto((prodotto.getId()));
        em.merge(prodotto);
        em.merge(fornitore);
    }

    public void giveOrdine(Ordine ordine, GestoreOrdini gestoreOrdini) {
        gestoreOrdini.aggiungiOrdine(ordine);
        ordine.setIdGestore(gestoreOrdini);
        em.merge(ordine);
        em.merge(gestoreOrdini);
    }

    // Creazione di un admin
    /*Admin admin = new Admin();
        admin.setNome("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("admin123");
        admin.setPermessi("GESTIONE_COMPLETA");
        admin.setRuolo("ADMIN"); */
    // record

    Fornitore utenteFornitore1=new Fornitore("Mario", "Rossi", "mario.rossi@example.com", "mrossi", "abc");


    Prodotto p1=new Prodotto("Computer Gaming Ryzen 7 – RTX 4060", "PC da gaming ad alte prestazioni con processore Ryzen 7 di ultima generazione, ideale per giochi AAA in Full HD e 2K. Raffreddamento silenzioso e case RGB.", 1299.99,  /*null,*/ Categoria.FISSI, 3, true);
    Prodotto p2=new Prodotto("Computer da Ufficio Intel i5", "Desktop affidabile, silenzioso e a basso consumo, perfetto per studio, smart working e software da ufficio. Avvio rapido e massima stabilità.", 649.99,/*null,*/Categoria.FISSI, 5, true);
    Prodotto p3=new Prodotto("Workstation Creativa Ryzen 9", "Potente workstation progettata per editing video, rendering 3D e grafica professionale. Elevate prestazioni multi-core e memoria ad alta velocità.", 1799.9, /*null,*/Categoria.FISSI, 2, true);
    Prodotto p4=new Prodotto("Computer Compatto Mini-ITX", "PC compatto adatto a casa e ufficio, veloce e pratico, con consumi ridotti. Perfetto per navigazione, streaming e applicazioni leggere.", 499.99, /*null,*/Categoria.FISSI, 4, true);
    Prodotto p5=new Prodotto("Gaming Budget Intel i3 – GTX 1650", "Desktop entry-level perfetto per gaming leggero ed e-sports. Buon equilibrio tra prestazioni e prezzo, ideale per Fortnite, Valorant e simili.", 749.99, /*null,*/Categoria.FISSI, 7, true);

    // Parametri: nome, cognome, email, username, password, ruolo
    Indirizzo ind= new Indirizzo("Italia","Napoli","Boschetto Fangoso","Via Boschetto", 4, 80033);
    Utente cliente = new Utente("Francesco", "Mascolo", "f.mascolo@gmail.com", "francesco_m", "password");

    List<Prodotto> prodotti = Arrays.asList(p1,p2, p3, p4);
    Magazzino magazzino= new Magazzino(ind, prodotti);
    Magazziniere magazziniere = new Magazziniere("Antonio","Rossi","Arossi@gmail.com","rosso","password", magazzino);

    ItemCartDTO item1= new ItemCartDTO(p1.getId(),2);
    ItemCartDTO item2= new ItemCartDTO(p2.getId(),3);
    List<ItemCartDTO> listItem = Arrays.asList(item1, item2);

    GestoreOrdini gestore1= new GestoreOrdini("Luca","Bianchi","l.bianchi@gmail.com","bianco","password");
    Ordine ordine = new Ordine(cliente.getId(),10.3,listItem);

    @PostConstruct
    public void populateDB(){
        System.out.println("HO INIZIATO IL POPOLAMENTOOOOOOOOO!! \n");
        System.out.println(em);

        em.createQuery("DELETE FROM Prodotto p").executeUpdate();
        em.createQuery("DELETE FROM Fornitore f").executeUpdate();

        // Aggiungi i prodotti alla lista del fornitore
        utenteFornitore1.setProdottiForniti(new ArrayList<>(Arrays.asList(p1, p2, p3, p4,p5)));

        em.persist(utenteFornitore1);
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);
        em.persist(p5);
        em.persist(cliente);

        em.persist(ordine);
        em.persist(gestore1);
        giveOrdine(ordine, gestore1);

        em.flush();
        em.persist(magazziniere);
        OrderRequest orderRequest= new OrderRequest(magazziniere.getId(), gestore1.getId(), LocalDateTime.now(), ordine.getId());
        em.persist(orderRequest);

        System.out.println("MagID: "+magazziniere.getId());
        System.out.println("GestOrdID: "+gestore1.getId());
        System.out.println("Ordine: "+ordine.getId());
        System.out.println(orderRequest);

        em.flush();
    }

    @PreDestroy
    public void clearDB(){
        em.remove(p1);
        em.remove(p2);
        em.remove(p3);
        em.remove(p4);
        em.remove(cliente);
        em.remove(utenteFornitore1);
        em.clear();
    }
}