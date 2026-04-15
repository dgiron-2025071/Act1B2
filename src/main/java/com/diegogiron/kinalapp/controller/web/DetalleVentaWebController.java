package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.DetalleVenta;
import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IDetalleVentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/detalle-ventas")
public class DetalleVentaWebController {

    private final IDetalleVentaService detalleVentaService;

    public DetalleVentaWebController(IDetalleVentaService detalleVentaService) {
        this.detalleVentaService = detalleVentaService;
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
            model.addAttribute("detalles", detalleVentaService.listarPorEstado(estado));
        } else {
            model.addAttribute("detalles", detalleVentaService.listar());
        }
        model.addAttribute("titulo", "Detalles de Ventas");
        return "detalle-ventas/lista";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model,
                      RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

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