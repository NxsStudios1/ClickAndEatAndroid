package com.example.login.network.model;

import com.google.gson.annotations.SerializedName;

public class IngredienteDto {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("cantidadPorcion")
    private double cantidadPorcion;

    @SerializedName("unidadMedida")
    private String unidadMedida; // "GRAMOS", "LITROS", etc.

    @SerializedName("stockActual")
    private double stockActual;

    @SerializedName("precioUnitario")
    private double precioUnitario;

    public IngredienteDto() {}

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCantidadPorcion() {
        return cantidadPorcion;
    }

    public void setCantidadPorcion(double cantidadPorcion) {
        this.cantidadPorcion = cantidadPorcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getStockActual() {
        return stockActual;
    }

    public void setStockActual(double stockActual) {
        this.stockActual = stockActual;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
