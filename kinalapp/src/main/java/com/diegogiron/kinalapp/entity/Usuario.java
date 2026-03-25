package com.diegogiron.kinalapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @Column(name = "codigo_usuario")
    private int codigoUsuario;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String rol;
    @Column
    private int estado;

    public Usuario() {}

    public Usuario(int codigoUsuario, String username, String password, String email, String rol, int estado) {
        this.codigoUsuario = codigoUsuario;
        this.username = username;
        this.password = password;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
    }

    public int getCodigoUsuario() { return codigoUsuario; }
    public void setCodigoUsuario(int codigoUsuario) { this.codigoUsuario = codigoUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}