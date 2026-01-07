package model.UserManagement;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import model.OrderManagement.Prodotto;
import java.util.List;

@Entity
@DiscriminatorValue("FORNITORE")
public class Fornitore extends Utente {

    @OneToMany(mappedBy = "fornitore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prodotto> prodottiForniti;

    public Fornitore() {
        super();
    }

    public Fornitore(List<Prodotto> prodottiForniti) {
        super();
        this.prodottiForniti = prodottiForniti;
    }

    public List<Prodotto> getProdottiForniti() {
        return prodottiForniti;
    }

    public void setProdottiForniti(List<Prodotto> prodottiForniti) {
        this.prodottiForniti = prodottiForniti;
    }

    @Override
    public String toString() {
        return super.toString() + " Prodotti:" + (prodottiForniti != null ? prodottiForniti.toString() : "[]");
    }
}