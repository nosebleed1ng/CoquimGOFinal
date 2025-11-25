package com.interaccion.coquimgo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NOMBRE = "coquimgo.db";
    public static final String TABLE_LUGAR = "lugar";
    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LUGAR + "(" +
                "idLugar INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombreLugar TEXT NOT NULL," +
                "descripcionLugar TEXT NOT NULL," +
                "horarioLugar TEXT NOT NULL," +
                "ubicacionLugar TEXT NOT NULL," +
                "costoLugar TEXT NOT NULL," +
                "tipoLugar TEXT NOT NULL," +
                "favorito INTEGER NOT NULL," +
                "visitado INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + TABLE_LUGAR );
        onCreate(db);
    }
}
