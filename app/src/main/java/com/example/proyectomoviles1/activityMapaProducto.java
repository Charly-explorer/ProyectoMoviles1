package com.example.proyectomoviles1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;

public class activityMapaProducto extends AppCompatActivity {

    private MapView mapView;
    private Button btnConfirmarUbicacion;
    private GeoPoint ubicacionSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_mapa_producto);

        mapView = findViewById(R.id.map);
        btnConfirmarUbicacion = findViewById(R.id.btnConfirmarUbicacion);

        // Configuración del mapa
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);

        // Obtener coordenadas default
        double lat = getIntent().getDoubleExtra("latitud", 10.430684188597372);
        double lon = getIntent().getDoubleExtra("longitud", -85.08498580135634);
        
        //En caso exotico de que vengan el 00, usar las siguientes cordenadas
        if (lat == 0.0 && lon == 0.0) {
            lat = 10.430684188597372;
            lon = -85.08498580135634;
        }

        GeoPoint puntoInicial = new GeoPoint(lat, lon);
        mapView.getController().setCenter(puntoInicial);
        actualizarMarcador(puntoInicial);

        // Clicks en el mapa
        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                actualizarMarcador(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                actualizarMarcador(p);
                return true;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mReceiver);
        mapView.getOverlays().add(mapEventsOverlay);

        btnConfirmarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ubicacionSeleccionada != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("latitudSeleccionada", ubicacionSeleccionada.getLatitude());
                    resultIntent.putExtra("longitudSeleccionada", ubicacionSeleccionada.getLongitude());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    private void actualizarMarcador(GeoPoint punto) {
        ubicacionSeleccionada = punto;
        mapView.getOverlays().clear();
        //Esta clase permite que se pueda detectar los clicks en el mapa y poder actualizar el mapa
        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                actualizarMarcador(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                actualizarMarcador(p);
                return true;
            }
        };
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mReceiver);
        mapView.getOverlays().add(mapEventsOverlay);

        Marker nuevo = new Marker(mapView);
        nuevo.setPosition(punto);
        nuevo.setTitle("Ubicación seleccionada");
        nuevo.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(nuevo);
        mapView.invalidate();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}