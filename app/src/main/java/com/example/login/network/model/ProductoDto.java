package com.example.login.network.model;

import com.google.gson.annotations.SerializedName;

public class ProductoDto {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("precio")
    private double precio;

    @SerializedName("disponible")
    private boolean disponible;

    // OJO: pon aquí el nombre EXACTO que use tu backend:
    // puede ser "idCategoriaProducto" o "idCategoria"
    @SerializedName("idCategoria")
    private int idCategoria;      // usamos este en el código

    // (opcional) nombre de categoría que regresa el backend
    @SerializedName("nombreCategoria")
    private String nombreCategoria;

    // getters / setters...

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
}
