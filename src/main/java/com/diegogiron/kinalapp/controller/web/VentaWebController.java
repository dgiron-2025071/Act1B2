package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.*;
import com.diegogiron.kinalapp.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/web/ventas")
public class VentaWebController {

    private static final Logger log = LoggerFactory.getLogger(VentaWebController.class);

    private final IVentaService ventaService;
    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;
    private final IProductoService productoService;
    private final IDetalleVentaService detalleVentaService;

    public VentaWebController(IVentaService ventaService, IClienteService clienteService,
                              IUsuarioService usuarioService, IProductoService productoService,
                              IDetalleVentaService detalleVentaService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.detalleVentaService = detalleVentaService;
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
        List<Venta> ventas = estado != null ? ventaService.findByEstado(estado) : ventaService.listarTodos();
        model.addAttribute("ventas", ventas);
        model.addAttribute("titulo", "Gestión de Ventas");
        return "ventas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        model.addAttribute("clientes", clienteService.listarPorEstado(1));
        model.addAttribute("usuarios", usuarioService.listarPorEstado(1));
        model.addAttribute("titulo", "Nueva Venta");
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String dpiCliente,
                          @RequestParam long codigoUsuario,
                          RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        try {
            Cliente cliente = clienteService.buscarPorDPI(dpiCliente)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            Usuario usuario = usuarioService.buscarPorId(codigoUsuario)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            Venta venta = new Venta();
            venta.setFechaVenta(LocalDate.now());
            venta.setTotal(BigDecimal.ZERO);
            venta.setEstado(1);
            venta.setCliente(cliente);
            venta.setUsuario(usuario);
            venta = ventaService.guardar(venta);

            ra.addFlashAttribute("success", "Venta creada. Agregue detalles.");
            return "redirect:/web/ventas/detalles/" + venta.getCodigoVenta();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/ventas/nuevo";
        }
    }

    @GetMapping("/detalles/{idVenta}")
    public String gestionarDetalles(@PathVariable long idVenta, Model model) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        Venta venta = ventaService.buscarPorId(idVenta).orElseThrow();
        List<DetalleVenta> detalles = detalleVentaService.listar().stream()
                .filter(d -> d.getVenta().getCodigoVenta() == idVenta).toList();
        model.addAttribute("venta", venta);
        model.addAttribute("detalles", detalles);
        model.addAttribute("productos", productoService.listarPorEstado(1));
        model.addAttribute("nuevoDetalle", new DetalleVenta());
        model.addAttribute("titulo", "Detalles de Venta #" + idVenta);
        return "ventas/detalle";
    }

    @PostMapping("/detalles/agregar")
    public String agregarDetalle(@RequestParam long idVenta, @RequestParam int idProducto,
                                 @RequestParam int cantidad, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        try {
            Venta venta = ventaService.buscarPorId(idVenta).orElseThrow();
            if (venta.getEstado() == 0) {
                ra.addFlashAttribute("error", "No se pueden agregar productos a una venta anulada");
                return "redirect:/web/ventas/detalles/" + idVenta;
            }
            Producto producto = productoService.buscarPorId(idProducto).orElseThrow();
            if (producto.getStock() < cantidad) {
                ra.addFlashAttribute("error", "Stock insuficiente. Disponible: " + producto.getStock());
                return "redirect:/web/ventas/detalles/" + idVenta;
            }
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
            detalle.setEstado(1);
            detalleVentaService.guardar(detalle);

            BigDecimal nuevoTotal = venta.getTotal().add(detalle.getSubtotal());
            venta.setTotal(nuevoTotal);
            ventaService.guardar(venta);

            producto.setStock(producto.getStock() - cantidad);
            productoService.guardar(producto);

            ra.addFlashAttribute("success", "Detalle agregado");
        } catch (Exception e) {
            log.error("Error al agregar detalle", e);
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/ventas/detalles/" + idVenta;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable long id, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        try {
            Venta venta = ventaService.buscarPorId(id).orElse(null);
            if (venta == null) {
                ra.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/web/ventas";
            }
            if (venta.getEstado() == 0) {
                ra.addFlashAttribute("error", "La venta ya está anulada");
                return "redirect:/web/ventas";
            }
            List<DetalleVenta> detalles = detalleVentaService.listar().stream()
                    .filter(d -> d.getVenta().getCodigoVenta() == id).toList();
            for (DetalleVenta detalle : detalles) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoService.guardar(producto);
            }
            venta.setEstado(0);
            ventaService.guardar(venta);
            ra.addFlashAttribute("success", "Venta #" + id + " anulada y stock restaurado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al anular la venta: " + e.getMessage());
        }
        return "redirect:/web/ventas";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable long id, Model model, RedirectAttributes ra) {
        if (getUsuarioAutenticado() == null) return "redirect:/auth/login";
        return ventaService.buscarPorId(id)
                .map(venta -> {
                    model.addAttribute("venta", venta);
                    return "ventas/ver";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Venta no encontrada");
                    return "redirect:/web/ventas";
                });
    }
}