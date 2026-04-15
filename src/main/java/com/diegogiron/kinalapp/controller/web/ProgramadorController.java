package com.diegogiron.kinalapp.controller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/programador")
public class ProgramadorController {

    @GetMapping
    public String perfil(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
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