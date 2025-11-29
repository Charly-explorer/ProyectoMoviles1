package com.example.proyectomoviles1;

public class Producto {
    private int code;
    private String nombre;
    private int idCategoria;
    private String descripcion;
    private byte[] imagen;
    private  byte[] audio;
    private double latitud;
    private double longitud;

    public Producto(int code, String nombre, String descripcion, int idCategoria, byte[] imagen, byte[] audio, double latitud, double longitud) {
        this.code = code;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.imagen = imagen;
        this.audio = audio;
        this.latitud = latitud;
        this.longitud = longitud;
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
    public byte[] getAudio() { return audio;}

    public void setAudio(byte[] audio) { this.audio = audio; }
    
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}