package com.example.inventario.controller;

import com.example.inventario.model.Inventory;
import com.example.inventario.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/inventarios")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = service; }



    @GetMapping("/{productoId}")
    public ResponseEntity<?> getInventarioProducto(@PathVariable Long productoId) {
        Map<String, Object> resultado = service.getProductoInventario(productoId);
        if (resultado.containsKey("error")) {
            return ResponseEntity.status(404).body(resultado);
        }
        return ResponseEntity.ok(Map.of("data", resultado));
    }



    @PutMapping("/{productoId}/reducir")
    public ResponseEntity<?> reducirInventario(@PathVariable Long productoId, @RequestParam int cantidad){
        Map<String, Object> resultado = service.actualizarCantidad(productoId,cantidad);

        if (resultado.containsKey("error")){
            return ResponseEntity.status(400).body(resultado);
        }
        return ResponseEntity.ok(Map.of("data",resultado));
    }



    @PostMapping
    public ResponseEntity<?> crearInventario(@RequestBody Map<String, Object> body){
        Long productoId = Long.valueOf(body.get("productoId").toString());
        int cantidad = Integer.parseInt(body.get("cantidad").toString());
        Inventory inventory = new Inventory();
        inventory.setProductoId(productoId);;
        inventory.setCantidad(cantidad);

        Inventory guardar = service.save( inventory);
        return ResponseEntity.status(201).body(Map.of("data",guardar));
    }
}
