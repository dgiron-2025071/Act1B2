package com.diegogiron.kinalapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @Column(name = "id_usuario")
    private int idUsuario;
    @Column
    private String nombreUsuario;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private int estado;
    public Usuario() {}
    public Usuario(int idUsuario, String nombreUsuario, String username, String password, int estado) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.username = username;
        this.password = password;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}