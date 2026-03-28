package com.diegogiron.kinalapp.controller;

import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.service.IProductoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar(){
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Producto>> listarPorEstado(@PathVariable int estado){
        List<Producto> productos = productoService.listarPorEstado(estado);
        if(productos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable int id){
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Producto producto){
        try{
            Producto nuevo = productoService.guardar(producto);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id){
        try{
            if(!productoService.existePorId(id)){
                return ResponseEntity.notFound().build();
            }
            productoService.eliminar(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Producto producto){
        try{
            if(!productoService.existePorId(id)){
                return ResponseEntity.notFound().build();
            }
            Producto actualizado = productoService.actualizar(id, producto);
            return ResponseEntity.ok(actualizado);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}