package com.example.proyectomoviles1;

public class OpcionMenu {
    private int iconoResId;
    private String titulo;

    public OpcionMenu(int iconoResId, String titulo) {
        this.iconoResId = iconoResId;
        this.titulo = titulo;
    }

    public int getIconoResId() {
        return iconoResId;
    }

    public String getTitulo() {
        return titulo;
    }
}
