package com.diegogiron.kinalapp.controller;

import com.diegogiron.kinalapp.entity.DetalleVenta;
import com.diegogiron.kinalapp.service.IDetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private IDetalleVentaService detalleVentaService;

    @GetMapping
    public List<DetalleVenta> listar() {
        return detalleVentaService.listar();
    }

    @PostMapping
    public DetalleVenta guardar(@RequestBody DetalleVenta detalleVenta) {
        return detalleVentaService.guardar(detalleVenta);
    }

    @GetMapping("/{id}")
    public DetalleVenta buscar(@PathVariable Long id) {
        return detalleVentaService.buscar(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        detalleVentaService.eliminar(id);
    }
}