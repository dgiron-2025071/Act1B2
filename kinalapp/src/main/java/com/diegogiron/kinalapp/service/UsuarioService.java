package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        if(usuario.getEstado() == 0){
            usuario.setEstado(1);
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(int id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario actualizar(int id, Usuario usuario) {
        if(!usuarioRepository.existsById(id)){
            throw new RuntimeException("Usuario no encontrado con ID " + id);
        }
        usuario.setCodigoUsuario(id);
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(int id) {
        if(!usuarioRepository.existsById(id)){
            throw new RuntimeException("Usuario no encontrado con ID " + id);
        }
        usuarioRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(int id) {
        return usuarioRepository.existsById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorEstado(int estado) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .filter(u -> u.getEstado() == estado)
                .toList();
    }
    // VALIDACIONES
    private void validarUsuario(Usuario usuario){
        if(usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()){
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if(usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()){
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if(usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()){
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if(usuario.getRol() == null || usuario.getRol().trim().isEmpty()){
            throw new IllegalArgumentException("El rol es obligatorio");
        }
    }
}