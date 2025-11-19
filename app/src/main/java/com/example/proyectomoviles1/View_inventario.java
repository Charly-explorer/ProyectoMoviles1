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
        db = new AdminDB(this, "InventarioDB", null, 1);
        SQLiteDatabase bd = db.getWritableDatabase();

        // 1. Asegurar que exista al menos una categoría
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

        // 2. Asegurar que los productos necesarios para el inventario de prueba existan
        // Usamos INSERT OR IGNORE para que no falle si ya existen
        bd.execSQL("INSERT OR IGNORE INTO Productos(codigo, nombre, idCategoria, descripcion) VALUES(150, 'Bolsa Maíz', 1, 'Producto inicial')");
        bd.execSQL("INSERT OR IGNORE INTO Productos(codigo, nombre, idCategoria, descripcion) VALUES(160, 'Saco de Frijoles', 1, 'Producto inicial')");
        bd.execSQL("INSERT OR IGNORE INTO Productos(codigo, nombre, idCategoria, descripcion) VALUES(170, 'Caja de Papas', 1, 'Producto inicial')");


        // 3. Insertar datos de prueba en Inventario si está vacío
        c = bd.rawQuery("SELECT COUNT(*) FROM Inventario", null);
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            if (count == 0) {
                bd.execSQL("INSERT INTO Inventario(codigoProducto, existencias, estado) VALUES(150, 10, 1)");
                bd.execSQL("INSERT INTO Inventario(codigoProducto, existencias, estado) VALUES(160, 5, 1)");
                bd.execSQL("INSERT INTO Inventario(codigoProducto, existencias, estado) VALUES(170, 4, 0)");
                
                // Opcional: Registrar estos movimientos iniciales también si se desea, 
                // pero como es carga directa por SQL, los triggers/código no los registran en Movimientos.
            }
        }
        c.close();

        this.lista = new ArrayList<>();
        this.listViewInventario = findViewById(R.id.listViewInv);

        lista = db.obtenerInventario();
        this.adapter = new CustomAdapterInventario(this, this.lista);
        this.listViewInventario.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Intent i = getIntent();
            int codigo = i.getIntExtra("codigo", 0);
            String nombre = i.getStringExtra("nombre");
            int existencia = i.getIntExtra("existencia", 0);
            if(codigo >0 && !nombre.isEmpty() && existencia >=0){
                db.guardarOActualizarInventario(codigo, existencia, true);
                lista = db.obtenerInventario();
                adapter.updateList(lista);
                listViewInventario.setAdapter(adapter);
            }
        }

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
            Inventario inv = (Inventario) adapter.getItem(itemseleccionado);
            db.desactivarInventarioPorId(inv.getIdInv());
            //Toast.makeText(getApplicationContext(),String.valueOf(inv.getCodigoProducto()), Toast.LENGTH_SHORT).show();
            View itemresaltado = listViewInventario.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            lista = db.obtenerInventario();
            adapter.updateList(lista);
            itemseleccionado = -1;
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

    public void volverMenu(View view){
        Intent intent= new Intent(this,menu_principal.class);
        startActivity(intent);
    }

}