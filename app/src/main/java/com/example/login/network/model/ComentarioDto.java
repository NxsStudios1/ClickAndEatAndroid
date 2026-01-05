package com.example.login.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ComentarioDto {

    @SerializedName("id")
    private int id;

    @SerializedName("asunto")
    private String asunto;

    @SerializedName("contenido")
    private String contenido;

    @SerializedName("calificacion")
    private int calificacion;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("fechaComentario")
    private String fechaComentario;

    @SerializedName("idCliente")
    private int idCliente;

    @SerializedName("nombreCliente")
    private String nombreCliente;

    @SerializedName("respuestas")
    private List<RespuestaComentarioDto> respuestas;

    public int getId() {
        return id;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public List<RespuestaComentarioDto> getRespuestas() {
        return respuestas;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setRespuestas(List<RespuestaComentarioDto> respuestas) {
        this.respuestas = respuestas;
    }
}
