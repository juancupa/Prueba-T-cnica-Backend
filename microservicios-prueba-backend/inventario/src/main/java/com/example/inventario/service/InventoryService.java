package com.example.inventario.service;

import com.example.inventario.model.Inventory;
import com.example.inventario.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {
    private final InventoryRepository repo;
    private final RestTemplate rest;
    private final String productsBaseUrl;
    private final String productsApiKey;

    public InventoryService(InventoryRepository repo, RestTemplate rest,
                            @Value("${products.base-url}") String productsBaseUrl,
                            @Value("${products.api-key}") String productsApiKey) {
        this.repo = repo; this.rest = rest;
        this.productsBaseUrl = productsBaseUrl; this.productsApiKey = productsApiKey;
    }

    public Inventory getOrCreate(Long productoId) {
        return repo.findByProductoId(productoId).orElseGet(() -> repo.save(new Inventory(productoId, 0)));
    }

    public Optional<Inventory> findByProductoId(Long productoId){
        return repo.findByProductoId(productoId);
    }

    public Map<String,Object> obtenerProductos(Long productoId) {
            String url = productsBaseUrl + "/" + productoId;
            try {
                var headers = new org.springframework.http.HttpHeaders();
                headers.set("x-api-key", productsApiKey);
                var entity = new org.springframework.http.HttpEntity<>(headers);
                var resp = rest.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);
                return resp.getBody();
            } catch (HttpClientErrorException.NotFound ex) {
                throw new RuntimeException("Producto no encntrado");
            } catch (Exception ex) {
                throw new RuntimeException("Error al buscar el  producto: " + ex.getMessage(), ex);
            }
    }


    public Map<String,Object> getProductoInventario(Long productoId){

        Map<String,Object> producto = obtenerProductos(productoId);
        Optional<Inventory> inventario = repo.findByProductoId(productoId);

        if (producto == null || producto.isEmpty()){
            return Map.of("error","Producto no encontrado");
        }
        Object productoData = producto.get("data");
        return Map.of(
                "prodcuto",producto,
                "inventario",inventario.orElse(null)
        );

    }

    public Map<String, Object> actualizarCantidad(Long productoId, int cantidadVendida){
        Optional<Inventory> opcional = repo.findByProductoId(productoId);

        if(opcional.isEmpty()){
            return Map.of("error","Inventario no encontrado para el producto ID"+ productoId);
        }
        Inventory inventory= opcional.get();

        int cantidad = inventory.getCantidad() - cantidadVendida;

        if(cantidad <0){
            return Map.of("error","Stock insuficientes para el producto "+productoId);
        }
        inventory.setCantidad(cantidad);
        repo.save(inventory);
        System.out.printf("Inventario actualziado: producto %d, cantidad restante: %d%n",productoId,cantidad);

        return Map.of(
                "message","Inventario actualziado correctamente",
                "productoId",productoId,
                "cantidadRestande",cantidad
        );
    }

    public Inventory save (Inventory inventory){

        return repo.save(inventory);
    }
}
