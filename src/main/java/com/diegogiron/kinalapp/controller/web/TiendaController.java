package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    private final IProductoService productoService;

    public TiendaController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listarProductos(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("titulo", "Tienda Kinal");
        model.addAttribute("productos", productoService.listarPorEstado(1));
        return "tienda/index";
    }

    @GetMapping("/producto/{id}")
    public String verProducto(@PathVariable int id, Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/auth/login";
        }
        Producto producto = productoService.buscarPorId(id).orElse(null);
        if (producto == null) {
            return "redirect:/tienda";
        }
        model.addAttribute("producto", producto);
        model.addAttribute("titulo", producto.getNombreProducto());
        return "tienda/producto-detalle";
    }
}