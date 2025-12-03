package com.example.login.network.model;

import java.util.List;

public class PedidoDto {

    private int id;
    private String numeroTicket;
    private int estado;           // PENDIENTE, EN_PROCESO, etc.
    private double total;
    private String fechaPedido;      // opcional, el backend puede ignorar
    private String observaciones;
    private int idCliente;           // FK a cliente

    private List<DetallePedidoDto> detalles;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

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
