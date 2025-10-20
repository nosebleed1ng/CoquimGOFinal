package com.interaccion.coquimgo;

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


public class LugaresTuristicosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Spinner spinnerFiltro;

    private CardView cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
            cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar idioma guardado
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefs.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_turisticos);

        // Configurar Toolbar con título traducible
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.lugaresTuristicos));
        }

        // Configurar Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Inicializar cards
        cardfuertelambert = findViewById(R.id.cardfuertelambert);
        cardcruztercermilenio = findViewById(R.id.cardcruztercermilenio);
        cardpueblitopeñuelas = findViewById(R.id.cardpueblitopeñuelas);
        cardavenidadelmar = findViewById(R.id.cardavenidadelmar);
        cardlamezquita = findViewById(R.id.cardlamezquita);
        cardelfaro = findViewById(R.id.cardelfaro);
        cardparquejapones = findViewById(R.id.cardparquejapones);

        // Clicks en cada card (abre información detallada)
        cardfuertelambert.setOnClickListener(v -> abrirInformacionLugar("Fuerte Lambert"));
        cardcruztercermilenio.setOnClickListener(v -> abrirInformacionLugar("Cruz del tercer milenio"));
        cardpueblitopeñuelas.setOnClickListener(v -> abrirInformacionLugar("Pueblito Peñuelas"));
        cardavenidadelmar.setOnClickListener(v -> abrirInformacionLugar("Avenida del mar"));
        cardlamezquita.setOnClickListener(v -> abrirInformacionLugar("La mezquita"));
        cardelfaro.setOnClickListener(v -> abrirInformacionLugar("El faro"));
        cardparquejapones.setOnClickListener(v -> abrirInformacionLugar("Parque japones"));

        // Configurar filtro
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = parent.getItemAtPosition(position).toString();
                aplicarFiltro(seleccion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    // Método para abrir detalles de un lugar
    private void abrirInformacionLugar(String nombreLugar) {
        Intent intent = new Intent(this, InformacionLugarActivity.class);
        intent.putExtra("nombreLugar", nombreLugar);
        intent.putExtra("origen", "lugares_turisticos");
        startActivity(intent);
    }

    // Aplica el filtro según la categoría seleccionada
    private void aplicarFiltro(String filtro) {
        mostrarTodas();

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

    // Mostrar todas las cards
    private void mostrarTodas() {
        cardfuertelambert.setVisibility(View.VISIBLE);
        cardcruztercermilenio.setVisibility(View.VISIBLE);
        cardpueblitopeñuelas.setVisibility(View.VISIBLE);
        cardavenidadelmar.setVisibility(View.VISIBLE);
        cardlamezquita.setVisibility(View.VISIBLE);
        cardelfaro.setVisibility(View.VISIBLE);
        cardparquejapones.setVisibility(View.VISIBLE);
    }

    // Ocultar todas excepto las seleccionadas
    private void ocultarExcepto(CardView... visibles) {
        CardView[] todas = {
                cardfuertelambert, cardcruztercermilenio, cardpueblitopeñuelas,
                cardavenidadelmar, cardlamezquita, cardelfaro, cardparquejapones
        };
        for (CardView card : todas) card.setVisibility(View.GONE);
        for (CardView card : visibles) card.setVisibility(View.VISIBLE);
    }

    // Menú lateral (Drawer)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lugares_visitados) {
            startActivity(new Intent(this, LugaresVisitadosActivity.class));
        } else if (id == R.id.nav_lugares_favoritos) {
            startActivity(new Intent(this, LugaresFavoritosActivity.class));
        } else if (id == R.id.nav_configuracion) {
            startActivity(new Intent(this, ConfiguracionActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
