package com.interaccion.coquimgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.interaccion.coquimgo.db.DbLugar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class InformacionLugarActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imgLugar;
    private TextView txtNombreLugar, txtDescripcion, txtUbicacion, txtHorarios, txtCostos, txtTipoLugar;
    private TextView txtRatingGlobal;
    private GoogleMap gMap;
    private double coordenadaX;
    private double coordenadaY;
    private String nomMap;

    private Button btnVolver, btnMarcarVisitado, btnMarcarFavorito, btnCalificar;

    // RatingBar
    private RatingBar ratingBarLugar;

    // Views para animaciones
    private CardView cardImagen, cardDescripcion, cardInfoLugar, cardMapa, cardRating;
    private View layoutBotonesAccion, layoutContenido;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private static final String USUARIO_ID = "usuario1";

    // <<< NUEVO: guardamos el idioma actual >>>
    private String idiomaActual = "es";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Tema
        ThemeHelper.applyTheme(this);

        // Idioma guardado
        SharedPreferences prefsConfig = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefsConfig.getString("idioma", "es");
        idiomaActual = idioma; // lo guardamos en el campo para usarlo en toda la clase
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_lugar);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        iniciarFirebase();

        // Referencias al layout
        imgLugar = findViewById(R.id.imglugar);
        txtNombreLugar = findViewById(R.id.txtnombreLugar);
        txtDescripcion = findViewById(R.id.txtdescripcion);
        txtUbicacion = findViewById(R.id.txtubicacion);
        txtHorarios = findViewById(R.id.txthorarios);
        txtCostos = findViewById(R.id.txtcostos);
        txtTipoLugar = findViewById(R.id.txttipoLugar);
        txtRatingGlobal = findViewById(R.id.txtRatingGlobal);

        btnVolver = findViewById(R.id.btnvolver);
        btnMarcarVisitado = findViewById(R.id.btnmarcarvisitado);
        btnMarcarFavorito = findViewById(R.id.btnmarcarfavorito);
        btnCalificar = findViewById(R.id.btnCalificar);

        // RatingBar
        ratingBarLugar = findViewById(R.id.ratingBarVisita);

        // Views para animar
        cardImagen = findViewById(R.id.cardImagen);
        cardDescripcion = findViewById(R.id.cardDescripcion);
        cardInfoLugar = findViewById(R.id.cardInfoLugar);
        cardMapa = findViewById(R.id.cardMapa);
        cardRating = findViewById(R.id.cardRating);
        layoutBotonesAccion = findViewById(R.id.layoutBotonesAccion);
        layoutContenido = findViewById(R.id.layoutContenido);

        // Textos traducibles
        btnVolver.setText(getString(R.string.volver));
        btnMarcarVisitado.setText(getString(R.string.visitado));
        btnMarcarFavorito.setText(getString(R.string.favorito));
        btnCalificar.setText(getString(R.string.calificar));

        // Microanimación de botones
        setupButtonPressAnimation(btnVolver);
        setupButtonPressAnimation(btnMarcarVisitado);
        setupButtonPressAnimation(btnMarcarFavorito);
        setupButtonPressAnimation(btnCalificar);

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

        // Nombre del lugar desde el Intent
        String nombreLugarIntent = getIntent().getStringExtra("nombreLugar");
        if (nombreLugarIntent != null) {
            //Carga base desde recursos (LOCALIZADOS por idioma)
            cargarInformacionLugar(nombreLugarIntent);

            String nombreNormalizadoBonito = normalizarNombre(nombreLugarIntent);
            actualizarTextoBotonVisitado(nombreNormalizadoBonito);
            actualizarTextoBotonFavorito(nombreNormalizadoBonito);

            //Cargar rating global desde Firebase
            String idLugar = nombreNormalizadoBonito.replace(" ", "_");
            cargarRatingGlobalDesdeFirebase(idLugar);
        }

        btnMarcarVisitado.setOnClickListener(v -> toggleVisitado());
        btnMarcarFavorito.setOnClickListener(v -> toggleFavorito());
        btnCalificar.setOnClickListener(v -> enviarCalificacion());

        // Animaciones de entrada del contenido
        prepararAnimacionesIniciales();
        animarEntradaContenido();
    }

    // ANIMACIONES

    private void prepararAnimacionesIniciales() {
        if (layoutContenido != null) layoutContenido.setAlpha(1f);

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

        if (cardRating != null) {
            cardRating.setAlpha(0f);
            cardRating.setTranslationY(200f);
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

        if (cardRating != null) {
            cardRating.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(650)
                    .setStartDelay(620)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (layoutBotonesAccion != null) {
            layoutBotonesAccion.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(700)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (btnVolver != null) {
            btnVolver.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(760)
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

    //FIREBASE

    private void iniciarFirebase() {
        try {
            FireBaseApp.initializeApp(this);
        } catch (Exception e) {
            // por si ya estaba inicializado
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /**
     * Guarda info del lugar en /lugaresTuristicos/idLugar
     * Solo sube textos descriptivos si el idioma es español ("es"),
     * para no llenar Firebase con descripciones en otros idiomas.
     */
    private void guardarLugarEnFirebase(String nombreNormalizado,
                                        boolean esFavorito,
                                        boolean esVisitado) {

        String idLugar = nombreNormalizado.replace(" ", "_");

        Map<String, Object> datos = new HashMap<>();
        datos.put("idLugar", idLugar);
        datos.put("nombreLugar", txtNombreLugar.getText().toString());
        datos.put("favorito", esFavorito);
        datos.put("visitado", esVisitado);

        // <<< SOLO si estamos en español, subimos descripción/horario/etc >>>
        if ("es".equals(idiomaActual)) {
            datos.put("descripcionLugar", txtDescripcion.getText().toString());
            datos.put("ubicacionLugar", txtUbicacion.getText().toString());
            datos.put("horarioLugar", txtHorarios.getText().toString());
            datos.put("costoLugar", txtCostos.getText().toString());
        }

        databaseReference
                .child("lugaresTuristicos")
                .child(idLugar)
                .updateChildren(datos);

        DbLugar dbLugar = new DbLugar(InformacionLugarActivity.this);

        if (dbLugar.existeLugarPorId(idLugar)) {
            dbLugar.actualizarLugar(
                    idLugar,
                    (String) datos.get("nombreLugar"),
                    "es".equals(idiomaActual) ? (String) datos.get("descripcionLugar") : txtDescripcion.getText().toString(),
                    "es".equals(idiomaActual) ? (String) datos.get("horarioLugar")     : txtHorarios.getText().toString(),
                    "es".equals(idiomaActual) ? (String) datos.get("ubicacionLugar")   : txtUbicacion.getText().toString(),
                    "es".equals(idiomaActual) ? (String) datos.get("costoLugar")       : txtCostos.getText().toString(),
                    esFavorito,
                    esVisitado
            );
        } else {
            dbLugar.insertarLugar(
                    idLugar,
                    (String) datos.get("nombreLugar"),
                    txtDescripcion.getText().toString(),
                    txtHorarios.getText().toString(),
                    txtUbicacion.getText().toString(),
                    txtCostos.getText().toString(),
                    esFavorito,
                    esVisitado
            );
        }
    }

    // CARGAR INFORMACIÓN DEL LUGAR

    private void cargarInformacionLugar(String nombreLugarIntent) {
        if (nombreLugarIntent == null) return;

        String nombreLower = nombreLugarIntent.trim().toLowerCase(Locale.ROOT);

        // 1) SIEMPRE: cargar desde recursos (localizados por idioma)
        cargarDesdeRecursos(nombreLower);

        // 2) SOLO ESPAÑOL: sobreescribir con lo de Firebase si existe
        if (!"es".equals(idiomaActual)) {
            // En inglés/portugués usamos solo strings.xml,
            // así el cambio de idioma funciona perfecto.
            return;
        }

        String nombreNormalizadoBonito = normalizarNombre(nombreLugarIntent);
        String idLugar = nombreNormalizadoBonito.replace(" ", "_");

        DatabaseReference lugarRef = databaseReference
                .child("lugaresTuristicos")
                .child(idLugar);

        lugarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String desc = snapshot.child("descripcionLugar").getValue(String.class);
                    String ubi  = snapshot.child("ubicacionLugar").getValue(String.class);
                    String hor  = snapshot.child("horarioLugar").getValue(String.class);
                    String cos  = snapshot.child("costoLugar").getValue(String.class);

                    if (desc != null && !desc.isEmpty()) {
                        txtDescripcion.setText(desc);
                    }
                    if (ubi != null && !ubi.isEmpty()) {
                        txtUbicacion.setText(ubi);
                    }
                    if (hor != null && !hor.isEmpty()) {
                        txtHorarios.setText(hor);
                    }
                    if (cos != null && !cos.isEmpty()) {
                        txtCostos.setText(cos);
                    }

                } else {
                    // Si no existe el nodo, guardamos los datos locales una vez (solo en español)
                    boolean esFav = estaFavorito(nombreNormalizadoBonito);
                    boolean esVis = estaVisitado(nombreNormalizadoBonito);
                    guardarLugarEnFirebase(nombreNormalizadoBonito, esFav, esVis);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LUGAR_FIREBASE", "Error al leer datos del lugar: " + error.getMessage());
            }
        });
    }

    private void cargarDesdeRecursos(String nombreLugar) {
        if (txtTipoLugar != null) txtTipoLugar.setText("");

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

    private boolean estaFavorito(String nombreLugar) {
        nombreLugar = normalizarNombre(nombreLugar);
        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        return prefs.getStringSet("lugaresFavoritos", new HashSet<>()).contains(nombreLugar);
    }

    private void toggleVisitado() {
        String nombreEnPantalla = txtNombreLugar.getText().toString();
        String nombreNormalizado = normalizarNombre(nombreEnPantalla);
        String idLugar = nombreNormalizado.replace(" ", "_");

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

        // Estado actual de favorito
        boolean estadoFavoritoActual = estaFavorito(nombreNormalizado);
        guardarLugarEnFirebase(nombreNormalizado, estadoFavoritoActual, nuevoEstadoVisitado);

        DbLugar dbLugar = new DbLugar(this);

        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        int ratingEntero = 0;
        if (ratingBarLugar != null) {
            ratingEntero = Math.round(ratingBarLugar.getRating());
        }

        // SQLite
        dbLugar.actualizarVisitado(USUARIO_ID, idLugar, nuevoEstadoVisitado, fechaHoy, ratingEntero);
        DatabaseReference visRef = databaseReference
                .child("visitados")
                .child(USUARIO_ID)
                .child(idLugar);

        if (nuevoEstadoVisitado) {
            Map<String, Object> datos = new HashMap<>();
            datos.put("fecha", fechaHoy);
            datos.put("rating", ratingEntero);
            visRef.setValue(datos);
        } else {
            visRef.removeValue();
        }

        // Recalcular promedio global solo si hay algún rating
        if (ratingBarLugar != null && ratingBarLugar.getRating() > 0f) {
            recalcularRatingGlobal(idLugar);
        }
        debugMostrarResumenSqlite();
    }

    private void actualizarTextoBotonVisitado(String nombreLugar) {
        btnMarcarVisitado.setText(estaVisitado(nombreLugar)
                ? getString(R.string.eliminarVisitado)
                : getString(R.string.visitado));
    }

    private void toggleFavorito() {
        String nombreEnPantalla = txtNombreLugar.getText().toString();
        String nombreNormalizado = normalizarNombre(nombreEnPantalla);
        String idLugar = nombreNormalizado.replace(" ", "_");

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

        // Guarda en Firebase y tabla lugar
        guardarLugarEnFirebase(nombreNormalizado, nuevoEstadoFavorito, estadoVisitadoActual);

        //Tabla favoritos  SQLite
        DbLugar dbLugar = new DbLugar(this);
        dbLugar.actualizarFavorito(USUARIO_ID, idLugar, nuevoEstadoFavorito);

        debugMostrarResumenSqlite();
    }

    private void actualizarTextoBotonFavorito(String nombreLugar) {
        btnMarcarFavorito.setText(estaFavorito(nombreLugar)
                ? getString(R.string.eliminarFavorito)
                : getString(R.string.favorito));
    }

    // Botón calificar
    private void enviarCalificacion() {
        if (ratingBarLugar == null) return;

        float rating = ratingBarLugar.getRating();

        if (rating <= 0f) {
            Toast.makeText(this, getString(R.string.selecciona_rating), Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreEnPantalla = txtNombreLugar.getText().toString();
        String nombreNormalizado = normalizarNombre(nombreEnPantalla);
        String idLugar = nombreNormalizado.replace(" ", "_");

        // Solo permitir calificar si está marcado como visitado
        if (!estaVisitado(nombreNormalizado)) {
            Toast.makeText(this, getString(R.string.debes_visitar_para_calificar), Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        int ratingEntero = Math.round(rating);

        DbLugar dbLugar = new DbLugar(this);
        dbLugar.actualizarVisitado(USUARIO_ID, idLugar, true, fechaHoy, ratingEntero);

        DatabaseReference visRef = databaseReference
                .child("visitados")
                .child(USUARIO_ID)
                .child(idLugar);

        Map<String, Object> datos = new HashMap<>();
        datos.put("fecha", fechaHoy);
        datos.put("rating", ratingEntero);
        visRef.setValue(datos);

        //Recalcular rating global
        recalcularRatingGlobal(idLugar);

        Toast.makeText(this, getString(R.string.gracias_por_calificar), Toast.LENGTH_SHORT).show();

        debugMostrarResumenSqlite();
    }

    //Cargar rating global inicial desde Firebase
    private void cargarRatingGlobalDesdeFirebase(String idLugar) {
        DatabaseReference lugarRef = databaseReference
                .child("lugaresTuristicos")
                .child(idLugar);

        lugarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Float ratingGlobal = snapshot.child("ratingGlobal").getValue(Float.class);
                Long ratingCount = snapshot.child("ratingCount").getValue(Long.class);

                float promedio = ratingGlobal != null ? ratingGlobal : 0f;
                long count = ratingCount != null ? ratingCount : 0L;

                if (ratingBarLugar != null) ratingBarLugar.setRating(promedio);

                if (txtRatingGlobal != null) {
                    String texto = "Rating global: " + promedio + " / 5 (" + count + " opiniones)";
                    txtRatingGlobal.setText(texto);
                }

                Log.d("RATING_GLOBAL", "Lugar " + idLugar +
                        " -> ratingGlobal=" + promedio +
                        " count=" + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RATING_GLOBAL", "Error al leer rating global: " + error.getMessage());
            }
        });
    }

    //Recalcular promedio global
    private void recalcularRatingGlobal(String idLugar) {

        if (databaseReference == null) {
            Log.e("RATING_GLOBAL", "databaseReference es null, no se puede recalcular");
            return;
        }
        if (idLugar == null || idLugar.trim().isEmpty()) {
            Log.e("RATING_GLOBAL", "idLugar vacío, no se puede recalcular");
            return;
        }

        DatabaseReference visitadosRef = databaseReference.child("visitados");

        visitadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    float suma = 0f;
                    int count = 0;

                    // Recorre todos los usuarios y sus lugares visitados
                    for (DataSnapshot usuarioSnap : snapshot.getChildren()) {
                        for (DataSnapshot lugarSnap : usuarioSnap.getChildren()) {
                            String keyLugar = lugarSnap.getKey();
                            if (keyLugar != null && keyLugar.equals(idLugar)) {

                                Long rating = lugarSnap.child("rating").getValue(Long.class);
                                if (rating != null) {
                                    suma += rating;
                                    count++;
                                }
                            }
                        }
                    }

                    float promedio = (count > 0) ? (suma / count) : 0f;

                    // Actualizar nodo
                    DatabaseReference lugarRef = databaseReference
                            .child("lugaresTuristicos")
                            .child(idLugar);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("ratingGlobal", promedio);
                    updates.put("ratingCount", count);
                    lugarRef.updateChildren(updates);

                    // Actualizar SQLite
                    try {
                        DbLugar dbLugar = new DbLugar(InformacionLugarActivity.this);
                        dbLugar.actualizarRatingGlobal(idLugar, promedio, count);
                    } catch (Exception e) {
                        Log.e("RATING_GLOBAL", "Error actualizando rating en SQLite", e);
                    }

                    // Actualizar RatingBar y texto
                    if (ratingBarLugar != null) {
                        ratingBarLugar.setRating(promedio);
                    }

                    if (txtRatingGlobal != null) {
                        String texto = "Rating global: " + promedio + " / 5 (" + count + " opiniones)";
                        txtRatingGlobal.setText(texto);
                    }

                    Log.d("RATING_GLOBAL",
                            "Recalc " + idLugar + " -> promedio=" + promedio + " count=" + count);

                } catch (Exception e) {
                    Log.e("RATING_GLOBAL", "Excepción en onDataChange al recalcular rating", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RATING_GLOBAL", "Error al recalcular rating global: " + error.getMessage());
            }
        });
    }

    private void debugMostrarResumenSqlite() {
        DbLugar dbLugar = new DbLugar(this);

        int totalFav = dbLugar.contarFavoritos(USUARIO_ID);
        int totalVis = dbLugar.contarVisitados(USUARIO_ID);

        String msg = "SQLite -> Favoritos: " + totalFav + " | Visitados: " + totalVis;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.d("DEBUG_SQLITE", msg);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng coordenadas = new LatLng(coordenadaX, coordenadaY);
        gMap.addMarker(new MarkerOptions().position(coordenadas).title(nomMap));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 13));
    }
}
