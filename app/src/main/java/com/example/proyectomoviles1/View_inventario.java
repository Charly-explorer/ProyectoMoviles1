package com.example.proyectomoviles1;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class View_inventario extends AppCompatActivity {
    ListView listViewInventario;
    AdminDB db;
    ArrayList<Inventario> lista;
    CustomAdapterInventario adapter;
    EditText txtBuscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_inventario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.txtBuscador = (EditText) findViewById(R.id.txtcodInv);
        //AdminDB db = new AdminDB(this, "UTN", null, 1);
       //SQLiteDatabase bd = db.getWritableDatabase();
        this.lista = new ArrayList<>();
        this.listViewInventario = findViewById(R.id.listViewInv);

       // db = new AdminDB(this, "miBD", null, 1);

        //lista = db.obtenerInventario();
        this.lista.add(new Inventario(1,150, "Bolsa Maiz",10,true));
        this.lista.add(new Inventario(2,160, "Saco de Frijoles",5,true));
        this.lista.add(new Inventario(3,170, "Caja de Papas",4,false));

        this.adapter = new CustomAdapterInventario(this, this.lista);
        this.listViewInventario.setAdapter(adapter);

        this.txtBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString();
                filtrarInventario(texto);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filtrarInventario(String texto) {
        ArrayList<Inventario> filtrada = new ArrayList<>();
        for (Inventario inv : this.lista) {
            if (String.valueOf(inv.getNombreProducto()).contains(texto)) {
                filtrada.add(inv);
            }
        }
        adapter.updateList(filtrada);
    }

}