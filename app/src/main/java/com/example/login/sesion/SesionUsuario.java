package com.example.login.sesion;

public class SesionUsuario {

    private static SesionUsuario instancia;

    private int idUsuario;
    private String nombre;
    private String telefono;
    private int rol;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstance() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public void setDatos(int idUsuario, String nombre, String telefono, int rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getRol() {
        return rol;
    }

    public void limpiar() {
        idUsuario = 0;
        nombre = null;
        telefono = null;
        rol = 0;
    }
}
