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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LugaresTuristicosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Spinner spinnerFiltro;
    private TextInputEditText etBuscarLugar;
    private RecyclerView rvLugares;

    private final List<LugarItem> listaCompleta = new ArrayList<>();
    private final List<LugarItem> listaFiltrada = new ArrayList<>();
    private LugaresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Tema
        ThemeHelper.applyTheme(this);

        // Idioma
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefs.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares_turisticos);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.lugaresTuristicos));
        }

        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        spinnerFiltro  = findViewById(R.id.spinnerFiltro);
        etBuscarLugar  = findViewById(R.id.etBuscarLugar);
        rvLugares      = findViewById(R.id.rvLugares);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // RecyclerView
        rvLugares.setLayoutManager(new LinearLayoutManager(this));
        rvLugares.setHasFixedSize(true);

        cargarListaCompleta();
        listaFiltrada.addAll(listaCompleta);

        adapter = new LugaresAdapter(listaFiltrada, item -> {
            abrirInformacionLugar(item.getNombreIntent());
        });
        rvLugares.setAdapter(adapter);

        // Animación general de entrada
        rvLugares.setAlpha(0f);
        rvLugares.setTranslationY(50f);
        rvLugares.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Filtro por categoría
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltrosActuales();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Búsqueda por texto
        etBuscarLugar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                aplicarFiltrosActuales();
            }
        });
    }

    private void cargarListaCompleta() {
        listaCompleta.clear();

        listaCompleta.add(new LugarItem(
                "Fuerte Lambert",
                R.string.txtfuertelambert,
                R.drawable.fuertelambert,
                "Cultural"
        ));

        listaCompleta.add(new LugarItem(
                "Cruz del Tercer Milenio",
                R.string.txtcruzdeltercermilenio,
                R.drawable.cruztercermilenio,
                "Cultural"
        ));

        listaCompleta.add(new LugarItem(
                "Pueblito Peñuelas",
                R.string.txtpueblitopeñuelas,
                R.drawable.pueblitopenuelas,
                "Cultural"
        ));

        listaCompleta.add(new LugarItem(
                "Avenida del Mar",
                R.string.txtavenidadelmar,
                R.drawable.avenidadelmar,
                "Playa"
        ));

        listaCompleta.add(new LugarItem(
                "La Mezquita",
                R.string.txtlamezquita,
                R.drawable.lamezquita,
                "Religioso"
        ));

        listaCompleta.add(new LugarItem(
                "El Faro",
                R.string.txtelfaro,
                R.drawable.elfaro,
                "Playa"
        ));

        listaCompleta.add(new LugarItem(
                "Parque Japonés",
                R.string.txtparquejapones,
                R.drawable.parquejapones,
                "Parques"
        ));
    }

    private void aplicarFiltrosActuales() {
        int pos = spinnerFiltro.getSelectedItemPosition();
        String categoriaClave;

        switch (pos) {
            case 1: // opción 1 del array -> Playa
                categoriaClave = "Playa";
                break;
            case 2: // Cultural
                categoriaClave = "Cultural";
                break;
            case 3: // Religioso
                categoriaClave = "Religioso";
                break;
            case 4: // Parques
                categoriaClave = "Parques";
                break;
            default: // 0 -> Todos
                categoriaClave = "";
                break;
        }

        String texto = etBuscarLugar.getText() != null
                ? etBuscarLugar.getText().toString()
                : "";
        String textoNormalizado = normalizar(texto.trim());

        listaFiltrada.clear();

        for (LugarItem item : listaCompleta) {
            if (!pasaFiltroCategoria(item, categoriaClave)) continue;
            if (!pasaFiltroTexto(item, textoNormalizado)) continue;
            listaFiltrada.add(item);
        }

        adapter.notifyDataSetChanged();
    }

    private boolean pasaFiltroCategoria(LugarItem item, String categoriaClave) {
        if (categoriaClave == null || categoriaClave.isEmpty()) {
            // "Todos"
            return true;
        }
        // Comparamos contra la clave interna fija
        return item.getCategoria().equalsIgnoreCase(categoriaClave);
    }

    private boolean pasaFiltroTexto(LugarItem item, String textoNormalizado) {
        if (textoNormalizado == null || textoNormalizado.isEmpty()) return true;

        String nombreLugar = getString(item.getTituloResId());
        String nombreNormalizado = normalizar(nombreLugar);
        return nombreNormalizado.contains(textoNormalizado);
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

    private void abrirInformacionLugar(String nombreLugar) {
        Intent intent = new Intent(this, InformacionLugarActivity.class);
        intent.putExtra("nombreLugar", nombreLugar);
        intent.putExtra("origen", "lugares_turisticos");
        startActivity(intent);
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
