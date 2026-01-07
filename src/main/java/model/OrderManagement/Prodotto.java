package model.OrderManagement;

import enumerativeTypes.Categoria;
import jakarta.persistence.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(name = "TROVA_TUTTI", query = "SELECT p FROM Prodotto p"),
        @NamedQuery(name = "TROVA_PER_ID", query = "SELECT p FROM Prodotto p WHERE p.id = :ID"),
        @NamedQuery(name = "TROVA_PER_PREZZO_MINORE", query = "SELECT p FROM Prodotto p WHERE p.prezzo <= :prezzo"),
        @NamedQuery(name = "TROVA_PER_PREZZO_MAGGIORE", query = "SELECT p FROM Prodotto p WHERE p.prezzo >= :prezzo"),
        @NamedQuery(name = "TROVA_PER_CATEGORIA", query = "SELECT p FROM Prodotto p WHERE p.categoria = :categoria"),
        @NamedQuery(name = "TROVA_PER_NOME", query = "SELECT p FROM Prodotto p WHERE p.nome = :nome")
})
@Entity
@Table(name = "Prodotto") // Specifica il nome della tabella nel DB
public class Prodotto implements Serializable {

    public static final String TROVA_PER_ID = "Product.findById";
    public static final String TROVA_PER_PREZZO_MINORE = "Product.findMinusPrize";
    public static final String TROVA_PER_CATEGORIA = "Product.findCategoria";
    public static final String TROVA_PER_NOME = "Product.findByNome";
    public static final String TROVA_PER_PREZZO_MAGGIORE = "Product.findMajorPrize";
    public static final String TROVA_TUTTI = "Product.findTutti";

    @Id
    @GeneratedValue
    @Column(name = "idProdotto") // Mappa alla colonna corretta
    private int id;

    @Column(name = "Nome")
    private String nome;

    @Column(name = "Descrizione")
    private String descrizione;

    @Column(name = "Prezzo")
    private Double prezzo;

    @Column(name = "Quantita")
    private Integer quantita;

    @Column(name = "Sconto")
    private Double sconto;

    @Lob
    @Column(name = "Foto")
    private byte[] foto;

    @Enumerated(EnumType.STRING)
    @Column(name = "Categoria")
    private Categoria categoria;

    //private ImageIcon image;
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    // Costruttori
    public Prodotto() {}

    public Prodotto(String nome, String descrizione, Double prezzo, byte[] foto, Categoria categoria) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.foto = foto;
        this.categoria = categoria;
    }

    // Getter e Setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    // Nuovi getter e setter per i campi mancanti
    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public Double getSconto() {
        return sconto;
    }

    public void setSconto(Double sconto) {
        this.sconto = sconto;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public void setImg(byte[] foto) { // Per compatibilità con il DAO
        this.foto = foto;
    }

    public byte[] getImageBytes() {
        return this.image; // Assumendo che 'image' sia un campo di tipo byte[]
    }



    public void setImageFromIcon(ImageIcon icon) {
        if (icon != null) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write((ImageIO.read((File) icon.getImage().getSource())), "png", bos);
                this.image = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}