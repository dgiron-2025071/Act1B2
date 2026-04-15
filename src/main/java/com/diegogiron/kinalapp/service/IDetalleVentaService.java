package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.DetalleVenta;
import java.util.List;

public interface IDetalleVentaService {
    List<DetalleVenta> listar();
    List<DetalleVenta> listarPorEstado(int estado);
    DetalleVenta guardar(DetalleVenta detalleVenta);
    DetalleVenta buscar(Long id);
    DetalleVenta actualizar(Long id, DetalleVenta detalleVenta);
    void eliminar(Long id);
    boolean existePorId(Long id);
}