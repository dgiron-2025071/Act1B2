package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // <-- IMPORTANTE

@Service
@Transactional
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        validarUsuario(usuario);
        if (usuario.getEstado() == null || usuario.getEstado() == 0) {
            usuario.setEstado(1);
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario actualizar(long id, Usuario usuario) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID " + id);
        }
        usuario.setCodigoUsuario(id);
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(long id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorEstado(int estado) {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getEstado() == estado)
                .toList();
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        // Si el repositorio tiene el método findByUsername, úsalo:
        // return usuarioRepository.findByUsername(username);
        // Si no, usa el filtro manual:
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
    }
}