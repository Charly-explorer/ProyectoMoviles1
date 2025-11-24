package com.example.proyectomoviles1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class View_AgregarProducto extends AppCompatActivity {

    EditText txtCodigo, txtNombre,txtDescripcion; //Tambien recordar la FOTO
    Spinner spCategoria;
    AdminDB db;
    ArrayList<Producto> Gestionlista;
    int codigoProducto = 0;
    private ActivityResultLauncher<Intent> lanzadorTomarFoto;
    private Bitmap imagenBitmap;
    private byte[] imagenBytes;
    private ImageView vistaImagen;
    private ActivityResultLauncher<String> lanzadorPermisoCamara;
    private byte[] imagenProducto;

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

        lanzadorPermisoCamara = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) {
                Toast.makeText(this, "Se requiere el permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show();
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            lanzadorPermisoCamara.launch(Manifest.permission.CAMERA);
        }

        vistaImagen = findViewById(R.id.imageView3);
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
        lanzadorTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        imagenBitmap = (Bitmap) resultado.getData().getExtras().get("data");
                        vistaImagen.setImageBitmap(imagenBitmap);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        imagenBytes = stream.toByteArray();
                    }
                }
        );

        codigoProducto = getIntent().getIntExtra("codigo", -1);
        if (codigoProducto >= 0) {
            txtCodigo.setEnabled(false);

            txtCodigo.setText(String.valueOf(codigoProducto));
            txtNombre.setText(getIntent().getStringExtra("nombre"));
            txtDescripcion.setText(getIntent().getStringExtra("descripcion"));
            seleccionarCategoriaEnSpinner(getIntent().getIntExtra("idCategoria", 0));
            imagenProducto = getIntent().getByteArrayExtra("imagen");
            if (imagenProducto != null) {
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(imagenProducto, 0, imagenProducto.length);
                vistaImagen.setImageBitmap(bitmap2);
            }
            /// Recibe la foto y la carga en el ImageView
        }


    }
    public void tomarFoto(View vista) {
        Intent intentTomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        lanzadorTomarFoto.launch(intentTomarFoto);
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
        if(imagenProducto != null){
            imagenBytes = imagenProducto;
        }
        if (imagenBytes == null) {
            Toast.makeText(this, "Debe tomar una foto", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] imagen2 = imagenBytes;

        if (codigoProducto >= 0) {

            if(!nombre.isEmpty() && !descripcion.isEmpty()){
                db.guardarOActualizarProducto(codigoProducto, nombre, descripcion, idCategoria, imagen2 );

                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Producto Creado", Toast.LENGTH_SHORT).show();
            db.guardarOActualizarProducto(codigo, nombre, descripcion, idCategoria, imagen2);
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