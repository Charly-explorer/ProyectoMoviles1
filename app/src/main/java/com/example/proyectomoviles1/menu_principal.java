package com.example.proyectomoviles1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu_principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int esAdmin = getIntent().getIntExtra("esAdmin", 0);
        Button btnMovimientos = findViewById(R.id.button3);
        Button btnCrearUsuario = findViewById(R.id.buttonCrearUsuario);
        if (esAdmin == 0) {
            if (btnMovimientos != null) btnMovimientos.setVisibility(View.GONE);
            if (btnCrearUsuario != null) btnCrearUsuario.setVisibility(View.GONE);
        }
    }
    public void PaginaProductos(View view){
        Intent intent= new Intent(this,Productos.class);
        startActivity(intent);
    }

    public void PaginaInventario(View view){
        Intent intent= new Intent(this,View_inventario.class);
        startActivity(intent);
    }
    public void PaginaCrearUsuario(View view) {
        Intent intent = new Intent(this, Crearusuarios.class);
        startActivity(intent);
    }

    public void onCerrarSesionClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Cerramos el menú actual
    }
    public void PaginaMovimientos(View view) {
        Intent intent = new Intent(this, activity_movimientos.class);
        startActivity(intent);
    }


}