package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.DetalleVenta;
import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.entity.Venta;
import com.diegogiron.kinalapp.repository.DetalleVentaRepository;
import com.diegogiron.kinalapp.repository.ProductoRepository;
import com.diegogiron.kinalapp.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DetalleVentaService implements IDetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public DetalleVentaService(DetalleVentaRepository detalleVentaRepository,
                               ProductoRepository productoRepository,
                               VentaRepository ventaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> listar() {
        return detalleVentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> listarPorEstado(int estado) {
        List<DetalleVenta> detalles = detalleVentaRepository.findAll();
        return detalles.stream()
                .filter(dv -> dv.getEstado() != null && dv.getEstado() == estado)
                .toList();
    }

    @Override
    public DetalleVenta guardar(DetalleVenta detalleVenta) {

        if (detalleVenta.getVenta() == null || detalleVenta.getVenta().getCodigoVenta() == 0) {
            throw new IllegalArgumentException("El ID de venta es obligatorio");
        }
        if (detalleVenta.getProducto() == null || detalleVenta.getProducto().getCodigoProducto() == 0) {
            throw new IllegalArgumentException("El ID de producto es obligatorio");
        }
        if (detalleVenta.getCantidad() == null || detalleVenta.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Venta venta = ventaRepository.findById(detalleVenta.getVenta().getCodigoVenta())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " +
                        detalleVenta.getVenta().getCodigoVenta()));

        Producto producto = productoRepository.findById(detalleVenta.getProducto().getCodigoProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " +
                        detalleVenta.getProducto().getCodigoProducto()));

        if (producto.getStock() < detalleVenta.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + producto.getStock() +
                    ", solicitado: " + detalleVenta.getCantidad());
        }

        producto.setStock(producto.getStock() - detalleVenta.getCantidad());
        productoRepository.save(producto);

        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(producto);
        detalleVenta.setPrecioUnitario(producto.getPrecio());
        detalleVenta.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(detalleVenta.getCantidad())));

        if (detalleVenta.getEstado() == null || detalleVenta.getEstado() == 0) {
            detalleVenta.setEstado(1);
        }

        return detalleVentaRepository.save(detalleVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleVenta buscar(Long id) {
        return detalleVentaRepository.findById(id).orElse(null);
    }

    @Override
    public DetalleVenta actualizar(Long id, DetalleVenta detalleVenta) {
        if (!detalleVentaRepository.existsById(id)) {
            throw new RuntimeException("Detalle de venta no encontrado con ID: " + id);
        }

        if (detalleVenta.getVenta() == null || detalleVenta.getVenta().getCodigoVenta() == 0) {
            throw new IllegalArgumentException("El ID de venta es obligatorio");
        }
        if (detalleVenta.getProducto() == null || detalleVenta.getProducto().getCodigoProducto() == 0) {
            throw new IllegalArgumentException("El ID de producto es obligatorio");
        }
        if (detalleVenta.getCantidad() == null || detalleVenta.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        DetalleVenta detalleOriginal = detalleVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado con ID: " + id));
        Producto productoOriginal = productoRepository.findById(detalleOriginal.getProducto().getCodigoProducto())
                .orElseThrow(() -> new RuntimeException("Producto original no encontrado"));
        productoOriginal.setStock(productoOriginal.getStock() + detalleOriginal.getCantidad());
        productoRepository.save(productoOriginal);
        Producto productoNuevo = productoRepository.findById(detalleVenta.getProducto().getCodigoProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " +
                        detalleVenta.getProducto().getCodigoProducto()));

        if (productoNuevo.getStock() < detalleVenta.getCantidad()) {
            productoOriginal.setStock(productoOriginal.getStock() - detalleOriginal.getCantidad());
            productoRepository.save(productoOriginal);
            throw new IllegalArgumentException("Stock insuficiente para el nuevo producto. Disponible: " +
                    productoNuevo.getStock() + ", solicitado: " + detalleVenta.getCantidad());
        }

        productoNuevo.setStock(productoNuevo.getStock() - detalleVenta.getCantidad());
        productoRepository.save(productoNuevo);
        Venta venta = ventaRepository.findById(detalleVenta.getVenta().getCodigoVenta())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " +
                        detalleVenta.getVenta().getCodigoVenta()));
        detalleVenta.setCodigoDetalle(id);
        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(productoNuevo);
        detalleVenta.setPrecioUnitario(productoNuevo.getPrecio());
        detalleVenta.setSubtotal(productoNuevo.getPrecio().multiply(BigDecimal.valueOf(detalleVenta.getCantidad())));
        if (detalleVenta.getEstado() == null || detalleVenta.getEstado() == 0) {
            detalleVenta.setEstado(1);
        }
        return detalleVentaRepository.save(detalleVenta);
    }

    @Override
    public void eliminar(Long id) {
        if (!detalleVentaRepository.existsById(id)) {
            throw new RuntimeException("Detalle de venta no encontrado con ID: " + id);
        }
        DetalleVenta detalle = detalleVentaRepository.findById(id).get();
        Producto producto = productoRepository.findById(detalle.getProducto().getCodigoProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setStock(producto.getStock() + detalle.getCantidad());
        productoRepository.save(producto);
        detalleVentaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return detalleVentaRepository.existsById(id);
    }
}