package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/usuarios")
public class UsuarioWebController {

    private final IUsuarioService usuarioService;

    public UsuarioWebController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private boolean tieneAcceso(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null &&
                (usuario.getRol().equals("ADMIN") || usuario.getRol().equals("VENDEDOR"));
    }

    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) Integer estado,
                         HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        if (estado != null) {
            model.addAttribute("usuarios", usuarioService.listarPorEstado(estado));
        } else {
            model.addAttribute("usuarios", usuarioService.listarTodos());
        }
        model.addAttribute("titulo", "Gestión de Usuarios");
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("titulo", "Nuevo Usuario");
        return "usuarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable long id, Model model,
                         RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("titulo", "Editar Usuario");
                    return "usuarios/formulario";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Usuario no encontrado");
                    return "redirect:/web/usuarios";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        try {
            // Si es edición y la contraseña está vacía, mantener la existente
            if (usuario.getCodigoUsuario() != 0 && (usuario.getPassword() == null || usuario.getPassword().isBlank())) {
                Usuario existente = usuarioService.buscarPorId(usuario.getCodigoUsuario()).orElse(null);
                if (existente != null) {
                    usuario.setPassword(existente.getPassword());
                }
            }
            usuarioService.guardar(usuario);
            ra.addFlashAttribute("success", "Usuario guardado exitosamente");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable long id,
                           RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        try {
            usuarioService.eliminar(id);
            ra.addFlashAttribute("success", "Usuario eliminado");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "No se pudo eliminar el usuario");
        }
        return "redirect:/web/usuarios";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable long id, Model model,
                      RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("titulo", "Detalle del Usuario");
                    return "usuarios/ver";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Usuario no encontrado");
                    return "redirect:/web/usuarios";
                });
    }
}