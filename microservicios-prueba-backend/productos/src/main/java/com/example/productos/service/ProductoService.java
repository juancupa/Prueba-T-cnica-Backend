package com.example.productos.service;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {
    private final ProductoRepository repo;
    private  final RestTemplate restTemplate;

    @Value("${products.base-url}")
    private String inventarioUrl;

    public ProductoService(ProductoRepository repo, RestTemplate restTemplate) {
        this.repo = repo;
        this.restTemplate=restTemplate;

    }

    public Producto create(Producto p) {

        Producto guarda = repo.save(p);

        int reintentos =3;
        for (int i=1; i<=reintentos;i++){
            try {

                restTemplate.postForEntity(inventarioUrl
                        , Map.of("productoId",guarda.getId(),"cantidad",10),
                        Void.class);

            }catch (Exception e){
                if (i==reintentos){
                    System.out.println("Error comunicando con inventario tras" + reintentos + "intentos: " +e.getMessage());
                    throw new RuntimeException("No se puede comunicar con el inventario");
                }
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException ignore ){}
            }
        }


        return guarda;


    }

    public Optional<Producto> findById(Long id) {
        return repo.findById(id);
    }
    public Optional<Producto> update(Long id, Producto updateProducto) {
        return repo.findById(id).map(p -> {
            p.setNombre(updateProducto.getNombre());
            p.setPrecio(updateProducto.getPrecio());
            return repo.save(p);
        });
    }
    public boolean delete(Long id) {
        return repo.findById(id)
                   .map(p -> {
                        repo.delete(p);
                        return true;
        }).orElse(false);
    }
    public List<Producto> listAll() { return repo.findAll(); }



    public List<Producto> findAll() {
        return repo.findAll();
    }


}
