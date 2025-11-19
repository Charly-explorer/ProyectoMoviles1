package com.example.proyectomoviles1;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Crearusuarios extends AppCompatActivity {

    private EditText etNombre, etApellido, etApellido2, etCorreo, etContrasena;
    private CheckBox cbEsAdmin;
    private AdminDB adminDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etApellido2 = findViewById(R.id.etApellido2);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        cbEsAdmin = findViewById(R.id.cbEsAdmin);

        adminDB = new AdminDB(this, "InventarioDB", null, 1);
    }

    public void onGuardarUsuarioClick(View view) {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String apellido2 = etApellido2.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        int esAdmin = cbEsAdmin.isChecked() ? 1 : 0;

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Nombre, correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        long resultado = adminDB.insertarUsuario(nombre, apellido, apellido2, correo, contrasena, esAdmin);

        if (resultado == -1) {
            Toast.makeText(this, "Error al crear usuario (¿correo duplicado?)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void onVolverUsuarioClick(View view) {

        finish();
    }
}
