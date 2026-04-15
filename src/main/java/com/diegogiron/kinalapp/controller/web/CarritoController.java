package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.service.IProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final IProductoService productoService;

    public CarritoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String verCarrito(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/auth/login";

        Map<Integer, Integer> carrito = (Map<Integer, Integer>) session.getAttribute("carrito");
        if (carrito == null) carrito = new HashMap<>();

        List<ItemCarrito> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            Optional<Producto> optProd = productoService.buscarPorId(entry.getKey());
            if (optProd.isPresent()) {
                Producto p = optProd.get();
                ItemCarrito item = new ItemCarrito();
                item.setProducto(p);
                item.setCantidad(entry.getValue());
                item.setSubtotal(p.getPrecio().multiply(BigDecimal.valueOf(entry.getValue())));
                items.add(item);
                total = total.add(item.getSubtotal());
            }
        }

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("titulo", "Mi Carrito");
        return "tienda/carrito";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam int idProducto, @RequestParam int cantidad, HttpSession session) {
        Map<Integer, Integer> carrito = (Map<Integer, Integer>) session.getAttribute("carrito");
        if (carrito == null) carrito = new HashMap<>();
        carrito.merge(idProducto, cantidad, Integer::sum);
        session.setAttribute("carrito", carrito);
        return "redirect:/tienda";
    }

    @GetMapping("/eliminar/{idProducto}")
    public String eliminar(@PathVariable int idProducto, HttpSession session) {
        Map<Integer, Integer> carrito = (Map<Integer, Integer>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.remove(idProducto);
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Map<String, String> params, HttpSession session) {
        Map<Integer, Integer> carrito = new HashMap<>();
        params.forEach((key, value) -> {
            if (key.startsWith("cant_")) {
                int id = Integer.parseInt(key.substring(5));
                int cant = Integer.parseInt(value);
                if (cant > 0) carrito.put(id, cant);
            }
        });
        session.setAttribute("carrito", carrito);
        return "redirect:/carrito";
    }

    // Clase interna para vista
    public static class ItemCarrito {
        private Producto producto;
        private int cantidad;
        private BigDecimal subtotal;
        // getters y setters
        public Producto getProducto() { return producto; }
        public void setProducto(Producto producto) { this.producto = producto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
}