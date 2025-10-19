package com.interaccion.coquimgo;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    // Método para cambiar el idioma de la app dinámicamente
    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, resources.getDisplayMetrics());
    }
}
