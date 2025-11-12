package unit;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;
import com.example.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class ProductoServiceTest {


    @Mock
    private ProductoRepository repo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductoService service;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(service, "inventarioUrl", "http://inventario:8082/api/inventarios");
    }



    @Test
    void testCrearProducto() {

        Producto producto = new Producto();
        producto.setNombre("Laptop");
        producto.setPrecio(1200.0);

        Producto productoGuardado = new Producto();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Laptop");
        productoGuardado.setPrecio(1200.0);

        when(repo.save(any(Producto.class))).thenReturn(productoGuardado);

        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        Producto resultado = service.create(producto);

        assertNotNull(resultado);
        assertEquals("Laptop",resultado.getNombre());
        assertEquals(1L,resultado.getId());
        verify(repo,times(1)).save(any(Producto.class));
        verify(restTemplate,atMost(3)).postForEntity(anyString(),any(),eq(Void.class));
    }


    @Test
    void testCrearProducto_FallaInventarioDespuesDeReintentos(){

        Producto producto = new Producto();
        producto.setNombre("Teclado");
        producto.setPrecio(100.0);

        Producto productoGuardado = new Producto();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Teclado");
        productoGuardado.setPrecio(100.0);


        when(repo.save(any(Producto.class))).thenReturn(productoGuardado);

        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenThrow(new RuntimeException("Error al comunicar con el inventario"));

        ReflectionTestUtils.setField(service, "inventarioUrl", "http://inventario:8082/api/inventarios");


        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.create(producto);
        });

        assertEquals("No se puede comunicar con el inventario", ex.getMessage());

        verify(restTemplate, times(3)).postForEntity(anyString(), any(), eq(Void.class));
        verify(repo, times(1)).save(any(Producto.class));
    }

}
