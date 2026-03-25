package com.diegogiron.kinalapp.controller;

import com.diegogiron.kinalapp.service.IClienteService;
import com.diegogiron.kinalapp.entity.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RestController = @Controller + @ResponseBody
@RequestMapping("/clientes")
// Todas las rutas en este controlador deben empezar con /clientes
public class ClienteController {

    //Inyectamos el servicio y No el repositorio
    //El controlador solo debe de tener conexion con el servicio

    private final IClienteService clienteService;

    //Como buena practica la Inyeccion de dependencias debe hacerse por el constructor
    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    //Resppnde a peticiones GET
    @GetMapping
    //ResponseEntity nos permite controlar el codigo HTTP y el cuerpo
    public ResponseEntity<List<Cliente>> listar(){
        List<Cliente> clientes = clienteService.listarTodos();
        //delegamos el servicio
        return ResponseEntity.ok(clientes);
        //200 OK con la lista de clientes
    }

    //Responde a peticiones GET para filtrar por estado
    @GetMapping("/estado/{estado}")
    //{estado} es una variable de ruta
    public ResponseEntity<List<Cliente>> listarPorEstado(@PathVariable Integer estado){
        //@PathVariable toma el valor de la URL y lo asigna al parametro estado
        List<Cliente> clientes = clienteService.listarPorEstado(estado);
        //Delegamos al servicio la consulta filtrada por estado
        if(clientes.isEmpty()){
            return ResponseEntity.noContent().build();
            //204 NO CONTENT si no existen clientes con ese estado
        }
        return ResponseEntity.ok(clientes);
        //200 OK con la lista de clientes filtrados
    }

    // {dpi} Es una variable de ruta (Valor a buscar)
    @GetMapping("/{dpi}")
    public ResponseEntity<Cliente> buscarPorDPI(@PathVariable String dpi){
        //El @PathVariable toma el valor de la URL y lo asigna al DPI
        return clienteService.buscarPorDPI(dpi)
                //Si optional tiene valor, devuelve 200 ok con el cliente
                .map(ResponseEntity::ok)
                //Si optional no tiene valor, devuelvo 404 NOT FOUND
                .orElse(ResponseEntity.notFound().build());
    }

    // POST crea un nuevo cliente
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Cliente cliente){
        //@RequestBody: Toma el JSON del cuerpo y lo convierte a un objeto de tipo cliente
        //<?> significa que es de "Tipo Generico" puede ser un cliente o un string
        try {
            Cliente nuevoCliente = clienteService.guardar(cliente);
            //Intentamos guardar el cliente pero puede lanzar una excepcion de IllegalArgumentException
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
            //201 CREATED (Mucho mas especifico que el 200, para la creacion de un cliente)
        }catch(IllegalArgumentException e){
            //Si hay error de validacion
            return ResponseEntity.badRequest().body(e.getMessage());
            //400 BAD REQUEST con el mensaje de error.
        }
    }

    //DELETE elimina un cliente
    @DeleteMapping("/{dpi}")
    public ResponseEntity<Void> eliminar(@PathVariable String dpi){
       //ResponseEntity<Void> : No devuelve cuerpo enj la respuesta.
        try{
            if(!clienteService.existePorDPI(dpi)) {
                return ResponseEntity.notFound().build();
                //404 Si no existe
            }
            clienteService.eliminar(dpi);
            return ResponseEntity.noContent().build();
            //204 NO CONTENT (Se ejecuto correctamente y no devuelve cuerpo)
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
            //404 NOT FOUND
        }


    }

    //Actualizar cliente a traves de DPI
    @PutMapping("/{dpi}")
    public ResponseEntity<?> actualizar(@PathVariable String dpi, @RequestBody Cliente cliente){
        try{
            if(!clienteService.existePorDPI(dpi)){
                //Verificar si existe antes de poder actualizar
                //404 NOT FOUND
                return ResponseEntity.notFound().build();
            }
            //Actualizar el cliente esto puede lanzar una excepcion
            Cliente clienteActualizado = clienteService.actualizar(dpi,cliente);
            return ResponseEntity.ok(clienteActualizado);
            //200 ok con el cliente ya actualizado
        }catch (IllegalArgumentException e){
            //Error cuando los datos sean incorrectos
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e){
            //Posible cualquier otro error como: cliente no encontrado, etc
            //404 NOT FOUND
            return ResponseEntity.notFound().build();
        }
    }

}

