package com.interaccion.coquimgo;

import android.health.connect.datatypes.ExerciseRoute;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.interaccion.coquimgo.databinding.ActivityMapaBinding;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    private ActivityMapaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_mapa_general);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng coquimbo = new LatLng(-29.9387069,-71.2916155);
        LatLng fuerteLambert = new LatLng(-29.933971429838568, -71.3360721762996);
        gMap.addMarker(new MarkerOptions().position(fuerteLambert).title("Fuerte Lambert"));
        LatLng cruzTercerMilenio = new LatLng(-29.951469351311008, -71.34737146280611);
        gMap.addMarker(new MarkerOptions().position(cruzTercerMilenio).title("Cruz del Tercer Milenio"));
        LatLng pueblitoPenuelas = new LatLng(-29.948892824, -71.291997180);
        gMap.addMarker(new MarkerOptions().position(pueblitoPenuelas).title("Pueblito Peñuelas"));
        LatLng avenidaDelMar = new LatLng(-29.915549, -71.275552);
        gMap.addMarker(new MarkerOptions().position(avenidaDelMar).title("Avenida del Mar"));
        LatLng laMezquita = new LatLng(-29.9634188,-71.3362789);
        gMap.addMarker(new MarkerOptions().position(laMezquita).title("La Mezquita"));
        LatLng elFaro = new LatLng(-29.905579, -71.274209);
        gMap.addMarker(new MarkerOptions().position(elFaro).title("El Faro"));
        LatLng parqueJapones = new LatLng(-29.90388889, -29.90388889);
        gMap.addMarker(new MarkerOptions().position(parqueJapones).title("Parque Japonés"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coquimbo, 12));
    }
}