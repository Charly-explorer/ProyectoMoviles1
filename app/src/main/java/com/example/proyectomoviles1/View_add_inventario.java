package com.example.proyectomoviles1;

import android.content.Intent;
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

public class View_add_inventario extends AppCompatActivity {
    ListView listViewProductosInv;
    AdminDB db;
    ArrayList<Producto> lista;
    CustomAdapterProductos adapter;
    EditText txtBuscador;
    int itemseleccionado = -1;

    EditText txtCodigoPro, txtNombrePro, txtExistenciaInv, txtBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_add_inventario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new AdminDB(this, "InventarioDB", null, 1);
        listViewProductosInv = findViewById(R.id.listViewProductosInv);
        lista = db.obtenerProductos();
        adapter = new CustomAdapterProductos(this, lista);

        listViewProductosInv.setAdapter(adapter);

        txtCodigoPro = findViewById(R.id.txtCodeInv);
        txtNombrePro = findViewById(R.id.txtNameInv);
        txtNombrePro.setEnabled(false);
        txtExistenciaInv = findViewById(R.id.txtExisInv);
        txtBuscar = findViewById(R.id.txtbuscarInvA);
        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString().toLowerCase();
                filtrarInventario(texto);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        this.listViewProductosInv.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            itemseleccionado = position;
            for (int i = 0; i < listViewProductosInv.getChildCount(); i++) {
                listViewProductosInv.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            view.setBackgroundColor(Color.LTGRAY);
            view.findViewById(R.id.textViewCodeInv);
        });
    }

    private void filtrarInventario(String texto) {
        ArrayList<Producto> filtrada = new ArrayList<>();
        for (Producto pro : this.lista) {
            if (pro.nombre.toLowerCase().contains(texto)) {
                filtrada.add(pro);
            }
        }
        this.adapter.updateList(filtrada);
    }

    public void agregar (View view){
        if (itemseleccionado >= 0)
        {
            Producto pro = (Producto) adapter.getItem(itemseleccionado);
            View itemresaltado = listViewProductosInv.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            txtNombrePro.setText(pro.nombre);
            txtCodigoPro.setText(String.valueOf(pro.code));
            txtCodigoPro.setEnabled(false);
            itemseleccionado = -1;
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Debe seleccionar un item", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardar(View view){
        String codigo = txtCodigoPro.getText().toString();
        String nombre = txtNombrePro.getText().toString();
        String existencia = txtExistenciaInv.getText().toString();
        if(!codigo.isEmpty() && !nombre.isEmpty() && !existencia.isEmpty()){
            Intent i = new Intent(View_add_inventario.this, View_inventario.class);
            i.putExtra("codigo", Integer.parseInt(codigo));
            i.putExtra("nombre", nombre);
            i.putExtra("existencia", Integer.parseInt(existencia));
            startActivity(i);
        } else{
            Toast.makeText(getApplicationContext(),"Debe llenar todas las casillas", Toast.LENGTH_SHORT).show();
        }
    }

}