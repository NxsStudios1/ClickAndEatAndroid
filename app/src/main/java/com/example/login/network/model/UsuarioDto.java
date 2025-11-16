package com.example.login.network.model;

public class UsuarioDto {

    private int id;
    private String nombre;
    private String telefono;
    private String contrasena;
    private int rol;

    public UsuarioDto() {
    }

    public UsuarioDto(int id, String nombre, String telefono, String contrasena, int rol) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public int getRol() {
        return rol;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }
}
