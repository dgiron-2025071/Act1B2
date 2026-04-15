package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.*;
import com.diegogiron.kinalapp.service.*;
import jakarta.servlet.http.HttpSession;
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

    private boolean tieneAcceso(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null && (usuario.getRol().equals("ADMIN") || usuario.getRol().equals("VENDEDOR"));
    }

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) Integer estado, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";
        List<Venta> ventas = estado != null ? ventaService.listarPorEstado(estado) : ventaService.listarTodos();
        model.addAttribute("ventas", ventas);
        model.addAttribute("titulo", "Gestión de Ventas");
        return "ventas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";
        model.addAttribute("venta", new Venta());
        model.addAttribute("clientes", clienteService.listarPorEstado(1));
        model.addAttribute("usuarios", usuarioService.listarPorEstado(1));
        model.addAttribute("titulo", "Nueva Venta");
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Venta venta, RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";
        try {
            venta.setFechaVenta(LocalDate.now());
            venta.setTotal(BigDecimal.ZERO);
            venta.setEstado(1);
            ventaService.guardar(venta);
            ra.addFlashAttribute("success", "Venta creada. Agregue detalles.");
            return "redirect:/web/ventas/detalles/" + venta.getCodigoVenta();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/ventas/nuevo";
        }
    }

    @GetMapping("/detalles/{idVenta}")
    public String gestionarDetalles(@PathVariable long idVenta, Model model, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";
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
                                 @RequestParam int cantidad, RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";
        try {
            Venta venta = ventaService.buscarPorId(idVenta).orElseThrow();
            Producto producto = productoService.buscarPorId(idProducto).orElseThrow();
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
            ra.addFlashAttribute("success", "Detalle agregado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/ventas/detalles/" + idVenta;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable long id, RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";
        try {
            ventaService.eliminar(id);
            ra.addFlashAttribute("success", "Venta anulada");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "Error al anular venta");
        }
        return "redirect:/web/ventas";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable long id, Model model, RedirectAttributes ra, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";
        return ventaService.buscarPorId(id)
                .map(venta -> { model.addAttribute("venta", venta); return "ventas/ver"; })
                .orElseGet(() -> { ra.addFlashAttribute("error", "Venta no encontrada"); return "redirect:/web/ventas"; });
    }
}