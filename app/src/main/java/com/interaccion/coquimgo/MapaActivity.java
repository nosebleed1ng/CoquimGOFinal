package com.interaccion.coquimgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.interaccion.coquimgo.databinding.ActivityMapaBinding;

public class MapaActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap gMap;
    private ActivityMapaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMapaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar
        setSupportActionBar(binding.toolbarMapa);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.mapa));
        }

        // Botón volver
        Button btnVolverMapa = binding.btnVolverMapa;
        btnVolverMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Fragment de mapa
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.id_mapa_general);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // Centra en Coquimbo
        LatLng coquimbo = new LatLng(-29.9387069, -71.2916155);

        // Marcadores
        LatLng fuerteLambert = new LatLng(-29.933971429838568, -71.3360721762996);
        gMap.addMarker(new MarkerOptions().position(fuerteLambert).title("Fuerte Lambert"));

        LatLng cruzTercerMilenio = new LatLng(-29.951469351311008, -71.34737146280611);
        gMap.addMarker(new MarkerOptions().position(cruzTercerMilenio).title("Cruz del Tercer Milenio"));

        LatLng pueblitoPenuelas = new LatLng(-29.948892824, -71.291997180);
        gMap.addMarker(new MarkerOptions().position(pueblitoPenuelas).title("Pueblito Peñuelas"));

        LatLng avenidaDelMar = new LatLng(-29.915549, -71.275552);
        gMap.addMarker(new MarkerOptions().position(avenidaDelMar).title("Avenida del Mar"));

        LatLng laMezquita = new LatLng(-29.9634188, -71.3362789);
        gMap.addMarker(new MarkerOptions().position(laMezquita).title("La Mezquita"));

        LatLng elFaro = new LatLng(-29.905579, -71.274209);
        gMap.addMarker(new MarkerOptions().position(elFaro).title("El Faro"));

        LatLng parqueJapones = new LatLng(-29.90388889, -29.90388889);
        gMap.addMarker(new MarkerOptions().position(parqueJapones).title("Parque Japonés"));

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coquimbo, 12));

        gMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Muestra un diálogo para preguntar si se quiere ir a la ficha
        new AlertDialog.Builder(this)
                .setTitle(marker.getTitle())
                .setMessage(getString(R.string.pregunta_ver_info_mapa))
                .setPositiveButton(getString(R.string.ver_informacion), (dialog, which) -> {
                    abrirInformacionLugarDesdeMarker(marker);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

        return true;
    }

    private void abrirInformacionLugarDesdeMarker(Marker marker) {
        if (marker == null || marker.getTitle() == null) return;

        String titulo = marker.getTitle();
        String nombreLugarExtra;

        switch (titulo) {
            case "Fuerte Lambert":
                nombreLugarExtra = "fuerte lambert";
                break;
            case "Cruz del Tercer Milenio":
                nombreLugarExtra = "cruz del tercer milenio";
                break;
            case "Pueblito Peñuelas":
                nombreLugarExtra = "pueblito peñuelas";
                break;
            case "Avenida del Mar":
                nombreLugarExtra = "avenida del mar";
                break;
            case "La Mezquita":
                nombreLugarExtra = "la mezquita";
                break;
            case "El Faro":
                nombreLugarExtra = "el faro";
                break;
            case "Parque Japonés":
                nombreLugarExtra = "parque japonés";
                break;
            default:
                nombreLugarExtra = titulo.toLowerCase();
                break;
        }

        Intent intent = new Intent(MapaActivity.this, InformacionLugarActivity.class);
        intent.putExtra("nombreLugar", nombreLugarExtra);
        intent.putExtra("origen", "mapa");
        startActivity(intent);
    }
}
