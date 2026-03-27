package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Venta;

import java.util.List;
import java.util.Optional;

public interface IVentaService {
    List<Venta> listarTodos();
    Venta guardar(Venta venta);
    Optional<Venta> buscarPorId(long id);
    Venta actualizar(long id, Venta venta);
    void eliminar(long id);
    boolean existePorId(long id);
}