package com.example.proyectomoviles1;

public class Inventario {
    private int idInv;
    private int codigoProducto;
    private String nombreProducto;
    private int existencia;
    private boolean estado;

    public Inventario(int idInv, int codigoProducto, String nombreProducto, int existencia, boolean estado) {
        this.idInv = idInv;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.existencia = existencia;
        this.estado = estado;
    }

    public int getIdInv() {
        return idInv;
    }

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getExistencia() {
        return existencia;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
