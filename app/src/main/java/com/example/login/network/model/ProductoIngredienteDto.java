// app/src/main/java/com/example/login/network/model/ProductoIngredienteDto.java
package com.example.login.network.model;

import com.google.gson.annotations.SerializedName;

public class ProductoIngredienteDto {

    @SerializedName("id")
    private int id;

    @SerializedName("cantidadIngrediente")
    private double cantidadIngrediente;

    @SerializedName("idProducto")
    private int idProducto;

    @SerializedName("idIngrediente")
    private int idIngrediente;

    public ProductoIngredienteDto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getCantidadIngrediente() { return cantidadIngrediente; }
    public void setCantidadIngrediente(double cantidadIngrediente) {
        this.cantidadIngrediente = cantidadIngrediente;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getIdIngrediente() { return idIngrediente; }
    public void setIdIngrediente(int idIngrediente) { this.idIngrediente = idIngrediente; }
}
