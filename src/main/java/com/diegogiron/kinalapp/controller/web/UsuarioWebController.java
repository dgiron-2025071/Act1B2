package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String username = auth.getName();
        return usuarioService.buscarPorUsername(username).orElse(null);
    }

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) Integer estado) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        if (estado != null) {
            model.addAttribute("usuarios", usuarioService.listarPorEstado(estado));
        } else {
            model.addAttribute("usuarios", usuarioService.listarTodos());
        }
        model.addAttribute("titulo", "Gestión de Usuarios");
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("titulo", "Nuevo Usuario");
        return "usuarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable long id, Model model, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
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
    public String guardar(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        try {
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
    public String eliminar(@PathVariable long id, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        try {
            usuarioService.eliminar(id);
            ra.addFlashAttribute("success", "Usuario eliminado");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "No se pudo eliminar el usuario");
        }
        return "redirect:/web/usuarios";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable long id, Model model, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
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