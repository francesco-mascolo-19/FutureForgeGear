package model.UserManagement;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import model.OrderManagement.Prodotto;
import model.UserManagement.Magazzino;
import java.util.List;
import java.util.ArrayList;

@Entity
@DiscriminatorValue("MAGAZZINIERE")
public class Magazziniere extends Utente{

    @Embedded
    private Magazzino magazzino;

    public Magazziniere() {}
    // Constructor with basic fields
    public Magazziniere(String nome, String cognome, String email, String username, String password, Magazzino magazzino) {
        super(nome, cognome, email, username, password);

    public Magazzino getMagazzino() {return magazzino;}

    public void setMagazzino(Magazzino magazzino) {this.magazzino = magazzino;}

    @Override
    public String toString(){
        return super.toString() + " " + magazzino.toString();
    }


}