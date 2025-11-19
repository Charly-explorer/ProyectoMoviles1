package com.example.proyectomoviles1;

public class Movimiento {
    private int id;
    private String nombreProducto;
    private String fecha;
    private String tipoMovimiento;
    private String detalle;

    public Movimiento(int id, String nombreProducto, String fecha, String tipoMovimiento, String detalle) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.detalle = detalle;
    }

    public int getId() {
        return id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public String getDetalle() {
        return detalle;
    }
}