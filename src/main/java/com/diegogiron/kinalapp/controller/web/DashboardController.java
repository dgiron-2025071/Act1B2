package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/web/dashboard")
public class DashboardController {

    private final IClienteService clienteService;
    private final IProductoService productoService;
    private final IVentaService ventaService;
    private final IUsuarioService usuarioService;

    public DashboardController(IClienteService clienteService, IProductoService productoService,
                               IVentaService ventaService, IUsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
    }

    private boolean tieneAcceso(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return usuario != null && (usuario.getRol().equals("ADMIN") || usuario.getRol().equals("VENDEDOR"));
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        if (!tieneAcceso(session)) return "redirect:/auth/login";

        model.addAttribute("totalClientes", clienteService.listarPorEstado(1).size());
        model.addAttribute("totalProductos", productoService.listarPorEstado(1).size());
        model.addAttribute("totalUsuarios", usuarioService.listarPorEstado(1).size());

        long ventasHoy = ventaService.listarTodos().stream()
                .filter(v -> v.getFechaVenta() != null && v.getFechaVenta().equals(LocalDate.now()))
                .count();
        model.addAttribute("ventasHoy", ventasHoy);

        BigDecimal totalMes = ventaService.listarTodos().stream()
                .filter(v -> v.getFechaVenta() != null &&
                        v.getFechaVenta().getMonthValue() == LocalDate.now().getMonthValue() &&
                        v.getFechaVenta().getYear() == LocalDate.now().getYear())
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalVentasMes", totalMes);

        model.addAttribute("titulo", "Dashboard");
        return "dashboard";
    }
}