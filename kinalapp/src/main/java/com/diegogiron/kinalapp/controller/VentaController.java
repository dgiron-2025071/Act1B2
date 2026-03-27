package com.diegogiron.kinalapp.controller;

import com.diegogiron.kinalapp.entity.Venta;
import com.diegogiron.kinalapp.service.IVentaService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final IVentaService ventaService;

    public VentaController(IVentaService ventaService) {
        this.ventaService = ventaService;
    }
    @GetMapping
    public ResponseEntity<List<Venta>> listar(){
        return ResponseEntity.ok(ventaService.listarTodos());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscar(@PathVariable long id){
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Venta venta){
        try{
            Venta nueva = ventaService.guardar(venta);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id){
        if(!ventaService.existePorId(id)){
            return ResponseEntity.notFound().build();
        }
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable long id, @RequestBody Venta venta){
        try{
            if(!ventaService.existePorId(id)){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ventaService.actualizar(id, venta));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}