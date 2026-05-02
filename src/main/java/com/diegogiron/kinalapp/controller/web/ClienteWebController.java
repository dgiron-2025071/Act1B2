package com.diegogiron.kinalapp.controller.web;

import com.diegogiron.kinalapp.entity.Cliente;
import com.diegogiron.kinalapp.entity.Usuario;
import com.diegogiron.kinalapp.service.IClienteService;
import com.diegogiron.kinalapp.service.IUsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/web/clientes")
public class ClienteWebController {

    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;

    public ClienteWebController(IClienteService clienteService, IUsuarioService usuarioService) {
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) Integer estado, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        if (estado != null) {
            model.addAttribute("clientes", clienteService.listarPorEstado(estado));
        } else {
            model.addAttribute("clientes", clienteService.listarTodos());
        }
        model.addAttribute("titulo", "Gestión de Clientes");
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        return "clientes/formulario";
    }

    @GetMapping("/editar/{dpi}")
    public String editar(@PathVariable String dpi, Model model, RedirectAttributes ra, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        return clienteService.buscarPorDPI(dpi)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("titulo", "Editar Cliente");
                    return "clientes/formulario";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Cliente no encontrado");
                    return "redirect:/web/clientes";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes ra, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        try {
            clienteService.guardar(cliente);
            ra.addFlashAttribute("success", "Cliente guardado exitosamente");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/clientes";
    }

    @GetMapping("/eliminar/{dpi}")
    public String eliminar(@PathVariable String dpi, RedirectAttributes ra, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        try {
            clienteService.eliminar(dpi);
            ra.addFlashAttribute("success", "Cliente eliminado");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "No se pudo eliminar el cliente");
        }
        return "redirect:/web/clientes";
    }

    @GetMapping("/ver/{dpi}")
    public String ver(@PathVariable String dpi, Model model, RedirectAttributes ra, Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        return clienteService.buscarPorDPI(dpi)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("titulo", "Detalle del Cliente");
                    return "clientes/ver";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Cliente no encontrado");
                    return "redirect:/web/clientes";
                });
    }
}