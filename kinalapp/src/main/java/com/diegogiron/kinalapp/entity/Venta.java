package com.diegogiron.kinalapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_venta")
    private long codigoVenta;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_venta")
    private Date fechaVenta;

    @Column
    private double total;

    @Column
    private int estado;

    @ManyToOne
    @JoinColumn(name = "clientes_dpi_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "usuarios_codigo_usuario")
    private Usuario usuario;

    public Venta() {}

    public Venta(Date fechaVenta, double total, int estado, Cliente cliente, Usuario usuario) {
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.estado = estado;
        this.cliente = cliente;
        this.usuario = usuario;
    }

    public long getCodigoVenta() { return codigoVenta; }
    public void setCodigoVenta(long codigoVenta) { this.codigoVenta = codigoVenta; }

    public Date getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Date fechaVenta) { this.fechaVenta = fechaVenta; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}