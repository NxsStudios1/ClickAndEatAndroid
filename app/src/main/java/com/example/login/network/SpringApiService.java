package com.example.login.network;

import com.example.login.network.model.UsuarioDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SpringApiService {

    @GET("usuario")
    Call<List<UsuarioDto>> getUsuarios();

    @POST("usuario")
    Call<UsuarioDto> crearUsuario(@Body UsuarioDto usuario);
}
