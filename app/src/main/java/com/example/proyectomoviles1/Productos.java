package com.example.proyectomoviles1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class Productos extends AppCompatActivity {
    ListView listViewProductos;
    AdminDB db;
    ArrayList<Producto> lista;
    CustomAdapterProductos adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AdminDB db = new AdminDB(this, "UTN", null, 1);
        SQLiteDatabase bd = db.getWritableDatabase();

// Verificar si existen categorías
        Cursor c = bd.rawQuery("SELECT COUNT(*) FROM Categorias", null);

        if (c.moveToFirst()) {
            int count = c.getInt(0);
            if (count == 0) {
                bd.execSQL("INSERT INTO Categorias(nombre) VALUES('Prueba')");
                bd.execSQL("INSERT INTO Productos(nombre, idCategoria, descripcion) " +
                        "VALUES('Producto Prueba', 1, 'Cargado desde Activity')");
            }
        }
        c.close();
        listViewProductos = findViewById(R.id.listViewProductos);

        db = new AdminDB(this, "miBD", null, 1);

        lista = db.obtenerProductos();

        adapter = new CustomAdapterProductos(this, lista);

        listViewProductos.setAdapter(adapter);


    }


}