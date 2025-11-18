package com.example.proyectomoviles1;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class View_AgregarProducto extends AppCompatActivity {
    ListView listViewProductos;
    AdminDB db;
    ArrayList<Producto> Gestionlista;
    CustomAdapterProductos Gestionlistaadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_agregar_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new AdminDB(this, "UTN", null, 1);
        SQLiteDatabase bd = db.getWritableDatabase();
        listViewProductos = findViewById(R.id.listViewGestionProductos);

        Gestionlista = db.obtenerProductos();

        Gestionlistaadapter = new CustomAdapterProductos(this, Gestionlista);

        listViewProductos.setAdapter(Gestionlistaadapter);
    }
}