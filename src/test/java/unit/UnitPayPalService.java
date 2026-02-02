package unit;

import model.OrderManagement.PayPal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.PayPalService;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class UnitPayPalService {

    private PayPalService service;

    @BeforeEach
    void setUp() {
        service = new PayPalService();
    }

    private void injectPayPal(PayPalService target, PayPal payPal) {
        try {
            Field f = PayPalService.class.getDeclaredField("payPal");
            f.setAccessible(true);
            f.set(target, payPal);
        } catch (Exception e) {
            throw new RuntimeException("Impossibile iniettare PayPal nel service per il test", e);
        }
    }

    @Test
    void effettuaPagamento_quandoProcessPaymentTrue_ritorna1() {
        PayPal payPalStub = new PayPal("test@mail.com", "pwd") {
            @Override
            public boolean processPayment(double importo) {
                return true;
            }
        };
        injectPayPal(service, payPalStub);

        int result = service.effettuaPagamento(100.0);

        assertEquals(1, result);
    }

    @Test
    void effettuaPagamento_quandoProcessPaymentFalse_ritorna0() {
        PayPal payPalStub = new PayPal("test@mail.com", "pwd") {
            @Override
            public boolean processPayment(double importo) {
                return false;
            }
        };
        injectPayPal(service, payPalStub);

        int result = service.effettuaPagamento(100.0);

        assertEquals(0, result);
    }

    @Test
    void effettuaPagamento_senzaInit_oPayPal_null_lanciaNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.effettuaPagamento(10.0));
    }
}