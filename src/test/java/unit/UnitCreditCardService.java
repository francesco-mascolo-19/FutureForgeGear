package unit;

import model.OrderManagement.CreditCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CreditCardService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class UnitCreditCardService {

    private CreditCardService service;

    @BeforeEach
    void setUp() {
        service = new CreditCardService();
    }

    private void injectCreditCard(CreditCardService target, CreditCard card) {
        try {
            Field f = CreditCardService.class.getDeclaredField("creditCard");
            f.setAccessible(true);
            f.set(target, card);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile iniettare CreditCard nel service per il test", e);
        }
    }

    @Test
    void effettuaPagamento_quandoPayWithCardTrue_ritorna1() {
        CreditCard cardStub = new CreditCard("Mario Rossi", "1111222233334444", "12/30", "123") {
            @Override
            public boolean payWithCard(double importo) {
                return true;
            }
        };
        injectCreditCard(service, cardStub);

        int result = service.effettuaPagamento(50.0);

        assertEquals(1, result);
    }

    @Test
    void effettuaPagamento_quandoPayWithCardFalse_ritorna0() {
        CreditCard cardStub = new CreditCard("Mario Rossi", "1111222233334444", "12/30", "123") {
            @Override
            public boolean payWithCard(double importo) {
                return false;
            }
        };
        injectCreditCard(service, cardStub);

        int result = service.effettuaPagamento(50.0);

        assertEquals(0, result);
    }

    @Test
    void effettuaPagamento_senzaInit_oCreditCardNull_lanciaNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.effettuaPagamento(10.0));
    }
}