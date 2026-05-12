package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface IClienteService {
    //Interfaz: Es un contrato que dice que metodofos debe tener cualquier servicio de Clientes.
    //No tiene implementacion, solo la definicion de los metodos.

    //Metodo que devuelve una lista de todos los clientes
    List<Cliente> listarTodos();
    //List<Cliente> lo que hace es devolver una lista de objetos de la entidad clientes

    //Metodo que devuelve una lista de clientes segun su estado
    List<Cliente> listarPorEstado(int estado);
    //Recibe un estado como parametro y retorna los clientes que coincidan con ese valor

    //Metodo que guarda un cliente en la base de datos
    Cliente guardar(Cliente cliente);
    //Parametros - Recibe un objeto de tipo cliente con los datos a guardar

    //Optional - Contenedor que puede o no tener un valor
    //Evita el error de NullPointerException
    Optional<Cliente> buscarPorDPI(String dpi);

    //Metodo que actualiza un cliente
    Cliente actualizar(String dpi, Cliente cliente);
    //Parametros - dpi: DPI del cliente a actualizar
    //Cliente cliente : Objeto con los datos nuevos
    //Retorna un objeto de3 tipo cliente ya actualizado

    //Metodo de tipo void para eliminar a un cliente
    //void: no retorna ningun dato
    //Elimina un cliente por su DPI
    void eliminar(String dpi);

    //boolean - Retorna true si existe, false si no existe
    boolean existePorDPI(String dpi);
}
