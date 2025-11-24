package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LugaresRecyclerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // Tema
        ThemeHelper.applyTheme(this);

        // Idioma
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefs.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_lugares);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.lugaresTuristicos));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerLugares);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<LugarItem> lista = crearListaLugares();

        LugaresAdapter adapter = new LugaresAdapter(lista, item -> {
            // Abrir información del lugar
            Intent intent = new Intent(this, InformacionLugarActivity.class);
            intent.putExtra("nombreLugar", item.getNombreIntent());
            intent.putExtra("origen", "lista_recycler");
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private List<LugarItem> crearListaLugares() {
        List<LugarItem> lista = new ArrayList<>();

        lista.add(new LugarItem(
                "Fuerte Lambert",
                R.string.txtfuertelambert,
                R.drawable.fuertelambert,
                "Cultural"
        ));
        lista.add(new LugarItem(
                "Cruz del Tercer Milenio",
                R.string.txtcruzdeltercermilenio,
                R.drawable.cruztercermilenio,
                "Cultural"
        ));
        lista.add(new LugarItem(
                "Pueblito Peñuelas",
                R.string.txtpueblitopeñuelas,
                R.drawable.pueblitopenuelas,
                "Cultural"
        ));
        lista.add(new LugarItem(
                "Avenida del Mar",
                R.string.txtavenidadelmar,
                R.drawable.avenidadelmar,
                "Playa"
        ));
        lista.add(new LugarItem(
                "La Mezquita",
                R.string.txtlamezquita,
                R.drawable.lamezquita,
                "Religioso"
        ));
        lista.add(new LugarItem(
                "El Faro",
                R.string.txtelfaro,
                R.drawable.elfaro,
                "Playa"
        ));
        lista.add(new LugarItem(
                "Parque Japonés",
                R.string.txtparquejapones,
                R.drawable.parquejapones,
                "Parques"
        ));

        return lista;
    }
}
