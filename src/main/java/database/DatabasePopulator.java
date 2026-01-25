package database;
import Utils.ImageUtil;
import enumerativeTypes.Categoria;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import model.OrderManagement.ItemCartDTO;
import model.OrderManagement.Ordine;
import model.OrderManagement.Prodotto;
import model.RequestManagement.OrderRequest;
import model.RequestManagement.ProductRequest;
import model.UserManagement.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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


    public void createProdotto(Prodotto prodotto, Fornitore fornitore) {
        fornitore.addProdotto(prodotto.getId()); // Aggiungi i prodotti alla lista del fornitore
        prodotto.setFornitore(fornitore.toDTO().getFornitoreID());
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



    Fornitore fornitore1= new Fornitore("Mario", "Rossi", "mario.rossi@example.com", "password");



    /* Prodotti */

    //COMPUTER FISSI
    Prodotto p1=new Prodotto("Computer Gaming Ryzen 7 – RTX 4060", "PC da gaming ad alte prestazioni con processore Ryzen 7 di ultima generazione, ideale per giochi AAA in Full HD e 2K. Raffreddamento silenzioso e case RGB.", 1299.99,  ImageUtil.readAndCompressImage("images/pcfisso/pcfisso3.jpg") ,Categoria.FISSI, 3, true, true);
    Prodotto p2=new Prodotto("Computer da Ufficio Intel i5", "Desktop affidabile, silenzioso e a basso consumo, perfetto per studio, smart working e software da ufficio. Avvio rapido e massima stabilità.", 649.99,  ImageUtil.readAndCompressImage("images/pcfisso/pcfisso5.jpg") ,Categoria.FISSI, 5, true, true);
    Prodotto p3=new Prodotto("Workstation Creativa Ryzen 9", "Potente workstation progettata per editing video, rendering 3D e grafica professionale. Elevate prestazioni multi-core e memoria ad alta velocità.", 1799.9, ImageUtil.readAndCompressImage("images/pcfisso/pcfisso4.jpg") ,Categoria.FISSI, 2, true, true);
    Prodotto p4=new Prodotto("Computer Compatto Mini-ITX", "PC compatto adatto a casa e ufficio, veloce e pratico, con consumi ridotti. Perfetto per navigazione, streaming e applicazioni leggere.", 499.99, ImageUtil.readAndCompressImage("images/pcfisso/pcfisso6.jpg") ,Categoria.FISSI, 4, true, true);
    Prodotto p5=new Prodotto("Gaming Budget Intel i3 – GTX 1650", "Desktop entry-level perfetto per gaming leggero ed e-sports. Buon equilibrio tra prestazioni e prezzo, ideale per Fortnite, Valorant e simili.", 749.99, ImageUtil.readAndCompressImage("images/pcfisso/pcfisso7.jpg") ,Categoria.FISSI, 7, true, true);

    //COMPUTER PORTATILI
    Prodotto p6= new Prodotto("Laptop Ultrabook","Ultrabook leggero e super portatile, ideale per università, lavoro in mobilità e multitasking veloce. Batteria a lunga durata e display Full HD.",1099.99,ImageUtil.readAndCompressImage("images/pcportatili/pcportatile3.jpg"),Categoria.PORTATILI, 12, true, true );
    Prodotto p7= new Prodotto("Notebook Gaming Ryzen 5","Laptop da gaming entry-level perfetto per giochi competitivi e utilizzo misto. Ottimo rapporto qualità-prezzo.",899.99,ImageUtil.readAndCompressImage("images/pcportatili/pcportatile4.jpg"),Categoria.PORTATILI, 7, true, true );
    Prodotto p8= new Prodotto("Laptop Professionale Intel i9","Notebook ad alte prestazioni per professionisti creativi. Ottimo per editing video, Photoshop, modeling 3D e rendering leggero.",1499.99,ImageUtil.readAndCompressImage("images/pcportatili/pcportatile5.jpg"),Categoria.PORTATILI, 8, true, true );
    Prodotto p9= new Prodotto("Chromebook Student","Portatile economico e veloce per studenti, navigazione web, streaming e studio online. Avvio istantaneo e batteria molto lunga.",349.99,ImageUtil.readAndCompressImage("images/pcportatili/pcportatile6.jpg"),Categoria.PORTATILI, 3, true, true );
    //Prodotto p10= new Prodotto("Laptop Business AMD Ryzen 3","Perfetto per smart working e ufficio, con ottime prestazioni per Word, Excel, videoconferenze e navigazione. Silenzioso e affidabile.",549.99,ImageUtil.readAndCompressImage("images/pcportatili/pcportatile6.jpg"),Categoria.PORTATILI, 270, false, true );


    //COMPONENTI
    Prodotto p11= new Prodotto("Scheda Video NVIDIA RTX 4060 8GB","GPU moderna ed efficiente, ideale per gaming 1080p/1440p, editing video e rendering 3D leggero. Consumi ridotti e ottimo rapporto qualità-prezzo.",379.99,ImageUtil.readAndCompressImage("images/componenti/componenti1.jpg"),Categoria.COMPONENTI, 5, true, true );
    Prodotto p12= new Prodotto("Processore AMD Ryzen 7 5800X","CPU 8 core / 16 thread perfetta per gaming competitivo, streaming e produttività. Compatibile con piattaforma AM4.",259.99,ImageUtil.readAndCompressImage("images/componenti/componenti2.jpg"),Categoria.COMPONENTI, 7, true, true );
    Prodotto p13= new Prodotto("SSD NVMe 1TB PCIe 4.0","Unità NVMe ultraveloce con letture fino a 5000 MB/s. Ideale per Windows, programmi pesanti e caricamenti rapidissimi.",89.99,ImageUtil.readAndCompressImage("images/componenti/componenti3.jpg"),Categoria.COMPONENTI, 3, true, true );
    Prodotto p14= new Prodotto("RAM DDR4 16GB (2×8GB) 3200MHz","Kit prestante e compatibile con la maggior parte delle schede madri. Migliora notevolmente reattività e multitasking.",59.99,ImageUtil.readAndCompressImage("images/componenti/componenti4.jpg"),Categoria.COMPONENTI, 1, true, true );
    //Prodotto p15= new Prodotto("Alimentatore 650W","PSU affidabile con certificazione 80+ Bronze, ventola silenziosa e protezioni complete. Ideale per build gaming di fascia media.",59.99,ImageUtil.readAndCompressImage("images/componenti/componenti5.jpg"),Categoria.COMPONENTI, 270, true, true );

    //PERIFERICHE
    Prodotto p16= new Prodotto("Mouse Gaming RGB","Mouse ergonomico con sensore da 12.000 DPI regolabili, illuminazione RGB e 6 tasti programmabili. Perfetto per gaming e lavoro.",29.99,ImageUtil.readAndCompressImage("images/periferiche/periferiche3.png"),Categoria.PERIFERICHE, 10, true, true );
    Prodotto p17= new Prodotto("Tastiera Meccanica","Tastiera meccanica compatta con switch rossi silenziosi, costruita in alluminio e retroilluminazione LED a più livelli.",59.99,ImageUtil.readAndCompressImage("images/periferiche/periferiche4.jpg"),Categoria.PERIFERICHE, 2, true, true );
    Prodotto p18= new Prodotto("Cuffie Wireless","Cuffie over-ear con audio surround virtuale, microfono removibile e batteria fino a 20 ore. Compatibili PC/Console.",69.99,ImageUtil.readAndCompressImage("images/periferiche/periferiche5.jpg"),Categoria.PERIFERICHE, 5, true, true );
    Prodotto p19= new Prodotto("Monitor 24'' FullHD","Monitor a 144Hz con pannello IPS, colori vividi e bassa latenza. Ideale per gaming fluido e produttività.",159.99,ImageUtil.readAndCompressImage("images/periferiche/periferiche6.jpg"),Categoria.PERIFERICHE, 12, true, true );
    Prodotto p20= new Prodotto("Microfono USB","Microfono a condensatore con filtro anti-pop e stand regolabile. Ottimo per streaming, call e registrazioni vocali.",49.99,ImageUtil.readAndCompressImage("images/periferiche/periferiche8.jpg"),Categoria.PERIFERICHE, 3, false, true ); //la foto giusta dovrebbe essere "periferiche7.jpg", ma così vediamo che anche il caso senza foto è gestito

    Indirizzo ind= new Indirizzo("Italia","Salerno","Sarno","Via Vesuvio", 4, 8006);
    Cliente cliente = new Cliente("Francesco", "Mascolo", "f.mascolo@gmail.com", "password", ind);


    /*
    List<Prodotto> prodotti = Arrays.asList(p1,p2, p3, p4);
    Magazzino magazzino= new Magazzino(ind, prodotti);*/
    Magazzino magazzino= new Magazzino(ind);
    Magazziniere magazziniere = new Magazziniere("Luigi","Bianchi","lbianchi@geg.it","password", magazzino);



    ItemCartDTO item1= new ItemCartDTO(p1.getId(),2);
    ItemCartDTO item2= new ItemCartDTO(p3.getId(),3);
    List<ItemCartDTO> listItem = Arrays.asList(item1, item2);

    GestoreOrdini gestore1= new GestoreOrdini("Francesco","Lamanna","f.lamanna3@geg.it","password");






    @PostConstruct
    public void populateDB(){
        System.out.println("HO INIZIATO IL POPOLAMENTOOOOOOOOO!! \n");

        em.createQuery("DELETE FROM Prodotto p").executeUpdate();
        em.createQuery("DELETE FROM Fornitore f").executeUpdate();
        em.createQuery("DELETE FROM Cliente c").executeUpdate();
        em.createQuery("DELETE FROM Utente").executeUpdate();



        em.flush();

        em.persist(fornitore1);
        em.persist(p1); em.flush();
        em.persist(p2); em.flush();
        em.persist(p3);em.flush();
        em.persist(p4);em.flush();
        em.persist(p5);em.flush();
        em.persist(p6);em.flush();
        em.persist(p7);em.flush();
        em.persist(p8);em.flush();
        em.persist(p9);em.flush();
        //em.persist(p10);em.flush();
        em.persist(p11);em.flush();
        em.persist(p12);em.flush();
        em.persist(p13);em.flush();
        em.persist(p14);em.flush();
        //em.persist(p15);em.flush();
        em.persist(p16);em.flush();
        em.persist(p17);em.flush();
        em.persist(p18);em.flush();
        em.persist(p19);em.flush();
        em.persist(p20);em.flush();
        //em.persist(p21);em.flush();

        em.persist(cliente);



        createProdotto(p1, fornitore1);
        createProdotto(p2, fornitore1);
        createProdotto(p3, fornitore1);
        createProdotto(p4, fornitore1);
        createProdotto(p5, fornitore1);
        createProdotto(p6, fornitore1);
        createProdotto(p7, fornitore1);
        createProdotto(p8, fornitore1);
        createProdotto(p9, fornitore1);
        //createProdotto(p10, fornitore1);
        createProdotto(p11, fornitore1);
        createProdotto(p12, fornitore1);
        createProdotto(p13, fornitore1);
        createProdotto(p14, fornitore1);
        //createProdotto(p15, fornitore1);
        createProdotto(p16, fornitore1);
        createProdotto(p17, fornitore1);
        createProdotto(p18, fornitore1);
        createProdotto(p19, fornitore1);
        createProdotto(p20, fornitore1);


        em.flush();

        Ordine ordine = new Ordine(cliente.getId(),10.3, listItem);
        Ordine ordine2 = new Ordine(cliente.getId(),24.1, listItem);

        em.persist(ordine);
        em.persist(ordine2);
        em.persist(gestore1);
        giveOrdine(ordine, gestore1);

        em.flush();


        em.persist(magazziniere);
        OrderRequest orderRequest= new OrderRequest(magazziniere.getId(), gestore1.getId(), LocalDateTime.now(), ordine.getId(), "Ao bello");
        OrderRequest orderRequest2= new OrderRequest(magazziniere.getId(), gestore1.getId(), LocalDateTime.now(), ordine2.getId(), "Ao bello 2");
        em.persist(orderRequest);
        em.persist(orderRequest2);

        em.flush();
        ProductRequest productRequest = new ProductRequest(magazziniere.getId(), fornitore1.getId(), LocalDateTime.now(), p4.getId(), 3, "ProvaRichiesta");
        em.persist(productRequest);
        em.flush();

        System.out.println("Popolamento completato");


    }

    @PreDestroy
    public void clearDB(){
        em.remove(p1);
        em.remove(p2);
        em.remove(p3);
        em.remove(p4);
        em.remove(cliente);
        em.remove(fornitore1);
        em.clear();
    }

}