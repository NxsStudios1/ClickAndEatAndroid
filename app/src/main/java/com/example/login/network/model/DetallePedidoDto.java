package com.example.login.network.model;

import com.google.gson.annotations.SerializedName;

public class DetallePedidoDto {

    private int id;
    private int idPedido;

    private Integer idProducto;
    private Integer idPromocion;

    @SerializedName("tipoItem")
    private int tipoItem;

    private int cantidad;
    private double precioUnitario;

    @SerializedName("subtotal")
    private double subtotal;

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Integer getIdPromocion() { return idPromocion; }
    public void setIdPromocion(Integer idPromocion) { this.idPromocion = idPromocion; }

    public int getTipoItem() { return tipoItem; }
    public void setTipoItem(int tipoItem) { this.tipoItem = tipoItem; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}
