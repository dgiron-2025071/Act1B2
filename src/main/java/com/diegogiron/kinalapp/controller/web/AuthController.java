package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(IUsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model,
                            HttpSession session) {
        if (session.getAttribute("usuario") != null) {
            return "redirect:/tienda";
        }

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión correctamente");
        }

        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model, HttpSession session) {
        if (session.getAttribute("usuario") != null) {
            return "redirect:/tienda";
        }
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }
    @GetMapping("/error/403")
    public String accesoDenegado() {
        return "error/403";
    }
    @PostMapping("/registro")
    public String registroProcesar(@ModelAttribute Usuario usuario,
                                   RedirectAttributes ra) {
        try {
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                ra.addFlashAttribute("error", "El nombre de usuario es obligatorio");
                return "redirect:/auth/registro";
            }
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                ra.addFlashAttribute("error", "La contraseña es obligatoria");
                return "redirect:/auth/registro";
            }
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                ra.addFlashAttribute("error", "El correo electrónico es obligatorio");
                return "redirect:/auth/registro";
            }
            if (usuarioService.buscarPorUsername(usuario.getUsername()).isPresent()) {
                ra.addFlashAttribute("error", "El nombre de usuario ya está en uso");
                return "redirect:/auth/registro";
            }

            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setRol("USER");
            usuario.setEstado(1);
            usuarioService.guardar(usuario);

            ra.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión");
            return "redirect:/auth/login";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/auth/registro";
        }
    }
}