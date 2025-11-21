package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class LugaresTuristicosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Spinner spinnerFiltro;
    private TextInputEditText etBuscarLugar;

    private CardView cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
            cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeHelper.applyTheme(this);

        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefs.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_turisticos);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.lugaresTuristicos));
        }

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

        cardfuertelambert = findViewById(R.id.cardfuertelambert);
        cardcruztercermilenio = findViewById(R.id.cardcruztercermilenio);
        cardpueblitopeñuelas = findViewById(R.id.cardpueblitopeñuelas);
        cardavenidadelmar = findViewById(R.id.cardavenidadelmar);
        cardlamezquita = findViewById(R.id.cardlamezquita);
        cardelfaro = findViewById(R.id.cardelfaro);
        cardparquejapones = findViewById(R.id.cardparquejapones);

        cardfuertelambert.setOnClickListener(v -> abrirInformacionLugar("Fuerte Lambert"));
        cardcruztercermilenio.setOnClickListener(v -> abrirInformacionLugar("Cruz del Tercer Milenio"));
        cardpueblitopeñuelas.setOnClickListener(v -> abrirInformacionLugar("Pueblito Peñuelas"));
        cardavenidadelmar.setOnClickListener(v -> abrirInformacionLugar("Avenida del Mar"));
        cardlamezquita.setOnClickListener(v -> abrirInformacionLugar("La Mezquita"));
        cardelfaro.setOnClickListener(v -> abrirInformacionLugar("El Faro"));
        cardparquejapones.setOnClickListener(v -> abrirInformacionLugar("Parque Japonés"));

        animarToolbarYFiltro();
        animarCards();

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mostrarTodas();
                aplicarFiltro(parent.getItemAtPosition(position).toString());

                String texto = etBuscarLugar.getText() != null
                        ? etBuscarLugar.getText().toString()
                        : "";

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
    }

    private void animarToolbarYFiltro() {

        toolbar.setTranslationY(-100f);
        toolbar.setAlpha(0f);
        toolbar.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        spinnerFiltro.setTranslationY(-40f);
        spinnerFiltro.setAlpha(0f);
        spinnerFiltro.animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay(150)
                .setDuration(400)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        if (etBuscarLugar != null) {
            etBuscarLugar.setAlpha(0f);
            etBuscarLugar.setTranslationY(-20f);
            etBuscarLugar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(220)
                    .setDuration(400)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    private void animarCards() {

        CardView[] cards = {
                cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
                cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones
        };

        float distancia = -200f;
        int index = 0;

        for (CardView card : cards) {
            if (card == null) continue;

            card.setAlpha(1f);
            card.setTranslationX(distancia);
            card.setScaleX(0.90f);
            card.setScaleY(0.90f);

            long delay = index * 150L;

            card.animate()
                    .alpha(1f)
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

    private void abrirInformacionLugar(String nombreLugar) {
        Intent intent = new Intent(this, InformacionLugarActivity.class);
        intent.putExtra("nombreLugar", nombreLugar);
        intent.putExtra("origen", "lugares_turisticos");
        startActivity(intent);
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
            default:
                break;
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

    private void ocultarExcepto(CardView... visibles) {
        CardView[] todas = {
                cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
                cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones
        };
        for (CardView card : todas) if (card != null) card.setVisibility(View.GONE);
        for (CardView card : visibles) if (card != null) card.setVisibility(View.VISIBLE);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        texto = texto.toLowerCase(Locale.ROOT);
        texto = texto
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ü", "u")
                .replace("ñ", "n");
        return texto;
    }

    private void filtrarPorTexto(String texto) {
        String buscado = normalizar(texto.trim());
        if (buscado.isEmpty()) return;

        String nFuerte   = normalizar(getString(R.string.txtfuertelambert));
        String nCruz     = normalizar(getString(R.string.txtcruzdeltercermilenio));
        String nPueblito = normalizar(getString(R.string.txtpueblitopeñuelas));
        String nAvMar    = normalizar(getString(R.string.txtavenidadelmar));
        String nMezquita = normalizar(getString(R.string.txtlamezquita));
        String nFaro     = normalizar(getString(R.string.txtelfaro));
        String nParque   = normalizar(getString(R.string.txtparquejapones));

        if (!nFuerte.contains(buscado))   cardfuertelambert.setVisibility(View.GONE);
        if (!nCruz.contains(buscado))     cardcruztercermilenio.setVisibility(View.GONE);
        if (!nPueblito.contains(buscado)) cardpueblitopeñuelas.setVisibility(View.GONE);
        if (!nAvMar.contains(buscado))    cardavenidadelmar.setVisibility(View.GONE);
        if (!nMezquita.contains(buscado)) cardlamezquita.setVisibility(View.GONE);
        if (!nFaro.contains(buscado))     cardelfaro.setVisibility(View.GONE);
        if (!nParque.contains(buscado))   cardparquejapones.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lugares_visitados) {
            startActivity(new Intent(this, LugaresVisitadosActivity.class));
        } else if (id == R.id.nav_lugares_favoritos) {
            startActivity(new Intent(this, LugaresFavoritosActivity.class));
        } else if (id == R.id.nav_configuracion) {
            startActivity(new Intent(this, ConfiguracionActivity.class));
        } else if (id == R.id.nav_mapa) {
            startActivity(new Intent(this, MapaActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
