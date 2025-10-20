package com.interaccion.coquimgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

/**
 * Activity que muestra los lugares marcados como favoritos.
 * - Idioma dinámico (LocaleHelper)
 * - Toolbar traducible
 * - Filtrado funcional
 * - Carga dinámica desde SharedPreferences
 */
public class LugaresFavoritosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Spinner spinnerFiltro;

    private CardView cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
            cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones;

    private Set<String> lugaresFavoritos = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar idioma guardado
        SharedPreferences prefsConfig = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefsConfig.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_favoritos);

        // Toolbar traducible
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.lugaresFavoritos));

        // Drawer Navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Referencias a Cards
        cardfuertelambert = findViewById(R.id.cardfuertelambert);
        cardcruztercermilenio = findViewById(R.id.cardcruztercermilenio);
        cardpueblitopeñuelas = findViewById(R.id.cardpueblitopeñuelas);
        cardavenidadelmar = findViewById(R.id.cardavenidadelmar);
        cardlamezquita = findViewById(R.id.cardlamezquita);
        cardelfaro = findViewById(R.id.cardelfaro);
        cardparquejapones = findViewById(R.id.cardparquejapones);

        // Cargar lugares favoritos guardados
        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        lugaresFavoritos = prefs.getStringSet("lugaresFavoritos", new HashSet<>());

        actualizarLugaresFavoritos();

        // Filtrar por categoría
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = parent.getItemAtPosition(position).toString();
                aplicarFiltro(seleccion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Clicks de cada lugar
        cardfuertelambert.setOnClickListener(v -> abrirInformacionLugar("Fuerte Lambert"));
        cardcruztercermilenio.setOnClickListener(v -> abrirInformacionLugar("Cruz del Tercer Milenio"));
        cardpueblitopeñuelas.setOnClickListener(v -> abrirInformacionLugar("Pueblito Peñuelas"));
        cardavenidadelmar.setOnClickListener(v -> abrirInformacionLugar("Avenida del Mar"));
        cardlamezquita.setOnClickListener(v -> abrirInformacionLugar("La Mezquita"));
        cardelfaro.setOnClickListener(v -> abrirInformacionLugar("El Faro"));
        cardparquejapones.setOnClickListener(v -> abrirInformacionLugar("Parque Japonés"));
    }

    private void actualizarLugaresFavoritos() {
        cardfuertelambert.setVisibility(lugaresFavoritos.contains("Fuerte Lambert") ? View.VISIBLE : View.GONE);
        cardcruztercermilenio.setVisibility(lugaresFavoritos.contains("Cruz del Tercer Milenio") ? View.VISIBLE : View.GONE);
        cardpueblitopeñuelas.setVisibility(lugaresFavoritos.contains("Pueblito Peñuelas") ? View.VISIBLE : View.GONE);
        cardavenidadelmar.setVisibility(lugaresFavoritos.contains("Avenida del Mar") ? View.VISIBLE : View.GONE);
        cardlamezquita.setVisibility(lugaresFavoritos.contains("La Mezquita") ? View.VISIBLE : View.GONE);
        cardelfaro.setVisibility(lugaresFavoritos.contains("El Faro") ? View.VISIBLE : View.GONE);
        cardparquejapones.setVisibility(lugaresFavoritos.contains("Parque Japonés") ? View.VISIBLE : View.GONE);
    }

    private void aplicarFiltro(String filtro) {
        actualizarLugaresFavoritos();

        switch (filtro) {
            case "Playa":
                ocultarExcepto("Avenida del Mar", "El Faro");
                break;
            case "Cultural":
                ocultarExcepto("Fuerte Lambert", "Cruz del Tercer Milenio", "Pueblito Peñuelas");
                break;
            case "Religioso":
                ocultarExcepto("La Mezquita");
                break;
            case "Parques":
                ocultarExcepto("Parque Japonés");
                break;
        }
    }

    private void ocultarExcepto(String... visibles) {
        Set<String> visiblesSet = new HashSet<>();
        for (String n : visibles) visiblesSet.add(n);

        if (!visiblesSet.contains("Fuerte Lambert")) cardfuertelambert.setVisibility(View.GONE);
        if (!visiblesSet.contains("Cruz del Tercer Milenio")) cardcruztercermilenio.setVisibility(View.GONE);
        if (!visiblesSet.contains("Pueblito Peñuelas")) cardpueblitopeñuelas.setVisibility(View.GONE);
        if (!visiblesSet.contains("Avenida del Mar")) cardavenidadelmar.setVisibility(View.GONE);
        if (!visiblesSet.contains("La Mezquita")) cardlamezquita.setVisibility(View.GONE);
        if (!visiblesSet.contains("El Faro")) cardelfaro.setVisibility(View.GONE);
        if (!visiblesSet.contains("Parque Japonés")) cardparquejapones.setVisibility(View.GONE);
    }

    private void abrirInformacionLugar(String nombreLugar) {
        Intent i = new Intent(this, InformacionLugarActivity.class);
        i.putExtra("nombreLugar", nombreLugar);
        i.putExtra("origen", "lugares_favoritos");
        startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lugares_turisticos)
            startActivity(new Intent(this, LugaresTuristicosActivity.class));
        else if (id == R.id.nav_lugares_visitados)
            startActivity(new Intent(this, LugaresVisitadosActivity.class));
        else if (id == R.id.nav_configuracion)
            startActivity(new Intent(this, ConfiguracionActivity.class));

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
