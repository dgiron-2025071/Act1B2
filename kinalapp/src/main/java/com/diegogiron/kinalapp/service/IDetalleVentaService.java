package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.DetalleVenta;
import java.util.List;

public interface IDetalleVentaService {

    List<DetalleVenta> listar();

    DetalleVenta guardar(DetalleVenta detalleVenta);

    DetalleVenta buscar(Long id);

    void eliminar(Long id);
}