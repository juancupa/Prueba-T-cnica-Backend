package unit;

import com.example.inventario.model.Inventory;
import com.example.inventario.repository.InventoryRepository;
import com.example.inventario.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InventoryServiceTest {

    @Mock
    private InventoryRepository repo;

    @InjectMocks
    private InventoryService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarInventario(){

        Inventory inv = new Inventory( 1L, 10);
        when(repo.save(any(Inventory.class))).thenReturn(inv);

        Inventory resul = service.save(inv);

        assertNotNull(resul);
        assertEquals(10,resul.getCantidad());
        verify(repo,times(1)).save(inv);
    }

    @Test
    void testActualizarCantidadReduceCorrectamente(){
        Inventory existente = new Inventory(1L, 1L, 10);
        when(repo.findByProductoId(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Inventory.class))).thenReturn(existente);

        Map<String, Object> resultado = service.actualizarCantidad(1L, 3);

        assertEquals(7, resultado.get("cantidadRestande"));
        assertEquals("Inventario actualziado correctamente", resultado.get("message"));


    }


}
