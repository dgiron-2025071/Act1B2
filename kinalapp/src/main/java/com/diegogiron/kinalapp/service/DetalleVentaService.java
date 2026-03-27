package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.DetalleVenta;
import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.entity.Venta;
import com.diegogiron.kinalapp.repository.DetalleVentaRepository;
import com.diegogiron.kinalapp.repository.ProductoRepository;
import com.diegogiron.kinalapp.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DetalleVentaService implements IDetalleVentaService {
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private VentaRepository ventaRepository;
    @Override
    public List<DetalleVenta> listar() {
        return detalleVentaRepository.findAll();
    }
    @Override
    public DetalleVenta guardar(DetalleVenta detalleVenta) {

        if (detalleVenta.getProducto() == null || detalleVenta.getVenta() == null) {
            throw new RuntimeException("Producto o Venta no pueden ser null");
        }
        Producto producto = productoRepository.findById(
                detalleVenta.getProducto().getCodigoProducto()
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Venta venta = ventaRepository.findById(
                detalleVenta.getVenta().getCodigoVenta()
        ).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        detalleVenta.setPrecioUnitario(producto.getPrecio());
        BigDecimal subtotal = producto.getPrecio()
                .multiply(BigDecimal.valueOf(detalleVenta.getCantidad()));
        detalleVenta.setSubtotal(subtotal);
        detalleVenta.setProducto(producto);
        detalleVenta.setVenta(venta);
        return detalleVentaRepository.save(detalleVenta);
    }
    @Override
    public DetalleVenta buscar(Long id) {
        return detalleVentaRepository.findById(id).orElse(null);
    }
    @Override
    public void eliminar(Long id) {
        detalleVentaRepository.deleteById(id);
    }
}