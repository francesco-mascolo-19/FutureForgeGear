package model.OrderManagement;

import jakarta.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "Ordine")
@NamedQueries({
        @NamedQuery(name = "Ordine.TROVA_TUTTI", query = "SELECT o FROM Ordine o"),
        @NamedQuery(name = "Ordine.TROVA_PER_UTENTE", query = "SELECT o FROM Ordine o WHERE o.emailCliente = :emailCliente"),
        @NamedQuery(name = "Ordine.TROVA_PER_TOTALE", query = "SELECT o FROM Ordine o WHERE o.totale = :totale"),
        @NamedQuery(name = "Ordine.TROVA_PER_DATA", query = "SELECT o FROM Ordine o WHERE o.dataOrdine = :data"),
        @NamedQuery(name = "Ordine.TROVA_PER_STATO", query = "SELECT o FROM Ordine o WHERE o.stato = :stato")
})
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ORDINE")
    private int idOrdine;

    @Column(name = "indirizzo")
    private String indirizzo;

    @Column(name = "IVA_cliente")
    private int ivaCliente;

    @Column(name = "dataOrdine")
    @Temporal(TemporalType.DATE)
    private Date dataOrdine;

    @Column(name = "totale")
    private double totale;

    @Column(name = "stato")
    private String stato;

    @Column(name = "numeroProdotti")
    private int numeroProdotti;

    @Column(name = "EmailCliente")
    private String emailCliente;

    @Column(name = "citta")
    private String citta;

    @Column(name = "CAP")
    private int cap;

    @Column(name = "provincia")
    private String provincia;

    // Costruttori
    public Ordine() {
    }

    public Ordine(String indirizzo, int ivaCliente, Date dataOrdine, double totale,
                  String stato, int numeroProdotti, String emailCliente,
                  String citta, int cap, String provincia) {
        this.indirizzo = indirizzo;
        this.ivaCliente = ivaCliente;
        this.dataOrdine = dataOrdine;
        this.totale = totale;
        this.stato = stato;
        this.numeroProdotti = numeroProdotti;
        this.emailCliente = emailCliente;
        this.citta = citta;
        this.cap = cap;
        this.provincia = provincia;
    }

    // Getter e Setter (standardizzati)
    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public int getIvaCliente() {
        return ivaCliente;
    }

    public void setIvaCliente(int ivaCliente) {
        this.ivaCliente = ivaCliente;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public double getTotale() {
        return totale;
    }

    public void setTotale(double totale) {
        this.totale = totale;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public int getNumeroProdotti() {
        return numeroProdotti;
    }

    public void setNumeroProdotti(int numeroProdotti) {
        this.numeroProdotti = numeroProdotti;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    // Getter e Setter legacy (per compatibilità)
    public int getNumeroOrdine() {
        return idOrdine;
    }

    public void setNumeroOrdine(int numeroOrdine) {
        this.idOrdine = numeroOrdine;
    }

    public int getIVA_cliente() {
        return ivaCliente;
    }

    public void setIVA_cliente(int IVA) {
        this.ivaCliente = IVA;
    }

    public Date getData() {
        return dataOrdine;
    }

    public void setData(Date data) {
        this.dataOrdine = data;
    }

    public int getCAP() {
        return cap;
    }

    public void setCAP(int x) {
        this.cap = x;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String x) {
        this.emailCliente = x;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dataFormattata = dataOrdine != null ? dateFormat.format(dataOrdine) : "N/A";
        return "Ordine{" +
                "idOrdine=" + idOrdine +
                ", dataOrdine=" + dataFormattata +
                ", totale=" + totale +
                ", stato='" + stato + '\'' +
                ", emailCliente='" + emailCliente + '\'' +
                '}';
    }
}