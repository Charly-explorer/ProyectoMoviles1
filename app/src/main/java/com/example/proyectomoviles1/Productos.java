package com.example.proyectomoviles1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Productos extends AppCompatActivity {
    ListView listViewProductos;
    AdminDB db;
    ArrayList<Producto> lista;
    CustomAdapterProductos adapter;
    EditText txtBuscador;
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
        this.txtBuscador = (EditText) findViewById(R.id.txtnombre);
        db = new AdminDB(this, "UTN", null, 1);
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
        listViewProductos = findViewById(R.id.listViewGestionProductos);


        lista = db.obtenerProductos();

        adapter = new CustomAdapterProductos(this, lista);

        listViewProductos.setAdapter(adapter);

        this.txtBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString().toLowerCase();
                buscarProducto(texto);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });


    }
    public void GestionarProductos(View view){
        Intent intent= new Intent(this,View_AgregarProducto.class);
        startActivity(intent);
    }
    private void buscarProducto(String texto) {
        ArrayList<Producto> filtrada = new ArrayList<>();
        for (Producto inv : this.lista) {
            if (inv.getNombre().toLowerCase().contains(texto)) {
                filtrada.add(inv);
            }
        }
        adapter.updateList(filtrada);
    }


}