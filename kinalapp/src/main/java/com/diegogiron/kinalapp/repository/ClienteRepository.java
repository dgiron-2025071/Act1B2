package com.diegogiron.kinalapp.repository;

import com.diegogiron.kinalapp.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente,String> {

}
