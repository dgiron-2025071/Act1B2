package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(IUsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioService.buscarPorUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@kinal.edu.gt");
            admin.setRol("ADMIN");
            admin.setEstado(1);
            usuarioService.guardar(admin);
            System.out.println("Usuario administrador creado: admin / admin123");
        }

        if (usuarioService.buscarPorUsername("vendedor").isEmpty()) {
            Usuario vendedor = new Usuario();
            vendedor.setUsername("vendedor");
            vendedor.setPassword(passwordEncoder.encode("vendedor123"));
            vendedor.setEmail("vendedor@kinal.edu.gt");
            vendedor.setRol("VENDEDOR");
            vendedor.setEstado(1);
            usuarioService.guardar(vendedor);
            System.out.println(" Usuario vendedor creado: vendedor / vendedor123");
        }

        if (usuarioService.buscarPorUsername("user").isEmpty()) {
            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@kinal.edu.gt");
            user.setRol("USER");
            user.setEstado(1);
            usuarioService.guardar(user);
            System.out.println("Usuario estándar creado: user / user123");
        }
    }
}