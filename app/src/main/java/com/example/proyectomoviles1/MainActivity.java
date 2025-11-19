package com.example.proyectomoviles1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private AdminDB adminDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        adminDB = new AdminDB(this, "InventarioDB", null, 1);

    }

    public void onLoginClick(View view) {
        String correo = etUsuario.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(MainActivity.this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer esAdmin = adminDB.loginUsuario(correo, pass);
        if (esAdmin == null) {
            Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        } else {
            // AQUÍ: tanto admin como usuario van al menú
            Intent intent = new Intent(MainActivity.this, menu_principal.class);
            intent.putExtra("esAdmin", esAdmin);   // 1 = admin, 0 = usuario
            startActivity(intent);
            finish();
        }
    }


    public void Siguiente(View view){
        Intent intent= new Intent(this,menu_principal.class);
        startActivity(intent);
    }

}