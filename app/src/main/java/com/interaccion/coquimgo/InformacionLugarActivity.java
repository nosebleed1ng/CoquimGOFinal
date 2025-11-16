package com.interaccion.coquimgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.interaccion.coquimgo.model.Lugar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class InformacionLugarActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imgLugar;
    private TextView txtNombreLugar, txtDescripcion, txtUbicacion, txtHorarios, txtCostos;
    private GoogleMap gMap;
    private double coordenadaX;
    private double coordenadaY;
    private String nomMap;
    private Button btnVolver, btnMarcarVisitado, btnMarcarFavorito;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar idioma guardado antes del layout
        SharedPreferences prefsConfig = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefsConfig.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_lugar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Referencias al layout
        imgLugar = findViewById(R.id.imglugar);
        txtNombreLugar = findViewById(R.id.txtnombreLugar);
        txtDescripcion = findViewById(R.id.txtdescripcion);
        txtUbicacion = findViewById(R.id.txtubicacion);
        txtHorarios = findViewById(R.id.txthorarios);
        txtCostos = findViewById(R.id.txtcostos);
        btnVolver = findViewById(R.id.btnvolver);
        btnMarcarVisitado = findViewById(R.id.btnmarcarvisitado);
        btnMarcarFavorito = findViewById(R.id.btnmarcarfavorito);

        // Textos traducibles
        btnVolver.setText(getString(R.string.volver));
        btnMarcarVisitado.setText(getString(R.string.visitado));
        btnMarcarFavorito.setText(getString(R.string.favorito));

        // Detectar origen
        String origen = getIntent().getStringExtra("origen");

        btnVolver.setOnClickListener(v -> {
            if ("lugares_visitados".equals(origen)) {
                startActivity(new Intent(this, LugaresVisitadosActivity.class));
            } else if ("lugares_favoritos".equals(origen)) {
                startActivity(new Intent(this, LugaresFavoritosActivity.class));
            } else {
                startActivity(new Intent(this, LugaresTuristicosActivity.class));
            }
            finish();
        });

        // Nombre del lugar
        String nombreLugar = getIntent().getStringExtra("nombreLugar");
        if (nombreLugar != null) {
            cargarInformacionLugar(nombreLugar.trim().toLowerCase(Locale.ROOT));
            actualizarTextoBotonVisitado(normalizarNombre(nombreLugar));
            actualizarTextoBotonFavorito(normalizarNombre(nombreLugar));
        }

        btnMarcarVisitado.setOnClickListener(v -> toggleVisitado(nombreLugar));
        btnMarcarFavorito.setOnClickListener(v -> toggleFavorito(nombreLugar));
        iniciarFirebase();
    }

    private void iniciarFirebase() {
        FireBaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void cargarInformacionLugar(String nombreLugar) {
        switch (nombreLugar) {
            case "fuerte lambert":
                imgLugar.setImageResource(R.drawable.fuertelambert);
                txtNombreLugar.setText(getString(R.string.txtfuertelambert));
                txtDescripcion.setText(getString(R.string.desc_fuertelambert));
                txtUbicacion.setText(getString(R.string.ubi_fuertelambert));
                txtHorarios.setText(getString(R.string.hor_fuertelambert));
                txtCostos.setText(getString(R.string.cost_fuertelambert));
                coordenadaX = -29.933971429838568;
                coordenadaY = -71.3360721762996;
                nomMap = "Fuerte Lambert";
                break;

            case "cruz del tercer milenio":
                imgLugar.setImageResource(R.drawable.cruztercermilenio);
                txtNombreLugar.setText(getString(R.string.txtcruzdeltercermilenio));
                txtDescripcion.setText(getString(R.string.desc_cruz));
                txtUbicacion.setText(getString(R.string.ubi_cruz));
                txtHorarios.setText(getString(R.string.hor_cruz));
                txtCostos.setText(getString(R.string.cost_cruz));
                coordenadaX = -29.951469351311008;
                coordenadaY = -71.34737146280611;
                nomMap = "Cruz del Tercer Milenio";
                break;

            case "pueblito peñuelas":
                imgLugar.setImageResource(R.drawable.pueblitopenuelas);
                txtNombreLugar.setText(getString(R.string.txtpueblitopeñuelas));
                txtDescripcion.setText(getString(R.string.desc_pueblito));
                txtUbicacion.setText(getString(R.string.ubi_pueblito));
                txtHorarios.setText(getString(R.string.hor_pueblito));
                txtCostos.setText(getString(R.string.cost_pueblito));
                coordenadaX = -29.948892824;
                coordenadaY = -71.291997180;
                nomMap = "Pueblito Peñuelas";
                break;

            case "avenida del mar":
                imgLugar.setImageResource(R.drawable.avenidadelmar);
                txtNombreLugar.setText(getString(R.string.txtavenidadelmar));
                txtDescripcion.setText(getString(R.string.desc_mar));
                txtUbicacion.setText(getString(R.string.ubi_mar));
                txtHorarios.setText(getString(R.string.hor_mar));
                txtCostos.setText(getString(R.string.cost_mar));
                coordenadaX = -29.915549;
                coordenadaY = -71.275552;
                nomMap = "Avenida del Mar";
                break;

            case "la mezquita":
                imgLugar.setImageResource(R.drawable.lamezquita);
                txtNombreLugar.setText(getString(R.string.txtlamezquita));
                txtDescripcion.setText(getString(R.string.desc_mezquita));
                txtUbicacion.setText(getString(R.string.ubi_mezquita));
                txtHorarios.setText(getString(R.string.hor_mezquita));
                txtCostos.setText(getString(R.string.cost_mezquita));
                coordenadaX = -29.96305556;
                coordenadaY = -71.33541667;
                nomMap = "La Mezquita";
                break;

            case "el faro":
                imgLugar.setImageResource(R.drawable.elfaro);
                txtNombreLugar.setText(getString(R.string.txtelfaro));
                txtDescripcion.setText(getString(R.string.desc_faro));
                txtUbicacion.setText(getString(R.string.ubi_faro));
                txtHorarios.setText(getString(R.string.hor_faro));
                txtCostos.setText(getString(R.string.cost_faro));
                coordenadaX = -29.905579;
                coordenadaY = -71.274209;
                nomMap = "El Faro";
                break;

            case "parque japones":
                imgLugar.setImageResource(R.drawable.parquejapones);
                txtNombreLugar.setText(getString(R.string.txtparquejapones));
                txtDescripcion.setText(getString(R.string.desc_parque));
                txtUbicacion.setText(getString(R.string.ubi_parque));
                txtHorarios.setText(getString(R.string.hor_parque));
                txtCostos.setText(getString(R.string.cost_parque));
                coordenadaX = -29.90388889;
                coordenadaY = -29.90388889;
                nomMap = "Parque Japonés";
                break;

            default:
                imgLugar.setImageResource(android.R.drawable.ic_dialog_alert);
                txtNombreLugar.setText(getString(R.string.app_name));
                txtDescripcion.setText(getString(R.string.no_info));
                txtUbicacion.setText("");
                txtHorarios.setText("");
                txtCostos.setText("");
                nomMap = getString(R.string.app_name);
                break;
        }

        // Actualizar marcador si el mapa ya está listo
        if (gMap != null) {
            LatLng coordenadas = new LatLng(coordenadaX, coordenadaY);
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(coordenadas).title(nomMap));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 13));
        }
    }

    // Normaliza el nombre del lugar (para guardar siempre igual)
    private String normalizarNombre(String nombre) {
        if (nombre == null) return "";
        nombre = nombre.trim().toLowerCase(Locale.ROOT);
        switch (nombre) {
            case "fuerte lambert":
                return "Fuerte Lambert";
            case "cruz del tercer milenio":
                return "Cruz del Tercer Milenio";
            case "pueblito peñuelas":
                return "Pueblito Peñuelas";
            case "avenida del mar":
                return "Avenida del Mar";
            case "la mezquita":
                return "La Mezquita";
            case "el faro":
                return "El Faro";
            case "parque japonés":
            case "parque japones":
                return "Parque Japonés";
            default:
                return nombre;
        }
    }

    private boolean estaVisitado(String nombreLugar) {
        nombreLugar = normalizarNombre(nombreLugar);
        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        return prefs.getStringSet("lugaresVisitados", new HashSet<>()).contains(nombreLugar);
    }

    private void toggleVisitado(String nombreLugar) {
        nombreLugar = normalizarNombre(nombreLugar);
        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        Set<String> visitados = new HashSet<>(prefs.getStringSet("lugaresVisitados", new HashSet<>()));


        if (visitados.contains(nombreLugar)) {
            visitados.remove(nombreLugar);
            Toast.makeText(this, getString(R.string.eliminarVisitado), Toast.LENGTH_SHORT).show();
        } else {
            visitados.add(nombreLugar);
            Toast.makeText(this, getString(R.string.visitado), Toast.LENGTH_SHORT).show();
        }

        prefs.edit().putStringSet("lugaresVisitados", visitados).apply();
        actualizarTextoBotonVisitado(nombreLugar);
    }

    private void actualizarTextoBotonVisitado(String nombreLugar) {
        btnMarcarVisitado.setText(estaVisitado(nombreLugar)
                ? getString(R.string.eliminarVisitado)
                : getString(R.string.visitado));
    }

    private boolean estaFavorito(String nombreLugar) {
        nombreLugar = normalizarNombre(nombreLugar);
        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        return prefs.getStringSet("lugaresFavoritos", new HashSet<>()).contains(nombreLugar);
    }

    private void toggleFavorito(String nombreLugar) {
        nombreLugar = normalizarNombre(nombreLugar);
        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        Set<String> favoritos = new HashSet<>(prefs.getStringSet("lugaresFavoritos", new HashSet<>()));

        if (favoritos.contains(nombreLugar)) {

            favoritos.remove(nombreLugar);
            Toast.makeText(this, getString(R.string.eliminarFavorito), Toast.LENGTH_SHORT).show();
        } else {
            Lugar lugar = new Lugar();
            lugar.setIdLugar(UUID.randomUUID().toString());
            lugar.setNombreLugar(String.valueOf(txtNombreLugar));
            lugar.setDescripcionLugar(String.valueOf(txtDescripcion));
            lugar.setUbicacionLugar(String.valueOf(txtUbicacion));
            lugar.setHorarioLugar(String.valueOf(txtHorarios));
            lugar.setCostoLugar(String.valueOf(txtCostos));
            databaseReference.child("Lugar").child(lugar.getIdLugar()).setValue(lugar);
            favoritos.add(nombreLugar);
            Toast.makeText(this, getString(R.string.favorito), Toast.LENGTH_SHORT).show();
        }

        prefs.edit().putStringSet("lugaresFavoritos", favoritos).apply();
        actualizarTextoBotonFavorito(nombreLugar);
    }

    private void actualizarTextoBotonFavorito(String nombreLugar) {
        btnMarcarFavorito.setText(estaFavorito(nombreLugar)
                ? getString(R.string.eliminarFavorito)
                : getString(R.string.favorito));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng coordenadas = new LatLng(coordenadaX, coordenadaY);
        gMap.addMarker(new MarkerOptions().position(coordenadas).title(nomMap));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 13));
    }
}
