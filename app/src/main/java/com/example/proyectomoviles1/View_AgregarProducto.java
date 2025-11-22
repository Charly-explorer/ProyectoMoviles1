package com.example.proyectomoviles1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class View_AgregarProducto extends AppCompatActivity {

    EditText txtCodigo, txtNombre,txtDescripcion; //Tambien recordar la FOTO
    Spinner spCategoria;
    AdminDB db;
    ArrayList<Producto> Gestionlista;
    int codigoProducto = 0;


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



        this.txtCodigo = (EditText) findViewById(R.id.txtCodigo);
        this.txtNombre = (EditText) findViewById(R.id.txtNombre);
        this.txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        this.spCategoria = (Spinner) findViewById(R.id.spCategoria);

        db = new AdminDB(this, "InventarioDB", null, 1);
        SQLiteDatabase bd = db.getWritableDatabase();

        ArrayList<Categoria> categorias = obtenerCategorias();
        ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);

        Gestionlista = db.obtenerProductos();

        codigoProducto = getIntent().getIntExtra("codigo", -1);
        if (codigoProducto != 0) {
            txtCodigo.setEnabled(false);

            txtCodigo.setText(String.valueOf(codigoProducto));
            txtNombre.setText(getIntent().getStringExtra("nombre"));
            txtDescripcion.setText(getIntent().getStringExtra("descripcion"));
            seleccionarCategoriaEnSpinner(getIntent().getIntExtra("idCategoria", 0));
            /// Aqui tambien debo cargar la imagen --------------------------------------------------------------------
        }

    }

    //Esto lo voy hacer con un IF si me llego un boolean true de la pagina anterior que edite el del codigo enviado y si no que cree uno nuevo

    //Nota: Si se envio true que significa que va a editar recordarme desabilitar el EditText del codigo, que este no sea modificable
    public void crearProducto(View view){
        SQLiteDatabase bd = db.getWritableDatabase();
        Categoria categoriaSeleccionada = (Categoria) spCategoria.getSelectedItem();
        int idCategoria = categoriaSeleccionada.getId();

        int codigo = Integer.parseInt(txtCodigo.getText().toString());
        String nombre = txtNombre.getText().toString();
        String descripcion = txtDescripcion.getText().toString();

        if(codigo == 0 || nombre.isEmpty() || descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }else {
            String sql = "INSERT INTO Productos(codigo, nombre, idCategoria, descripcion) VALUES (?, ?, ?, ?)";
            SQLiteStatement stmt = bd.compileStatement(sql);
            stmt.bindLong(1, codigo);
            stmt.bindString(2, nombre);
            stmt.bindLong(3, idCategoria);
            stmt.bindString(4, descripcion);
            stmt.executeInsert();

        }
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

    public void Guardar(View view){

        int codigo = Integer.parseInt(txtCodigo.getText().toString());
        String nombre = txtNombre.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        Categoria categoriaSeleccionada = (Categoria) spCategoria.getSelectedItem();
        int idCategoria = categoriaSeleccionada.getId();

        if (codigoProducto != 0) {
            Producto prod = db.obtenerProducto(codigoProducto);
            prod.setNombre(nombre);
            prod.setDescripcion(descripcion);
            prod.setIdCategoria(idCategoria);
            ///  Aqui tambien tengo que mandar la foto ----------------------------------------------------

            if(!nombre.isEmpty() && !descripcion.isEmpty()){

                //db.guardarOActualizarProducto(codigoProducto, nombre, idCategoria, descripcion);

                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Producto Creado", Toast.LENGTH_SHORT).show();
            //Producto prod = new Producto(codigo, nombre, descripcion, idCategoria);

            finish();
        }


        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        spCategoria.setSelection(0);
        finish();
    }

    private void seleccionarCategoriaEnSpinner(int idCategoria) {
        for (int i = 0; i < spCategoria.getCount(); i++) {
            Categoria c = (Categoria) spCategoria.getItemAtPosition(i);
            if (c.getId() == idCategoria) {
                spCategoria.setSelection(i);
                break;
            }
        }
    }

    public void Regresar(View view){
        finish();
    }


}