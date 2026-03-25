package com.diegogiron.kinalapp.service;

import com.diegogiron.kinalapp.entity.Cliente;
import com.diegogiron.kinalapp.repository.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//Anotacion que registra un bean como bean de spring
//Que la clase contiene la logica del negocio
@Service
//Por defecto todos los metodos de esta clase seran "transaccionales"
//Una transaccion es que puede o no ocurrir algo
@Transactional

public class ClienteService implements IClienteService{
    /*  Private: Solo accesible dentro de la clase
        ClienteRepository: Es el repositorio para acceder a la base datos.
        Inyeccion de dependencias por lo que Spring nos da el repositorio.
     */
    private final ClienteRepository clienteRepository;

    /*
    * Constructor: Este se ejecuta al crear el objeto
    * Parametros: Spring pasa el repositorio automaticamente y a esto se le conoce como inyeccion de dependencias
    * Asignamos el repositorio a nuestra variable de clase
    */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /*
    * @Override: Indica que estamos implementando un metodo de la interfaz
    */
    @Override
    /*
    * readOnly = true: Lo que hace es optimizar la consulta
    */
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
        /*
        * Lama al metodo findAll() del repositorio de Spring Data JPA
        * Este metodo hace exactamente el select * from.
        */
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        /*
         * Metodo de guardar crea un cliente
         * Aca es donde colocamos la logica del negocio Antes de guardar
         * Primero validanmos el dato
         */
        validarCliente(cliente);
        if(cliente.getEstado()==0){
            cliente.setEstado(1);
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorDPI(String dpi) {
        //Buscar un cliente por DPI
        return clienteRepository.findById(dpi);
        //Optional nos evita el NullPointerException
    }

    @Override
    public Cliente actualizar(String dpi, Cliente cliente) {
        //Actualiza un cliente existente
        if(!clienteRepository.existsById(dpi)){
            throw new RuntimeException("Cliente no se encontro con DPI " + dpi);
            //Si no existe, se lanza una exscepcion (es decir un error controlado)
        }
        /*
        * 1. Asegurar que le DPI del objeto coincida con el de la URL
        * 2. Por seguridad usamops el DPI de la URL y no el que viene de JSON
        * */
        cliente.setDPICliente(dpi);
        validarCliente(cliente);

        return clienteRepository.save(cliente);
    }

    @Override
    public void eliminar(String dpi) {
        //Eliminar un cliente
        if(!clienteRepository.existsById(dpi)){
            throw new RuntimeException("El Cliente no se encontro con el DPI "+ dpi);
        }
        clienteRepository.deleteById(dpi);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorDPI(String dpi) {
        //Verificar si existe el Cliente
        return clienteRepository.existsById(dpi);
        //Retorna true o false.
    }

    //Metodo Privado(Solo puede utilizarse dentro de la clase)
    private void validarCliente(Cliente cliente){
        /*
        * Validaciones Del Negocio: Este metodo se hara privado por que es algo nterno del servicio.
        */
        if(cliente.getDPICliente() == null || cliente.getDPICliente().trim().isEmpty()){
            //Si el DPI es null o esta vacio despues de quitar espacios
            //Lanza una excepcion con un mensaje
            throw new IllegalArgumentException("El DPI es un dato obligatorio,");
        }

        if(cliente.getNombreCliente()== null || cliente.getNombreCliente().trim().isEmpty()){
            throw new IllegalArgumentException("El Nombre del cliente es obligatorio");
        }

        if(cliente.getApellidoCliente()== null || cliente.getApellidoCliente().trim().isEmpty()){
            throw new IllegalArgumentException("El Apellido del cliente es obligatorio");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarPorEstado(int estado) {
        // Obtiene todos los clientes desde la base de datos
        List<Cliente> clientes = clienteRepository.findAll();
        // Convierte la lista en un Stream para poder aplicar operaciones funcionales
        return clientes.stream()
                //.filter(...) permite aplicar una condición para filtrar elementos
                .filter(c -> c.getEstado() == estado)
                /*
                * c -> es una expresión lambda donde:
                * c representa cada objeto Cliente de la lista
                * c.getEstado() obtiene el estado del cliente actual
                * == estado compara si coincide con el parámetro recibido
                 */

                //.toList() convierte el resultado del Stream nuevamente en una lista
                .toList();
    }
}