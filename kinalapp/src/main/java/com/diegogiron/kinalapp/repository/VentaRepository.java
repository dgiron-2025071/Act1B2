package com.diegogiron.kinalapp.repository;

import com.diegogiron.kinalapp.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}