package com.example.proyectomoviles1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Productos extends AppCompatActivity {
    public ListView listViewProductos;
    AdminDB db;
    ArrayList<Producto> lista;
    CustomAdapterProductos adapter;
    EditText txtBuscador;
    Button Nuevo, Editar, Eliminar;
    Producto producto = null;
    int seleccionado = -1;
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
        this.Nuevo = (Button) findViewById(R.id.btnNuevo);
        this.Editar = (Button) findViewById(R.id.btnEditar);
        this.Eliminar = (Button) findViewById(R.id.btnEliminar);

        listViewProductos = findViewById(R.id.listViewGestionProductos);

        this.txtBuscador = (EditText) findViewById(R.id.txtnombre);
        db = new AdminDB(this, "InventarioDB", null, 1);
        SQLiteDatabase bd = db.getWritableDatabase();

        Cursor c = bd.rawQuery("SELECT COUNT(*) FROM Categorias", null);

        if (c.moveToFirst()) {
            int count = c.getInt(0);
            if (count == 0) {
                bd.execSQL("INSERT INTO Categorias(nombre) VALUES('Cat 1')");
                bd.execSQL("INSERT INTO Categorias(nombre) VALUES('Cat 2')");
                bd.execSQL("INSERT INTO Categorias(nombre) VALUES('Cat 3')");
            }
        }
        c.close();
        listViewProductos = findViewById(R.id.listViewGestionProductos);

        cargarProductos();

        this.listViewProductos.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            seleccionado = position;
            for (int i = 0; i < listViewProductos.getChildCount(); i++) {
                listViewProductos.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            view.setBackgroundColor(Color.LTGRAY);
            Editar.setEnabled(true);
            Nuevo.setEnabled(false);
            Eliminar.setEnabled(true);
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos(); // vuelve a consultar BD y setear el adapter
        Editar.setEnabled(false);
        Nuevo.setEnabled(true);
    }

    public void cargarProductos() {
        lista = db.obtenerProductos();

        adapter = new CustomAdapterProductos(this, lista);

        listViewProductos.setAdapter(adapter);
    }

    public void Nuevo(View view) {
        Intent i = new Intent(this, View_AgregarProducto.class);
        startActivity(i);
    }
    public void Editar(View view) {

        if (seleccionado >= 0) {
            Producto pro = (Producto) adapter.getItem(seleccionado);
            View itemresaltado = listViewProductos.getChildAt(seleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            int codigo = pro.getCode();
            String nombre = pro.getNombre();
            String descripcion = pro.getDescripcion();
            int idCategoria = pro.getIdCategoria();
            byte[] imagen = pro.getImagen();
            byte[] audio = pro.getAudio();
            double latitud = pro.getLatitud();
            double longitud = pro.getLongitud();

            producto = db.obtenerProducto(codigo);

            if (producto != null) {
                Intent i = new Intent(this,View_AgregarProducto.class);
                i.putExtra("codigo", codigo);
                i.putExtra("nombre", nombre);
                i.putExtra("descripcion", descripcion);
                i.putExtra("idCategoria", idCategoria);
                i.putExtra("imagen", imagen);
                i.putExtra("audio", audio);
                i.putExtra("latitud", latitud);
                i.putExtra("longitud", longitud);
                Eliminar.setEnabled(false);
                startActivity(i); 

            } else {
                Toast.makeText(getApplicationContext(), "Error con el producto", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Debe seleccionar un producto", Toast.LENGTH_SHORT).show();
        }
        seleccionado = -1;
    }
    public void Regresar(View view){
        finish();
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

    public  void Eliminar(View view){
        if (seleccionado >= 0) {
            Producto pro = (Producto) adapter.getItem(seleccionado);
            View itemresaltado = listViewProductos.getChildAt(seleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            int codigo = pro.getCode();
            if(db.eliminarProductoPorCodigo(codigo)){
                Toast.makeText(getApplicationContext(), "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Debe seleccionar un producto para eliminar", Toast.LENGTH_SHORT).show();
        }
        seleccionado = -1;
        Eliminar.setEnabled(false);
        Editar.setEnabled(false);
        Nuevo.setEnabled(true);
        cargarProductos();
    }

}