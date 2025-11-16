package com.interaccion.coquimgo;

import com.google.firebase.database.FirebaseDatabase;

public class FireBaseApp extends android.app.Application {
    public static void initializeApp(InformacionLugarActivity informacionLugarActivity) {
        //Si no funciona o algo es por este método
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
