package com.example.proyectomoviles1;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class activity_movimientos extends AppCompatActivity {
    private ListView listViewMovimientos;
    private AdminDB adminDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        listViewMovimientos = findViewById(R.id.listViewMovimientos);
        adminDB = new AdminDB(this, "InventarioDB", null, 1);


        ArrayList<Movimiento> listaMov = adminDB.obtenerMovimientos();

        ArrayList<String> textos = new ArrayList<>();

        for (Movimiento m : listaMov) {
            String linea = m.getFecha() + " - " + m.getNombreProducto() +
                    " - " + m.getTipoMovimiento();

            if (m.getDetalle() != null && !m.getDetalle().isEmpty()) {
                linea += " (" + m.getDetalle() + ")";
            }

            textos.add(linea);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                textos
        );

        listViewMovimientos.setAdapter(adapter);
    }

    public void Regresar(View view) {
        finish();
    }
}