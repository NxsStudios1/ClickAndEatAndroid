package com.example.login.network.model;

import com.google.gson.annotations.SerializedName;

public class RespuestaComentarioDto {

    @SerializedName("id")
    private int id;

    @SerializedName("contenido")
    private String contenido;

    @SerializedName("fechaRespuesta")
    private String fechaRespuesta;

    @SerializedName("idComentario")
    private int idComentario;

    @SerializedName("idAdministrador")
    private int idAdministrador;

    @SerializedName("nombreAdministrador")
    private String nombreAdministrador;

    public RespuestaComentarioDto() {
    }

    public int getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public String getFechaRespuesta() {
        return fechaRespuesta;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public String getNombreAdministrador() {
        return nombreAdministrador;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setFechaRespuesta(String fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public void setNombreAdministrador(String nombreAdministrador) {
        this.nombreAdministrador = nombreAdministrador;
    }
}
