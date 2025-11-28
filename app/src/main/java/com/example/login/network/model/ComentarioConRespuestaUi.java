package com.example.login.network.model;

public class ComentarioConRespuestaUi {

    public int idComentario;
    // Comentario del cliente
    public String nombreCliente;
    public String fechaComentario;
    public String categoria;   // texto (COMIDA, SERVICIO, etc.)
    public int calificacion;
    public String asunto;
    public String contenido;

    // Respuesta del administrador (opcional)
    public String nombreAdmin;
    public String fechaRespuesta;
    public String contenidoRespuesta;
}
