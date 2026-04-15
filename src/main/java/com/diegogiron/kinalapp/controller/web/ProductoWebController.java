package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/productos")
public class ProductoWebController {

    private final IProductoService productoService;

    public ProductoWebController(IProductoService productoService) {
        this.productoService = productoService;
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

        model.addAttribute("productos", estado != null ?
                productoService.listarPorEstado(estado) : productoService.listarTodos());
        model.addAttribute("titulo", "Gestión de Productos");
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Nuevo Producto");
        return "productos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable int id, Model model,
                         RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        return productoService.buscarPorId(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    model.addAttribute("titulo", "Editar Producto");
                    return "productos/formulario";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Producto no encontrado");
                    return "redirect:/web/productos";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        try {
            productoService.guardar(producto);
            ra.addFlashAttribute("success", "Producto guardado exitosamente");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id,
                           RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        try {
            productoService.eliminar(id);
            ra.addFlashAttribute("success", "Producto eliminado");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "No se pudo eliminar el producto");
        }
        return "redirect:/web/productos";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable int id, Model model,
                      RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        return productoService.buscarPorId(id)
                .map(producto -> {
                    model.addAttribute("producto", producto);
                    model.addAttribute("titulo", "Detalle del Producto");
                    return "productos/ver";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Producto no encontrado");
                    return "redirect:/web/productos";
                });
    }
}