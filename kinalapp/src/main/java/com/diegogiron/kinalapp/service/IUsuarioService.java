package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    List<Usuario> listarTodos();

    List<Usuario> listarPorEstado(int estado);

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(int id);

    Usuario actualizar(int id, Usuario usuario);

    void eliminar(int id);

    boolean existePorId(int id);
}