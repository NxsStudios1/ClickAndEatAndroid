// com/example/login/network/SpringApiService.java
package com.example.login.network;

import com.example.login.network.model.CategoriaProductoDto;
import com.example.login.network.model.ComentarioDto;
import com.example.login.network.model.IngredienteDto;
import com.example.login.network.model.ProductoDto;
import com.example.login.network.model.ProductoIngredienteDto;
import com.example.login.network.model.PromocionDto;
import com.example.login.network.model.PromocionProductoDto;
import com.example.login.network.model.RespuestaComentarioDto;
import com.example.login.network.model.UsuarioDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SpringApiService {

    // ====== USUARIOS ======
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


    // =================== INGREDIENTES ===================

    @GET("ingrediente")
    Call<List<IngredienteDto>> getIngredientes();

    @POST("ingrediente")
    Call<IngredienteDto> crearIngrediente(@Body IngredienteDto ingrediente);

    @PUT("ingrediente/{id}")
    Call<IngredienteDto> actualizarIngrediente(@Path("id") int id,
                                               @Body IngredienteDto ingrediente);

    @DELETE("ingrediente/{id}")
    Call<Void> eliminarIngrediente(@Path("id") int id);


    // =================== CATEGORIA PRODUCTO ===================

    @GET("categoriaProducto")
    Call<List<CategoriaProductoDto>> getCategoriasProducto();

    @POST("categoriaProducto")
    Call<CategoriaProductoDto> crearCategoriaProducto(@Body CategoriaProductoDto dto);

    @PUT("categoriaProducto/{id}")
    Call<CategoriaProductoDto> actualizarCategoriaProducto(
            @Path("id") int id,
            @Body CategoriaProductoDto dto
    );

    @DELETE("categoriaProducto/{id}")
    Call<Void> eliminarCategoriaProducto(@Path("id") int id);


    // =================== PRODUCTOS ===================

    @GET("producto")
    Call<List<ProductoDto>> getProductos();

    @POST("producto")
    Call<ProductoDto> crearProducto(@Body ProductoDto dto);

    @PUT("producto/{id}")
    Call<ProductoDto> actualizarProducto(
            @Path("id") int id,
            @Body ProductoDto dto
    );

    @DELETE("producto/{id}")
    Call<Void> eliminarProducto(@Path("id") int id);


    // =================== PRODUCTO-INGREDIENTE ===================

    @GET("productoIngrediente")
    Call<List<ProductoIngredienteDto>> getProductoIngredientes();

    @POST("productoIngrediente")
    Call<ProductoIngredienteDto> crearProductoIngrediente(@Body ProductoIngredienteDto dto);

    @PUT("productoIngrediente/{id}")
    Call<ProductoIngredienteDto> actualizarProductoIngrediente(
            @Path("id") int id,
            @Body ProductoIngredienteDto dto
    );

    @DELETE("productoIngrediente/{id}")
    Call<Void> eliminarProductoIngrediente(@Path("id") int id);


    // ================== PROMOCIONES ==================
    @GET("promocion")
    Call<List<PromocionDto>> getPromociones();

    @POST("promocion")
    Call<PromocionDto> crearPromocion(@Body PromocionDto dto);

    @PUT("promocion/{id}")
    Call<PromocionDto> actualizarPromocion(@Path("id") int id,
                                           @Body PromocionDto dto);

    @DELETE("promocion/{id}")
    Call<Void> eliminarPromocion(@Path("id") int id);

    // ============ PROMOCION - PRODUCTO (RELACIÓN) ============
    @GET("promocionProducto")
    Call<List<PromocionProductoDto>> getPromocionProductos();

    @POST("promocionProducto")
    Call<PromocionProductoDto> crearPromocionProducto(@Body PromocionProductoDto dto);

    @PUT("promocionProducto/{id}")
    Call<PromocionProductoDto> actualizarPromocionProducto(@Path("id") int id,
                                                           @Body PromocionProductoDto dto);

    @DELETE("promocionProducto/{id}")
    Call<Void> eliminarPromocionProducto(@Path("id") int id);

}
