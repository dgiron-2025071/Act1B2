package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.DetalleVenta;
import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IDetalleVentaService;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/detalle-ventas")
public class DetalleVentaWebController {

    private final IDetalleVentaService detalleVentaService;
    private final IUsuarioService usuarioService;

    public DetalleVentaWebController(IDetalleVentaService detalleVentaService, IUsuarioService usuarioService) {
        this.detalleVentaService = detalleVentaService;
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
            model.addAttribute("detalles", detalleVentaService.listarPorEstado(estado));
        } else {
            model.addAttribute("detalles", detalleVentaService.listar());
        }
        model.addAttribute("titulo", "Detalles de Ventas");
        return "detalle-ventas/lista";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        DetalleVenta detalle = detalleVentaService.buscar(id);
        if (detalle == null) {
            ra.addFlashAttribute("error", "Detalle no encontrado");
            return "redirect:/web/detalle-ventas";
        }
        model.addAttribute("detalle", detalle);
        model.addAttribute("titulo", "Detalle de Venta");
        return "detalle-ventas/ver";
    }
}