package com.interaccion.coquimgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LugaresVisitadosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Spinner spinnerFiltro;
    private TextInputEditText etBuscarLugar;

    private CardView cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
            cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones;

    private ImageView lockFuerte, lockCruz, lockPueblito,
            lockAvMar, lockMezquita, lockFaro, lockParque;

    private Set<String> lugaresVisitados = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeHelper.applyTheme(this);

        SharedPreferences prefsConfig = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefsConfig.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_visitados);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.lugaresVisitados));

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        etBuscarLugar = findViewById(R.id.etBuscarLugar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Cards
        cardfuertelambert = findViewById(R.id.cardfuertelambert);
        cardcruztercermilenio = findViewById(R.id.cardcruztercermilenio);
        cardpueblitopeñuelas = findViewById(R.id.cardpueblitopeñuelas);
        cardavenidadelmar = findViewById(R.id.cardavenidadelmar);
        cardlamezquita = findViewById(R.id.cardlamezquita);
        cardelfaro = findViewById(R.id.cardelfaro);
        cardparquejapones = findViewById(R.id.cardparquejapones);

        // Locks
        lockFuerte   = findViewById(R.id.lock_fuertelambert);
        lockCruz     = findViewById(R.id.lock_cruztercermilenio);
        lockPueblito = findViewById(R.id.lock_pueblitopeñuelas);
        lockAvMar    = findViewById(R.id.lock_avenidadelmar);
        lockMezquita = findViewById(R.id.lock_lamezquita);
        lockFaro     = findViewById(R.id.lock_elfaro);
        lockParque   = findViewById(R.id.lock_parquejapones);

        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        lugaresVisitados = prefs.getStringSet("lugaresVisitados", new HashSet<>());

        mostrarTodas();
        actualizarEstadoVisitados();

        animarToolbarYFiltro();
        animarCards();

        // ⭐ CORREGIDO: position → int
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = parent.getItemAtPosition(position).toString();
                mostrarTodas();
                aplicarFiltro(seleccion);
                String texto = etBuscarLugar.getText() != null ? etBuscarLugar.getText().toString() : "";
                filtrarPorTexto(texto);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        etBuscarLugar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString();
                String filtroActual = spinnerFiltro.getSelectedItem() != null
                        ? spinnerFiltro.getSelectedItem().toString()
                        : "";
                mostrarTodas();
                aplicarFiltro(filtroActual);
                filtrarPorTexto(texto);
            }
        });

        cardfuertelambert.setOnClickListener(v -> abrirInformacionLugar("Fuerte Lambert"));
        cardcruztercermilenio.setOnClickListener(v -> abrirInformacionLugar("Cruz del Tercer Milenio"));
        cardpueblitopeñuelas.setOnClickListener(v -> abrirInformacionLugar("Pueblito Peñuelas"));
        cardavenidadelmar.setOnClickListener(v -> abrirInformacionLugar("Avenida del Mar"));
        cardlamezquita.setOnClickListener(v -> abrirInformacionLugar("La Mezquita"));
        cardelfaro.setOnClickListener(v -> abrirInformacionLugar("El Faro"));
        cardparquejapones.setOnClickListener(v -> abrirInformacionLugar("Parque Japonés"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("LugaresPrefs", Context.MODE_PRIVATE);
        lugaresVisitados = prefs.getStringSet("lugaresVisitados", new HashSet<>());

        mostrarTodas();
        actualizarEstadoVisitados();

        String texto = etBuscarLugar.getText() != null ? etBuscarLugar.getText().toString() : "";
        String filtroActual = spinnerFiltro.getSelectedItem() != null
                ? spinnerFiltro.getSelectedItem().toString()
                : "";
        aplicarFiltro(filtroActual);
        filtrarPorTexto(texto);
    }

    private void animarToolbarYFiltro() {
        toolbar.setTranslationY(-100f);
        toolbar.setAlpha(0f);
        toolbar.animate().translationY(0f).alpha(1f).setDuration(400)
                .setInterpolator(new DecelerateInterpolator()).start();

        spinnerFiltro.setTranslationY(-40f);
        spinnerFiltro.setAlpha(0f);
        spinnerFiltro.animate().translationY(0f).alpha(1f).setStartDelay(150)
                .setDuration(400).setInterpolator(new DecelerateInterpolator()).start();

        etBuscarLugar.setAlpha(0f);
        etBuscarLugar.setTranslationY(-20f);
        etBuscarLugar.animate().alpha(1f).translationY(0f).setStartDelay(220)
                .setDuration(400).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void animarCards() {
        CardView[] cards = {
                cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
                cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones
        };

        float distancia = -200f;
        int index = 0;

        for (CardView card : cards) {
            if (card == null || card.getVisibility() != View.VISIBLE) continue;

            card.setAlpha(card.getAlpha());
            card.setTranslationX(distancia);
            card.setScaleX(0.9f);
            card.setScaleY(0.9f);

            long delay = index * 150L;

            card.animate()
                    .translationX(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(delay)
                    .setDuration(500)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            index++;
        }
    }

    private void actualizarEstadoVisitados() {
        aplicarEstadoLugar(cardfuertelambert, lockFuerte,
                lugaresVisitados.contains("Fuerte Lambert"));
        aplicarEstadoLugar(cardcruztercermilenio, lockCruz,
                lugaresVisitados.contains("Cruz del Tercer Milenio"));
        aplicarEstadoLugar(cardpueblitopeñuelas, lockPueblito,
                lugaresVisitados.contains("Pueblito Peñuelas"));
        aplicarEstadoLugar(cardavenidadelmar, lockAvMar,
                lugaresVisitados.contains("Avenida del Mar"));
        aplicarEstadoLugar(cardlamezquita, lockMezquita,
                lugaresVisitados.contains("La Mezquita"));
        aplicarEstadoLugar(cardelfaro, lockFaro,
                lugaresVisitados.contains("El Faro"));
        aplicarEstadoLugar(cardparquejapones, lockParque,
                lugaresVisitados.contains("Parque Japonés"));
    }

    private void aplicarEstadoLugar(CardView card, ImageView lock, boolean esVisitado) {
        if (esVisitado) {
            card.setAlpha(1f);
            lock.setVisibility(View.GONE);
        } else {
            card.setAlpha(0.45f);
            lock.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarTodas() {
        cardfuertelambert.setVisibility(View.VISIBLE);
        cardcruztercermilenio.setVisibility(View.VISIBLE);
        cardpueblitopeñuelas.setVisibility(View.VISIBLE);
        cardavenidadelmar.setVisibility(View.VISIBLE);
        cardlamezquita.setVisibility(View.VISIBLE);
        cardelfaro.setVisibility(View.VISIBLE);
        cardparquejapones.setVisibility(View.VISIBLE);
    }

    private void aplicarFiltro(String filtro) {
        switch (filtro) {
            case "Playa":
                ocultarExcepto(cardavenidadelmar, cardelfaro);
                break;
            case "Cultural":
                ocultarExcepto(cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas);
                break;
            case "Religioso":
                ocultarExcepto(cardlamezquita);
                break;
            case "Parques":
                ocultarExcepto(cardparquejapones);
                break;
        }
    }

    private void ocultarExcepto(CardView... visibles) {
        CardView[] todas = {
                cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
                cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones
        };
        for (CardView card : todas) card.setVisibility(View.GONE);
        for (CardView card : visibles) card.setVisibility(View.VISIBLE);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        return texto.toLowerCase(Locale.ROOT)
                .replace("á","a").replace("é","e").replace("í","i")
                .replace("ó","o").replace("ú","u").replace("ü","u")
                .replace("ñ","n");
    }

    private void filtrarPorTexto(String texto) {
        String buscado = normalizar(texto.trim());
        if (buscado.isEmpty()) return;

        if (!normalizar(getString(R.string.txtfuertelambert)).contains(buscado))
            cardfuertelambert.setVisibility(View.GONE);

        if (!normalizar(getString(R.string.txtcruzdeltercermilenio)).contains(buscado))
            cardcruztercermilenio.setVisibility(View.GONE);

        if (!normalizar(getString(R.string.txtpueblitopeñuelas)).contains(buscado))
            cardpueblitopeñuelas.setVisibility(View.GONE);

        if (!normalizar(getString(R.string.txtavenidadelmar)).contains(buscado))
            cardavenidadelmar.setVisibility(View.GONE);

        if (!normalizar(getString(R.string.txtlamezquita)).contains(buscado))
            cardlamezquita.setVisibility(View.GONE);

        if (!normalizar(getString(R.string.txtelfaro)).contains(buscado))
            cardelfaro.setVisibility(View.GONE);

        if (!normalizar(getString(R.string.txtparquejapones)).contains(buscado))
            cardparquejapones.setVisibility(View.GONE);
    }

    private void abrirInformacionLugar(String nombreLugar) {
        Intent i = new Intent(this, InformacionLugarActivity.class);
        i.putExtra("nombreLugar", nombreLugar);
        i.putExtra("origen", "lugares_visitados");
        startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_lugares_turisticos)
            startActivity(new Intent(this, LugaresTuristicosActivity.class));
        else if (id == R.id.nav_lugares_favoritos)
            startActivity(new Intent(this, LugaresFavoritosActivity.class));
        else if (id == R.id.nav_configuracion)
            startActivity(new Intent(this, ConfiguracionActivity.class));
        else if (id == R.id.nav_mapa)
            startActivity(new Intent(this, MapaActivity.class));

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
