package com.example.proyectomoviles1;

public class Producto {
    private int code;
    private String nombre;
    private int idCategoria;
    private String descripcion;
    private byte[] imagen;

    public Producto(int code, String nombre, String descripcion, int idCategoria, byte[] imagen) {
        this.code = code;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.imagen = imagen;
    }

    public int getCode() {
        return code;
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

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idcategoria) {
        idCategoria = idcategoria;
    }
    public byte[] getImagen() { return imagen; }
    public void setImagen(byte[] imagen) { this.imagen = imagen; }
}