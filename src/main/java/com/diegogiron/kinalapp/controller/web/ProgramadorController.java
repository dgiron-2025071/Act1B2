package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/programador")
public class ProgramadorController {

    private final IUsuarioService usuarioService;

    public ProgramadorController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String username = auth.getName();
        return usuarioService.buscarPorUsername(username).orElse(null);
    }

    @GetMapping
    public String perfil(Model model) {
        if (getUsuarioAutenticado() == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("titulo", "Perfil del Programador");
        model.addAttribute("nombre", "Diego Girón");
        model.addAttribute("carnet", "2025071");
        model.addAttribute("correo", "dgiron-2025071@kinal.edu.gt");
        model.addAttribute("github", "https://github.com/dgiron-2025071");
        model.addAttribute("descripcion", "Desarrollador en aprendizaje, apasionado por crear soluciones elegantes. Estudiante de Perito Informática.");
        model.addAttribute("habilidades", "Java, Spring Boot, Thymeleaf, PostgreSQL, HTML/CSS, JavaScript");
        return "perfil-programador";
    }
}