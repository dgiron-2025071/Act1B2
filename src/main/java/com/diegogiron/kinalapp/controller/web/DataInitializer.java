package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IUsuarioService usuarioService;

    public DataInitializer(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existe al menos un usuario con rol ADMIN
        boolean adminExiste = usuarioService.listarTodos().stream()
                .anyMatch(u -> "ADMIN".equals(u.getRol()));

        if (!adminExiste) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin123");   // Contraseña en texto plano (para entorno académico)
            admin.setEmail("admin@kinal.edu.gt");
            admin.setRol("ADMIN");
            admin.setEstado(1);

            usuarioService.guardar(admin);
            System.out.println(" Usuario administrador creado automáticamente: admin / admin123");
        } else {
            System.out.println("ℹ Ya existe un administrador en la base de datos.");
        }
    }
}