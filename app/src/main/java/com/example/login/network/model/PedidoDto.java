package com.example.login.network.model;

import java.util.List;

public class PedidoDto {

    private int id;
    private String numeroTicket;
    private int estado;
    private double total;
    private String fechaPedido;
    private String observaciones;
    private int idCliente;

    private List<DetallePedidoDto> detalles;

    private String nombreCliente;

    public String getNombreCliente() {
        return nombreCliente;
    }
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(String fechaPedido) { this.fechaPedido = fechaPedido; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public List<DetallePedidoDto> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoDto> detalles) { this.detalles = detalles; }
}
