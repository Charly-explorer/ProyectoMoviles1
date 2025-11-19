package com.example.proyectomoviles1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

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

        opciones = new ArrayList<>();
        opciones.add(new OpcionMenu(R.mipmap.producto_foreground, getString(R.string.menu_productos)));
        opciones.add(new OpcionMenu(R.mipmap.inventario_foreground, getString(R.string.menu_inventario)));
        opciones.add(new OpcionMenu(R.mipmap.movimiento_inv_foreground, getString(R.string.menu_movimientos)));


        adapter = new CustomAdapterMenu(this, opciones);
        listViewMenu.setAdapter(adapter);

        listViewMenu.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, Productos.class));
                    break;

                case 1:
                    startActivity(new Intent(this, View_inventario.class));
                    break;

                case 2:
                    //startActivity(new Intent(this, Movimientos.class));
                    break;
            }
        });
    }
}
