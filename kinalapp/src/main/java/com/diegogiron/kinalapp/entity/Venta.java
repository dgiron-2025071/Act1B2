package com.diegogiron.kinalapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_venta")
    private long codigoVenta;

    @Column(name = "fecha_venta")
    private LocalDate fechaVenta;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column
    private Integer estado;

    @ManyToOne
    @JoinColumn(name = "clientes_dpi_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "usuarios_codigo_usuario")
    private Usuario usuario;

    public Venta() {}

    public Venta(LocalDate fechaVenta, BigDecimal total, Integer estado, Cliente cliente, Usuario usuario) {
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.estado = estado;
        this.cliente = cliente;
        this.usuario = usuario;
    }

    public long getCodigoVenta() { return codigoVenta; }
    public void setCodigoVenta(long codigoVenta) { this.codigoVenta = codigoVenta; }
    public LocalDate getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDate fechaVenta) { this.fechaVenta = fechaVenta; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}