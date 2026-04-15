package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Producto;
import com.diegogiron.kinalapp.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService implements IProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto guardar(Producto producto) {
        validarProducto(producto);
        if(producto.getEstado() == 0){
            producto.setEstado(1);
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(int id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto actualizar(int id, Producto producto) {
        if(!productoRepository.existsById(id)){
            throw new RuntimeException("Producto no encontrado con ID " + id);
        }
        producto.setCodigoProducto(id);
        validarProducto(producto);
        return productoRepository.save(producto);
    }

    @Override
    public void eliminar(int id) {
        if(!productoRepository.existsById(id)){
            throw new RuntimeException("Producto no encontrado con ID " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(int id) {
        return productoRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorEstado(int estado) {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .filter(p -> p.getEstado() == estado)
                .toList();
    }

    private void validarProducto(Producto producto){
        if(producto.getNombreProducto() == null || producto.getNombreProducto().trim().isEmpty()){
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if(producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if(producto.getStock() < 0){
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}