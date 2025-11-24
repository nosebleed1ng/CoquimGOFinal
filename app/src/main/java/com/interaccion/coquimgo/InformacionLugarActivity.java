package com.interaccion.coquimgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.interaccion.coquimgo.model.Lugar;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class InformacionLugarActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imgLugar;
    private TextView txtNombreLugar, txtDescripcion, txtUbicacion, txtHorarios, txtCostos, txtTipoLugar;
    private GoogleMap gMap;
    private double coordenadaX;
    private double coordenadaY;
    private String nomMap;
    private Button btnVolver, btnMarcarVisitado, btnMarcarFavorito;

    // Views para animaciones
    private CardView cardImagen, cardDescripcion, cardInfoLugar, cardMapa;
    private View layoutBotonesAccion, layoutContenido;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Tema
        ThemeHelper.applyTheme(this);

        // Idioma guardado
        SharedPreferences prefsConfig = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefsConfig.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_lugar);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Referencias al layout
        imgLugar = findViewById(R.id.imglugar);
        txtNombreLugar = findViewById(R.id.txtnombreLugar);
        txtDescripcion = findViewById(R.id.txtdescripcion);
        txtUbicacion = findViewById(R.id.txtubicacion);
        txtHorarios = findViewById(R.id.txthorarios);
        txtCostos = findViewById(R.id.txtcostos);
        txtTipoLugar = findViewById(R.id.txttipoLugar);

        btnVolver = findViewById(R.id.btnvolver);
        btnMarcarVisitado = findViewById(R.id.btnmarcarvisitado);
        btnMarcarFavorito = findViewById(R.id.btnmarcarfavorito);

        // Views para animar
        cardImagen = findViewById(R.id.cardImagen);
        cardDescripcion = findViewById(R.id.cardDescripcion);
        cardInfoLugar = findViewById(R.id.cardInfoLugar);
        cardMapa = findViewById(R.id.cardMapa);
        layoutBotonesAccion = findViewById(R.id.layoutBotonesAccion);
        layoutContenido = findViewById(R.id.layoutContenido);

        // Textos traducibles
        btnVolver.setText(getString(R.string.volver));
        btnMarcarVisitado.setText(getString(R.string.visitado));
        btnMarcarFavorito.setText(getString(R.string.favorito));

        // Microanimación de botones
        setupButtonPressAnimation(btnVolver);
        setupButtonPressAnimation(btnMarcarVisitado);
        setupButtonPressAnimation(btnMarcarFavorito);

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

        // nombre del lugar
        String nombreLugar = getIntent().getStringExtra("nombreLugar");
        if (nombreLugar != null) {
            cargarInformacionLugar(nombreLugar.trim().toLowerCase(Locale.ROOT));
            actualizarTextoBotonVisitado(normalizarNombre(nombreLugar));
            actualizarTextoBotonFavorito(normalizarNombre(nombreLugar));
        }

        btnMarcarVisitado.setOnClickListener(v -> toggleVisitado(nombreLugar));
        btnMarcarFavorito.setOnClickListener(v -> toggleFavorito(nombreLugar));

        iniciarFirebase();

        // animaciones de entrada del contenido
        prepararAnimacionesIniciales();
        animarEntradaContenido();
    }

    // ----------------- ANIMACIONES -----------------

    private void prepararAnimacionesIniciales() {
        if (layoutContenido != null) {
            layoutContenido.setAlpha(1f);
        }

        if (cardImagen != null) {
            cardImagen.setAlpha(0f);
            cardImagen.setTranslationY(220f);
            cardImagen.setScaleX(0.85f);
            cardImagen.setScaleY(0.85f);
        }

        if (txtNombreLugar != null) {
            txtNombreLugar.setAlpha(0f);
            txtNombreLugar.setTranslationY(180f);
        }

        if (txtTipoLugar != null) {
            txtTipoLugar.setAlpha(0f);
            txtTipoLugar.setTranslationY(180f);
        }

        if (cardDescripcion != null) {
            cardDescripcion.setAlpha(0f);
            cardDescripcion.setTranslationX(260f);
        }

        if (cardInfoLugar != null) {
            cardInfoLugar.setAlpha(0f);
            cardInfoLugar.setTranslationX(260f);
        }

        if (cardMapa != null) {
            cardMapa.setAlpha(0f);
            cardMapa.setScaleX(0.80f);
            cardMapa.setScaleY(0.80f);
            cardMapa.setTranslationY(200f);
        }

        if (layoutBotonesAccion != null) {
            layoutBotonesAccion.setAlpha(0f);
            layoutBotonesAccion.setTranslationY(220f);
        }
        if (btnVolver != null) {
            btnVolver.setAlpha(0f);
            btnVolver.setTranslationY(240f);
        }
    }

    private void animarEntradaContenido() {
        DecelerateInterpolator desacelerar = new DecelerateInterpolator();
        OvershootInterpolator rebote = new OvershootInterpolator(1.1f);

        if (cardImagen != null) {
            cardImagen.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(750)
                    .setStartDelay(80)
                    .setInterpolator(rebote)
                    .start();
        }

        if (txtNombreLugar != null) {
            txtNombreLugar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(650)
                    .setStartDelay(250)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (txtTipoLugar != null) {
            txtTipoLugar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(650)
                    .setStartDelay(320)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (cardDescripcion != null) {
            cardDescripcion.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(700)
                    .setStartDelay(380)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (cardInfoLugar != null) {
            cardInfoLugar.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(700)
                    .setStartDelay(460)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (cardMapa != null) {
            cardMapa.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0f)
                    .setDuration(750)
                    .setStartDelay(540)
                    .setInterpolator(rebote)
                    .start();
        }

        if (layoutBotonesAccion != null) {
            layoutBotonesAccion.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(650)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (btnVolver != null) {
            btnVolver.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(720)
                    .setInterpolator(desacelerar)
                    .start();
        }
    }

    private void setupButtonPressAnimation(Button button) {
        if (button == null) return;

        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    animateScale(v, 0.96f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    animateScale(v, 1f);
                    break;
            }
            return false;
        });
    }

    private void animateScale(View v, float scale) {
        ViewPropertyAnimatorCompat animator = ViewCompat.animate(v);
        animator.scaleX(scale).scaleY(scale).setDuration(120).start();
    }

    // Firebase
    private void iniciarFirebase() {
        FireBaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void guardarLugarEnFirebase(String nombreNormalizado,
                                        boolean esFavorito,
                                        boolean esVisitado) {

        String idLugar = nombreNormalizado.replace(" ", "_");

        Lugar lugar = new Lugar();
        lugar.setIdLugar(idLugar);
        lugar.setNombreLugar(txtNombreLugar.getText().toString());
        lugar.setDescripcionLugar(txtDescripcion.getText().toString());
        lugar.setUbicacionLugar(txtUbicacion.getText().toString());
        lugar.setHorarioLugar(txtHorarios.getText().toString());
        lugar.setCostoLugar(txtCostos.getText().toString());
        lugar.setFavorito(esFavorito);
        lugar.setVisitado(esVisitado);

        databaseReference
                .child("Lugar")
                .child(idLugar)
                .setValue(lugar);
    }

    private void cargarInformacionLugar(String nombreLugar) {
        if (txtTipoLugar != null) {
            txtTipoLugar.setText("");
        }

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

            case "parque japonés":
            case "parque japones":
                imgLugar.setImageResource(R.drawable.parquejapones);
                txtNombreLugar.setText(getString(R.string.txtparquejapones));
                txtDescripcion.setText(getString(R.string.desc_parque));
                txtUbicacion.setText(getString(R.string.ubi_parque));
                txtHorarios.setText(getString(R.string.hor_parque));
                txtCostos.setText(getString(R.string.cost_parque));
                coordenadaX = -29.906503;
                coordenadaY = -71.246194;
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

        if (gMap != null) {
            LatLng coordenadas = new LatLng(coordenadaX, coordenadaY);
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(coordenadas).title(nomMap));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 13));
        }
    }

    // Favoritos y visitados
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
        String nombreNormalizado = normalizarNombre(nombreLugar);

        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        Set<String> visitados = new HashSet<>(prefs.getStringSet("lugaresVisitados", new HashSet<>()));

        boolean estabaVisitado = visitados.contains(nombreNormalizado);
        boolean nuevoEstadoVisitado = !estabaVisitado;

        if (nuevoEstadoVisitado) {
            visitados.add(nombreNormalizado);
            Toast.makeText(this, getString(R.string.visitado), Toast.LENGTH_SHORT).show();
        } else {
            visitados.remove(nombreNormalizado);
            Toast.makeText(this, getString(R.string.eliminarVisitado), Toast.LENGTH_SHORT).show();
        }

        prefs.edit().putStringSet("lugaresVisitados", visitados).apply();
        actualizarTextoBotonVisitado(nombreNormalizado);

        boolean estadoFavoritoActual = estaFavorito(nombreNormalizado);
        guardarLugarEnFirebase(nombreNormalizado, estadoFavoritoActual, nuevoEstadoVisitado);
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
        String nombreNormalizado = normalizarNombre(nombreLugar);

        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        Set<String> favoritos = new HashSet<>(prefs.getStringSet("lugaresFavoritos", new HashSet<>()));

        boolean estabaFavorito = favoritos.contains(nombreNormalizado);
        boolean nuevoEstadoFavorito = !estabaFavorito;

        if (nuevoEstadoFavorito) {
            favoritos.add(nombreNormalizado);
            Toast.makeText(this, getString(R.string.favorito), Toast.LENGTH_SHORT).show();
        } else {
            favoritos.remove(nombreNormalizado);
            Toast.makeText(this, getString(R.string.eliminarFavorito), Toast.LENGTH_SHORT).show();
        }

        prefs.edit().putStringSet("lugaresFavoritos", favoritos).apply();
        actualizarTextoBotonFavorito(nombreNormalizado);

        boolean estadoVisitadoActual = estaVisitado(nombreNormalizado);
        guardarLugarEnFirebase(nombreNormalizado, nuevoEstadoFavorito, estadoVisitadoActual);
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
