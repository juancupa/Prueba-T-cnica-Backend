package com.example.productos.controller;

import com.example.productos.model.Producto;
import com.example.productos.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService service;
    public ProductoController(ProductoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Map<String,Object>> create(@RequestBody Map<String, Object> producto) {
        Producto prod = mapToProducto(producto);
        Producto guardar = service.create(prod);


        return ResponseEntity.status(201).body(Map.of("data",toJsonApiData(guardar)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

        Producto producto = new Producto();
        producto.setNombre((String) attributes.get("nombre"));
        producto.setPrecio(Double.valueOf(attributes.get("precio").toString()));

        return service.update(id, producto)
                .map(p -> {
                    Map<String, Object> body = Map.of("data", (Object) toJsonApiData(p));
                    return ResponseEntity.ok(body);
                })
                .orElseGet(() -> ResponseEntity.status(404).body(jsonApiError(
                        "404", "Not Found", "Producto no encontrado"
                )));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean eliminado = service.delete(id);
        if (eliminado) {
            return ResponseEntity.ok(
                    Map.of("data", Map.of(
                            "message", "Producto eliminado correctamente",
                            "status", "200"
                    ))
            );
        } else {
            return ResponseEntity.status(404).body(
                    jsonApiError("404", "Not Found", "Producto no encontrado")
            );
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        var productos = service.findAll();

        if (productos.isEmpty()){
            return ResponseEntity.status(404).body(jsonApiError(
                    "404","Not Found","No se encontro producto"
            ));
        }
        var dataList =  productos.stream()
                .map(this::toJsonApiData).toList();

        return  ResponseEntity.ok(Map.of("data",dataList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> getId(@PathVariable Long id) {
        return service.findById(id)
                .map(p -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("data", toJsonApiData(p));
                    return ResponseEntity.ok(body);
                })
                .orElse(ResponseEntity.status(404).body(jsonApiError(
                        "404", "Not Found", "Producto no encontrado"
                )));
    }


    // --- Utilidades JSON:API ---
    private Map<String, Object> toJsonApiData(Producto p) {
        return Map.of(
                "type", "productos",
                "id", p.getId().toString(),
                "attributes", Map.of(
                        "nombre", p.getNombre(),
                        "precio", p.getPrecio()
                )
        );
    }

    private Map<String, Object> jsonApiError(String status, String title, String detail) {
        return Map.of(
                "errors", List.of(Map.of(
                        "status", status,
                        "title", title,
                        "detail", detail
                ))
        );
    }

    private Producto mapToProducto(Map<String, Object> request) {
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        Producto p = new Producto();
        p.setNombre((String) attributes.get("nombre"));
        p.setPrecio(Double.valueOf(attributes.get("precio").toString()));
        return p;
    }
}
