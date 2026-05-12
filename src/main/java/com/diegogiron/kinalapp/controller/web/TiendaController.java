package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IProductoService;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/tienda")
public class TiendaController {

    private final IProductoService productoService;
    private final IUsuarioService usuarioService;

    public TiendaController(IProductoService productoService, IUsuarioService usuarioService) {
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarProductos(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        Usuario usuario = usuarioService.buscarPorUsername(principal.getName()).orElse(null);
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("titulo", "Tienda Kinal");
        model.addAttribute("productos", productoService.listarPorEstado(1));
        return "tienda/index";
    }

    @GetMapping("/producto/{id}")
    public String verProducto(@PathVariable int id, Model model, Principal principal) {
        if (principal == null) {
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