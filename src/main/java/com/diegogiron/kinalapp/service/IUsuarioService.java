package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Usuario;
import java.util.List;
import java.util.Optional; // <-- IMPORTANTE

public interface IUsuarioService {
    List<Usuario> listarTodos();
    List<Usuario> listarPorEstado(int estado);
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(long id);
    Usuario actualizar(long id, Usuario usuario);
    void eliminar(long id);
    boolean existePorId(long id);
    Optional<Usuario> buscarPorUsername(String username);
}