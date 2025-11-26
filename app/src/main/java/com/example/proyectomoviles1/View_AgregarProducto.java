package com.example.proyectomoviles1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class View_AgregarProducto extends AppCompatActivity {

    EditText txtCodigo, txtNombre,txtDescripcion; //Tambien recordar la FOTO
    TextView txtEstadoAud;
    Spinner spCategoria;
    AdminDB db;
    ArrayList<Producto> Gestionlista;
    int codigoProducto = 0;
    private ActivityResultLauncher<Intent> lanzadorTomarFoto;
    private Bitmap imagenBitmap;
    private byte[] imagenBytes;
    private ImageView vistaImagen;
    private ActivityResultLauncher<String> lanzadorPermisoCamara;
    private ActivityResultLauncher<String> lanzadorPermisoAudio;
    private byte[] imagenProducto;
    ImageButton btnGrabar, btnDetener, btnReproducir, btnPausa, btnRemplazar;
    private static final int REQUEST_PERMISSION_CODE = 1000;
    private SQLiteDatabase database;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;
    private boolean tieneAudioBD = false;
    private boolean audioModificado = false;
    File audioFile;
    byte[] audioData;

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

        lanzadorPermisoAudio = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(this, "Se requiere el permiso de audio para grabar", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        lanzadorPermisoCamara = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(this, "Se requiere el permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show();
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        lanzadorPermisoAudio.launch(Manifest.permission.RECORD_AUDIO);
                    }
                }
        );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            lanzadorPermisoCamara.launch(Manifest.permission.CAMERA);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                lanzadorPermisoAudio.launch(Manifest.permission.RECORD_AUDIO);
            }
        }

        vistaImagen = findViewById(R.id.imageView3);
        this.txtCodigo = (EditText) findViewById(R.id.txtCodigo);
        this.txtNombre = (EditText) findViewById(R.id.txtNombre);
        this.txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        this.spCategoria = (Spinner) findViewById(R.id.spCategoria);

        btnGrabar = findViewById(R.id.btnGrabar);
        btnDetener = findViewById(R.id.btnDetener);
        btnReproducir = findViewById(R.id.btnReproducir);
        btnPausa = findViewById(R.id.btnPausa);
        btnRemplazar = findViewById(R.id.btnRemplazar);
        txtEstadoAud = findViewById(R.id.txtEstadoAudio);

        setEstadoBotones(new ImageButton[]{btnDetener,btnReproducir, btnPausa}, false);

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
            audioData = getIntent().getByteArrayExtra("audio");
            if (imagenProducto != null) {
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(imagenProducto, 0, imagenProducto.length);
                vistaImagen.setImageBitmap(bitmap2);
            }
            if(audioData != null){
                setEstadoBotones(new ImageButton[]{btnRemplazar,btnReproducir}, true);
                setEstadoBotones(new ImageButton[]{btnGrabar, btnPausa, btnDetener}, false);
                tieneAudioBD = true;
                cargarAudioDesdeBD(audioData, codigoProducto);
            }
        } else {
            outputFile = getExternalFilesDir(null).getAbsolutePath() + "/Grabacion.3gp";
        }



        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();

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
        guardarAudio();
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
                db.guardarOActualizarProducto(codigoProducto, nombre, descripcion, idCategoria, imagen2, audioData );

                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Producto Creado", Toast.LENGTH_SHORT).show();
            db.guardarOActualizarProducto(codigo, nombre, descripcion, idCategoria, imagen2, audioData);
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

    public void guardarAudio(){
        if (!audioModificado) {
            return;
        }
        if (outputFile == null) {
            audioData = null;
            return;
        }
        audioFile = new File(outputFile);
        if (!audioFile.exists() || audioFile.length() == 0) {
            audioData = null;
            return;
        }
        audioData = new byte[(int) audioFile.length()];
        try (FileInputStream fis = new FileInputStream(audioFile)) {
            fis.read(audioData);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer el archivo de audio", Toast.LENGTH_SHORT).show();
            audioData = null;
        }
    }

    private void cargarAudioDesdeBD(byte[] audioBytes, long idProducto) {
        if (audioBytes != null && audioBytes.length > 0) {
            try {

                File tempFile = new File(getCacheDir(), "audio_prod_" + idProducto + ".3gp");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(audioBytes);
                fos.close();

                outputFile = tempFile.getAbsolutePath();
                tieneAudioBD = true;
                audioModificado = false;

                setEstadoBotones(new ImageButton[]{btnReproducir}, true);
                txtEstadoAud.setText("Audio guardado");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            tieneAudioBD = false;
            audioModificado = false;
            setEstadoBotones(new ImageButton[]{btnReproducir}, false);
            txtEstadoAud.setText("Sin audio");

            File nuevo = new File(getExternalFilesDir(null),
                    "audio_nuevo_" + System.currentTimeMillis() + ".3gp");
            outputFile = nuevo.getAbsolutePath();
        }
    }

    public void iniciarGrabacion(View view) {
        try {
            audioModificado = true;

            if (txtCodigo.getText().toString().isEmpty()) {
                Toast.makeText(this, "Digite el código del producto primero", Toast.LENGTH_SHORT).show();
                return;
            }

            File f = new File(getExternalFilesDir(null), "audio_prod_" + txtCodigo.getText().toString() + ".3gp");
            outputFile = f.getAbsolutePath();

            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.prepare();
            mediaRecorder.start();

            setEstadoBotones(new ImageButton[]{btnGrabar,btnReproducir,btnPausa}, false);
            setEstadoBotones(new ImageButton[]{btnDetener}, true);
            Toast.makeText(this, "La grabación comenzó", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void detenerGrabacion(View view) {
        try {
            mediaRecorder.stop();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        mediaRecorder.reset();

        setEstadoBotones(new ImageButton[]{btnReproducir}, true);
        setEstadoBotones(new ImageButton[]{btnDetener}, false);
        txtEstadoAud.setText("Audio grabado");
        Toast.makeText(this, "El audio se grabó con éxito", Toast.LENGTH_SHORT).show();
    }

    public void iniciarReproduccion(View view) {
        try {
            File f = new File(outputFile);
            if (!f.exists() || f.length() == 0) {
                Toast.makeText(this, "No hay audio disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();

            setEstadoBotones(new ImageButton[]{btnPausa}, true);
            Toast.makeText(this, "Reproducción de audio", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detenerReproduccion(View view) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();

                setEstadoBotones(new ImageButton[]{btnReproducir}, true);
                setEstadoBotones(new ImageButton[]{btnPausa}, false);
                Toast.makeText(this, "Reproducción de audio detenida", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void remplazarAudio(View view){
        setEstadoBotones(new ImageButton[]{btnReproducir}, true);
        setEstadoBotones(new ImageButton[]{btnGrabar}, true);
        audioModificado = false;
    }
    public void Regresar(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void setEstadoBotones(ImageButton[] botones, boolean estado) {
        for (ImageButton btn : botones) {
            if (btn == null) continue;
            btn.setEnabled(estado);
            if (estado) {
                btn.setBackgroundResource(R.drawable.bg_icon_button);
            } else {
                btn.setBackgroundResource(R.drawable.bg_icon_button_transparent);
            }
        }
    }



}