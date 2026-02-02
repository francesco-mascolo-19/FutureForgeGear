package unit;

import model.OrderManagement.ItemCartDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnitOrderService {

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService();
    }

    @Test
    void deserializeItems_conJsonValidi_ritornaListaConOggettiCorretti() {
        List<String> input = List.of(
                "{\"prodottoId\":1,\"quantity\":2}",
                "{\"prodottoId\":5,\"quantity\":10}"
        );

        List<ItemCartDTO> out = service.deserializeItems(input);

        assertNotNull(out);
        assertEquals(2, out.size());

        assertEquals(1, out.get(0).getProdottoId());
        assertEquals(2, out.get(0).getQuantity());

        assertEquals(5, out.get(1).getProdottoId());
        assertEquals(10, out.get(1).getQuantity());
    }

    @Test
    void deserializeItems_conListaVuota_ritornaListaVuota() {
        List<ItemCartDTO> out = service.deserializeItems(List.of());

        assertNotNull(out);
        assertTrue(out.isEmpty());
    }

    @Test
    void deserializeItems_conJsonNonValido_ritornaListaVuota() {
        List<String> input = List.of("{NON_JSON}");

        List<ItemCartDTO> out = service.deserializeItems(input);

        assertNotNull(out);
        assertTrue(out.isEmpty());
    }

    @Test
    void deserializeItems_conJsonNonValidoInMezzo_ritornaListaParziale() {
        List<String> input = List.of(
                "{\"prodottoId\":3,\"quantity\":1}",
                "{NON_JSON}",
                "{\"prodottoId\":7,\"quantity\":4}"
        );

        List<ItemCartDTO> out = service.deserializeItems(input);

        // il try/catch è fuori dal for → si ferma al primo errore
        assertNotNull(out);
        assertEquals(1, out.size());

        assertEquals(3, out.get(0).getProdottoId());
        assertEquals(1, out.get(0).getQuantity());
    }
}