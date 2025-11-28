package com.interaccion.coquimgo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "coquimgo.db";
    private static final int DB_VERSION = 3;

    public static final String TABLE_LUGAR = "lugar";
    public static final String TABLE_FAVORITOS = "favoritos";
    public static final String TABLE_VISITADOS = "visitados";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla de lugares
        db.execSQL("CREATE TABLE " + TABLE_LUGAR + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idLugar TEXT NOT NULL UNIQUE," +
                "nombreLugar TEXT NOT NULL," +
                "descripcionLugar TEXT NOT NULL," +
                "horarioLugar TEXT NOT NULL," +
                "ubicacionLugar TEXT NOT NULL," +
                "costoLugar TEXT NOT NULL," +
                "favorito INTEGER NOT NULL DEFAULT 0," +
                "visitado INTEGER NOT NULL DEFAULT 0" +
                ")");

        // Tabla de favoritos (por usuario)
        db.execSQL("CREATE TABLE " + TABLE_FAVORITOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuarioId TEXT NOT NULL," +
                "idLugar TEXT NOT NULL," +
                "UNIQUE(usuarioId, idLugar) ON CONFLICT REPLACE" +
                ")");

        // Tabla de visitados (por usuario, con fecha y rating)
        db.execSQL("CREATE TABLE " + TABLE_VISITADOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuarioId TEXT NOT NULL," +
                "idLugar TEXT NOT NULL," +
                "fecha TEXT," +
                "rating INTEGER," +
                "UNIQUE(usuarioId, idLugar) ON CONFLICT REPLACE" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITADOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUGAR);

        onCreate(db);
    }
}
