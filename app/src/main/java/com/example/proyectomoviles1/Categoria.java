package com.example.proyectomoviles1;

public class Categoria {
    private int id;
    private String nombre;

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // IMPORTANTE: esto es lo que el Spinner muestra
    @Override
    public String toString() {
        return nombre;
    }
}

