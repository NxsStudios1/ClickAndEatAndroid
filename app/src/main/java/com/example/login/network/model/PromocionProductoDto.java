// com/example/login/network/model/PromocionProductoDto.java
package com.example.login.network.model;

public class PromocionProductoDto {
    private int id;
    private int idPromocion;
    private int idProducto;
    private double cantidadProducto; // double para cuadrar con el backend

    public PromocionProductoDto() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(int idPromocion) {
        this.idPromocion = idPromocion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public double getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(double cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }
}
