package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final IUsuarioService usuarioService;

    public AuthController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            return usuario.getRol().equals("CLIENTE") ? "redirect:/tienda" : "redirect:/web/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        Optional<Usuario> optUsuario = usuarioService.buscarPorUsername(username);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            if (usuario.getPassword().equals(password) && usuario.getEstado() == 1) {
                session.setAttribute("usuario", usuario);
                return usuario.getRol().equals("CLIENTE") ? "redirect:/tienda" : "redirect:/web/dashboard";
            }
        }
        ra.addFlashAttribute("error", "Credenciales inválidas o usuario inactivo");
        return "redirect:/auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        try {
            usuario.setRol("CLIENTE");
            usuario.setEstado(1);
            usuarioService.guardar(usuario);
            ra.addFlashAttribute("success", "¡Registro exitoso! Ahora inicia sesión.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/registro";
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}