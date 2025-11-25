package com.interaccion.coquimgo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class DbLugar extends DbHelper {
    Context context;
    public DbLugar(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public Long insertarLugar(String nombre, String descripcion, String horario, String ubicacion,
                              String costo, String tipo, int favorito, int visitado){
        long id = 0;

        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("nombreLugar", nombre);
            values.put("descripcionLugar", descripcion);
            values.put("horarioLugar", horario);
            values.put("ubicacionLugar", ubicacion);
            values.put("costoLugar", costo);
            values.put("tipoLugar", tipo);
            values.put("favorito", favorito);
            values.put("visitado", visitado);

            id = db.insert(TABLE_LUGAR, null, values);
        } catch(Exception ex){

        }

        return id;
    }
}
