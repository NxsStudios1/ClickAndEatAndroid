// app/src/main/java/com/example/login/network/model/CategoriaProductoDto.java
package com.example.login.network.model;

import com.google.gson.annotations.SerializedName;

public class CategoriaProductoDto {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    public CategoriaProductoDto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
