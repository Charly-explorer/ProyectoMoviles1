package com.example.proyectomoviles1;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    int itemseleccionado = -1;

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
        removerInventarioDesactivado();
        this.txtBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString().toLowerCase();
                filtrarInventario(texto);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        this.listViewInventario.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            itemseleccionado = position;
            for (int i = 0; i < listViewInventario.getChildCount(); i++) {
                listViewInventario.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            view.setBackgroundColor(Color.LTGRAY);
            view.findViewById(R.id.textViewCodeInv);
        });
    }

    private void filtrarInventario(String texto) {
        ArrayList<Inventario> filtrada = new ArrayList<>();
        for (Inventario inv : this.lista) {
            if (inv.getNombreProducto().toLowerCase().contains(texto)) {
                filtrada.add(inv);
            }
        }
        this.adapter.updateList(filtrada);
    }

    public void removerInventarioDesactivado(){
        ArrayList<Inventario> newlist = new ArrayList<>();
        for (Inventario inv : lista){
            if(inv.isEstado()){
                newlist.add(inv);
            }
        }
        this.adapter.updateList(newlist);
    }

    public void eliminar(View v){
        if (itemseleccionado >= 0)
        {
            //Falta que cambie el estado en la base de datos, para que no aparesca
            //EliminarPorNombre(adapter.getItem(itemseleccionado));
            Inventario inv = (Inventario) adapter.getItem(itemseleccionado);
            //adapter.remove(inv.getIdInv());
            //lista.remove(inv);
            //adapter.remove(inv);
            View itemresaltado = listViewInventario.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            itemseleccionado = -1;
            removerInventarioDesactivado();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Debe seleccionar un item", Toast.LENGTH_SHORT).show();
        }
    }

    public void viewEditInv(View view){
        Intent intent= new Intent(this,View_add_inventario.class);
        startActivity(intent);
    }

}