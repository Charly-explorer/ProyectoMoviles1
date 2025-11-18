package com.example.proyectomoviles1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class View_AgregarProducto extends AppCompatActivity {
    ListView listViewProductos;
    EditText txtCodeInv, txtNameInv;
    Spinner spCategoria;
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
        this.txtCodeInv = (EditText) findViewById(R.id.txtNombre);
        this.txtNameInv = (EditText) findViewById(R.id.txtDescripcion);
        this.spCategoria = (Spinner) findViewById(R.id.spCategoria);
        db = new AdminDB(this, "UTN", null, 1);
        SQLiteDatabase bd = db.getWritableDatabase();


        ArrayList<Categoria> categorias = obtenerCategorias();
        ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);
        listViewProductos = findViewById(R.id.listViewGestionProductos);

        Gestionlista = db.obtenerProductos();

        Gestionlistaadapter = new CustomAdapterProductos(this, Gestionlista);

        listViewProductos.setAdapter(Gestionlistaadapter);
    }
    public void crearProducto(View view){
        SQLiteDatabase bd = db.getWritableDatabase();

        Categoria categoriaSeleccionada = (Categoria) spCategoria.getSelectedItem();
        int idCategoria = categoriaSeleccionada.getId();


        String nombre = txtCodeInv.getText().toString();
        String descripcion = txtNameInv.getText().toString();
        String sql = "INSERT INTO Productos(nombre, idCategoria, descripcion) VALUES (?, ?, ?)";
        SQLiteStatement stmt = bd.compileStatement(sql);
        stmt.bindString(1, nombre);
        stmt.bindLong(2, idCategoria);
        stmt.bindString(3, descripcion);
        stmt.executeInsert();

        Gestionlistaadapter.updateList(db.obtenerProductos());

    }
    public ArrayList<Categoria> obtenerCategorias() {

        SQLiteDatabase bd = db.getWritableDatabase();
        ArrayList<Categoria> lista = new ArrayList<>();

        Cursor c = bd.rawQuery("SELECT * FROM Categorias", null);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String nombre = c.getString(1);

                lista.add(new Categoria(id, nombre));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

}