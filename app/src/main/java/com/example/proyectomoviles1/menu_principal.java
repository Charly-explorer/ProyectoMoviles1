package com.example.proyectomoviles1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class menu_principal extends AppCompatActivity {

    ListView listViewMenu;
    ArrayList<OpcionMenu> opciones;
    CustomAdapterMenu adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewMenu = findViewById(R.id.listViewMenu);

        int esAdmin = getIntent().getIntExtra("esAdmin", 0);

        opciones = new ArrayList<>();

        opciones.add(new OpcionMenu(
                R.mipmap.producto_foreground,
                getString(R.string.menu_productos)
        ));

        opciones.add(new OpcionMenu(
                R.mipmap.inventario_foreground,
                getString(R.string.menu_inventario)
        ));

        if (esAdmin == 1) {
            opciones.add(new OpcionMenu(
                    R.mipmap.movimiento_inv_foreground,
                    getString(R.string.menu_movimientos)
            ));
            opciones.add(new OpcionMenu(
                    R.mipmap.add_user_foreground, // ícono placeholder para crear usuario
                    getString(R.string.menu_crear_usuarios)
            ));
        }

        opciones.add(new OpcionMenu(
                R.mipmap.salir_foreground, // ícono placeholder para cerrar sesión
                getString(R.string.menu_cerrar_sesion)
        ));

        adapter = new CustomAdapterMenu(this, opciones);
        listViewMenu.setAdapter(adapter);

        listViewMenu.setOnItemClickListener((parent, view, position, id) -> {
            OpcionMenu opcion = opciones.get(position);
            String titulo = opcion.getTitulo();

            if (titulo.equals(getString(R.string.menu_productos))) {
                PaginaProductos(null);
            } else if (titulo.equals(getString(R.string.menu_inventario))) {
                PaginaInventario(null);
            } else if (titulo.equals(getString(R.string.menu_movimientos))) {
                PaginaMovimientos(null);
            } else if (titulo.equals(getString(R.string.menu_crear_usuarios))) {
                PaginaCrearUsuario(null);
            } else if (titulo.equals(getString(R.string.menu_cerrar_sesion))) {
                onCerrarSesionClick(null);
            }
        });
    }

    public void PaginaProductos(View view){
        Intent intent = new Intent(this, Productos.class);
        startActivity(intent);
    }

    public void PaginaInventario(View view){
        Intent intent = new Intent(this, View_inventario.class);
        startActivity(intent);
    }

    public void PaginaMovimientos(View view){
        Intent intent = new Intent(this, activity_movimientos.class);
        startActivity(intent);
    }

    public void PaginaCrearUsuario(View view){
        Intent intent = new Intent(this, Crearusuarios.class);
        startActivity(intent);
    }

    public void onCerrarSesionClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}