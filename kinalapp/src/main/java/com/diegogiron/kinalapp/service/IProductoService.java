package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoService {

    List<Producto> listarTodos();
    List<Producto> listarPorEstado(int estado);

    Producto guardar(Producto producto);
    Optional<Producto> buscarPorId(int id);
    Producto actualizar(int id, Producto producto);

    void eliminar(int id);

    boolean existePorId(int id);
}