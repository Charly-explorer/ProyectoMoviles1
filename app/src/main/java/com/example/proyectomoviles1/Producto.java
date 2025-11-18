package com.example.proyectomoviles1;

public class Producto {
    public int code;
    public String nombre;
    public int idCategoria;
    public String descripcion;

    public Producto(String nombre, String descripcion, int idCategoria) {
        this.code = 0;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
    }
    public Producto(int code, String nombre, String descripcion, int idCategoria) {
        this.code = code;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
}