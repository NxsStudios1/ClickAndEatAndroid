// com/example/login/network/SpringApiService.java
package com.example.login.network;

import com.example.login.network.model.ComentarioDto;
import com.example.login.network.model.RespuestaComentarioDto;
import com.example.login.network.model.UsuarioDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SpringApiService {

    // ====== EXISTENTE: usuarios ======
    @GET("usuario")
    Call<List<UsuarioDto>> getUsuarios();

    @POST("usuario")
    Call<UsuarioDto> crearUsuario(@Body UsuarioDto usuario);

    // ====== COMENTARIOS CLIENTE ======
    @GET("comentario")
    Call<List<ComentarioDto>> getComentarios();

    @POST("comentario")
    Call<ComentarioDto> crearComentario(@Body ComentarioDto comentario);

    @DELETE("comentario/{id}")
    Call<Void> eliminarComentario(@Path("id") int idComentario);

    // ====== RESPUESTAS DEL ADMIN ======
    @GET("respuestaComentario")
    Call<List<RespuestaComentarioDto>> getRespuestasComentario();

    @POST("respuestaComentario")
    Call<RespuestaComentarioDto> crearRespuestaComentario(@Body RespuestaComentarioDto respuesta);
}
