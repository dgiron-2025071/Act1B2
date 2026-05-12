package com.diegogiron.kinalapp.repository;

import com.diegogiron.kinalapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // <-- IMPORTANTE

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}