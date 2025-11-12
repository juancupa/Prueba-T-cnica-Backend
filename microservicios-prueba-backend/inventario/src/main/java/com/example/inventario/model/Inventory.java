package com.example.inventario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="producto_id", nullable=false)
    private Long productoId;

    @Column(name="cantidad", nullable=false)
    private Integer cantidad;

    public Inventory() {}
    public Inventory(Long productoId, Integer cantidad) { this.productoId = productoId; this.cantidad = cantidad; }

    public Inventory(Long id,  Long productoId,Integer cantidad) {
        this.cantidad = cantidad;
        this.id = id;
        this.productoId = productoId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
