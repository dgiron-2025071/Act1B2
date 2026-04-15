package com.diegogiron.kinalapp.repository;

import com.diegogiron.kinalapp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}