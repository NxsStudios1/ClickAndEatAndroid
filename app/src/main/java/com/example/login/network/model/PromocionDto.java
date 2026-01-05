package com.example.login.network.model;

public class PromocionDto {
    private int id;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private double precioTotalConDescuento;
    private boolean activo;

    public PromocionDto() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public double getPrecioTotalConDescuento() { return precioTotalConDescuento; }
    public void setPrecioTotalConDescuento(double precioTotalConDescuento) {
        this.precioTotalConDescuento = precioTotalConDescuento;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
