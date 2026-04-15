package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Venta;
import com.diegogiron.kinalapp.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaService implements IVentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarTodos() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarPorEstado(int estado) {
        List<Venta> ventas = ventaRepository.findAll();
        return ventas.stream()
                .filter(v -> v.getEstado() == estado)
                .toList();
    }

    @Override
    public Venta guardar(Venta venta) {
        if (venta.getCliente() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (venta.getUsuario() == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (venta.getTotal() == null) {
            venta.setTotal(BigDecimal.ZERO);
        }
        if (venta.getEstado() == null || venta.getEstado() == 0) {
            venta.setEstado(1);
        }
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> buscarPorId(long id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta actualizar(long id, Venta venta) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con ID " + id);
        }
        venta.setCodigoVenta(id);
        return ventaRepository.save(venta);
    }

    @Override
    public void eliminar(long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con ID " + id);
        }
        ventaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(long id) {
        return ventaRepository.existsById(id);
    }
}