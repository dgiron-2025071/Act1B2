package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.*;
import com.diegogiron.kinalapp.service.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private static final Logger log = LoggerFactory.getLogger(CarritoController.class);

    private final IProductoService productoService;
    private final IVentaService ventaService;
    private final IDetalleVentaService detalleVentaService;
    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;

    public CarritoController(IProductoService productoService,
                             IVentaService ventaService,
                             IDetalleVentaService detalleVentaService,
                             IClienteService clienteService,
                             IUsuarioService usuarioService) {
        this.productoService = productoService;
        this.ventaService = ventaService;
        this.detalleVentaService = detalleVentaService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String verCarrito(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        Map<Integer, Integer> carrito = getCarrito(session);
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
            } else {
                carrito.remove(entry.getKey());
            }
        }
        session.setAttribute("carrito", carrito);

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("titulo", "Mi Carrito");
        return "tienda/carrito";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam int idProducto,
                          @RequestParam int cantidad,
                          HttpSession session,
                          RedirectAttributes ra) {
        Producto producto = productoService.buscarPorId(idProducto).orElse(null);
        if (producto == null || producto.getEstado() != 1) {
            ra.addFlashAttribute("error", "Producto no disponible");
            return "redirect:/tienda";
        }

        if (producto.getStock() < cantidad) {
            ra.addFlashAttribute("error", "Stock insuficiente. Disponible: " + producto.getStock());
            return "redirect:/tienda";
        }

        Map<Integer, Integer> carrito = getCarrito(session);
        carrito.merge(idProducto, cantidad, Integer::sum);
        session.setAttribute("carrito", carrito);

        ra.addFlashAttribute("success", "Producto agregado al carrito");
        return "redirect:/tienda";
    }

    @GetMapping("/eliminar/{idProducto}")
    public String eliminar(@PathVariable int idProducto, HttpSession session) {
        Map<Integer, Integer> carrito = getCarrito(session);
        carrito.remove(idProducto);
        session.setAttribute("carrito", carrito);
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

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/auth/login";

        Map<Integer, Integer> carrito = getCarrito(session);
        if (carrito.isEmpty()) {
            return "redirect:/carrito";
        }

        List<Cliente> clientes = clienteService.listarPorEstado(1);
        if (clientes.isEmpty()) {
            model.addAttribute("error", "No hay clientes registrados. Contacte al administrador.");
            return "redirect:/carrito";
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("titulo", "Finalizar Compra");
        return "tienda/checkout";
    }

    @PostMapping("/finalizar")
    public String finalizarCompra(@RequestParam(required = false) String dpiCliente,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        Map<Integer, Integer> carrito = getCarrito(session);
        if (carrito.isEmpty()) {
            ra.addFlashAttribute("error", "❌ El carrito está vacío");
            return "redirect:/carrito";
        }

        // Obtener cliente
        Cliente cliente = null;
        if (dpiCliente != null && !dpiCliente.isEmpty()) {
            cliente = clienteService.buscarPorDPI(dpiCliente).orElse(null);
        } else {
            List<Cliente> clientes = clienteService.listarPorEstado(1);
            if (!clientes.isEmpty()) {
                cliente = clientes.get(0);
            }
        }

        if (cliente == null) {
            ra.addFlashAttribute("error", "No se pudo determinar el cliente para la venta");
            return "redirect:/carrito/checkout";
        }

        try {
            log.info("Iniciando finalización de compra para usuario: {}", usuario.getUsername());

            // Crear venta
            Venta venta = new Venta();
            venta.setFechaVenta(LocalDate.now());
            venta.setTotal(BigDecimal.ZERO);
            venta.setEstado(1);
            venta.setCliente(cliente);
            venta.setUsuario(usuario);
            venta = ventaService.guardar(venta);
            log.info("Venta creada con ID: {}", venta.getCodigoVenta());

            BigDecimal totalVenta = BigDecimal.ZERO;

            for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
                Producto producto = productoService.buscarPorId(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + entry.getKey()));

                int cantidad = entry.getValue();
                if (producto.getStock() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombreProducto());
                }

                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(producto.getPrecio());
                detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
                detalle.setEstado(1);
                detalleVentaService.guardar(detalle);

                totalVenta = totalVenta.add(detalle.getSubtotal());

                // Descontar stock
                producto.setStock(producto.getStock() - cantidad);
                productoService.guardar(producto);
                log.info("Producto {} stock reducido a {}", producto.getNombreProducto(), producto.getStock());
            }

            venta.setTotal(totalVenta);
            ventaService.guardar(venta);

            // Limpiar carrito
            session.removeAttribute("carrito");

            log.info("Compra finalizada exitosamente. Total: Q {}", totalVenta);
            ra.addFlashAttribute("success",
                    " ¡Compra realizada con éxito! Pago contra entrega. Total: Q " + totalVenta);
            return "redirect:/tienda";

        } catch (Exception e) {
            log.error("Error al finalizar compra", e);
            ra.addFlashAttribute("error", " Error al procesar la compra: " + e.getMessage());
            return "redirect:/carrito";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getCarrito(HttpSession session) {
        Map<Integer, Integer> carrito = (Map<Integer, Integer>) session.getAttribute("carrito");
        return carrito != null ? carrito : new HashMap<>();
    }

    // Clase interna
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