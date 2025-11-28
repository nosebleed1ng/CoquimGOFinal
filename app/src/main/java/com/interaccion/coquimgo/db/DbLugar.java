package com.interaccion.coquimgo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbLugar {

    private final DbHelper dbHelper;

    public DbLugar(Context context) {
        dbHelper = new DbHelper(context);
    }

    public boolean existeLugarPorId(String idLugar) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean existe = false;

        Cursor cursor = db.query(
                DbHelper.TABLE_LUGAR,
                new String[]{"id"},
                "idLugar = ?",
                new String[]{idLugar},
                null, null, null,
                "1"
        );

        if (cursor != null) {
            existe = cursor.moveToFirst();
            cursor.close();
        }

        db.close();
        return existe;
    }

    public long insertarLugar(String idLugar,
                              String nombreLugar,
                              String descripcionLugar,
                              String horarioLugar,
                              String ubicacionLugar,
                              String costoLugar,
                              boolean favorito,
                              boolean visitado) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("idLugar", idLugar);
        values.put("nombreLugar", nombreLugar);
        values.put("descripcionLugar", descripcionLugar);
        values.put("horarioLugar", horarioLugar);
        values.put("ubicacionLugar", ubicacionLugar);
        values.put("costoLugar", costoLugar);
        values.put("favorito", favorito ? 1 : 0);
        values.put("visitado", visitado ? 1 : 0);
        values.put("ratingGlobal", 0f);
        values.put("ratingCount", 0);

        long id = db.insert(DbHelper.TABLE_LUGAR, null, values);
        db.close();

        return id;
    }

    public int actualizarLugar(String idLugar,
                               String nombreLugar,
                               String descripcionLugar,
                               String horarioLugar,
                               String ubicacionLugar,
                               String costoLugar,
                               boolean favorito,
                               boolean visitado) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombreLugar", nombreLugar);
        values.put("descripcionLugar", descripcionLugar);
        values.put("horarioLugar", horarioLugar);
        values.put("ubicacionLugar", ubicacionLugar);
        values.put("costoLugar", costoLugar);
        values.put("favorito", favorito ? 1 : 0);
        values.put("visitado", visitado ? 1 : 0);

        int filas = db.update(
                DbHelper.TABLE_LUGAR,
                values,
                "idLugar = ?",
                new String[]{idLugar}
        );

        db.close();
        return filas;
    }

    public void actualizarFavorito(String usuarioId,
                                   String idLugar,
                                   boolean esFavorito) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Actualiza columna favorito en tabla lugar
        ContentValues valuesLugar = new ContentValues();
        valuesLugar.put("favorito", esFavorito ? 1 : 0);
        db.update(DbHelper.TABLE_LUGAR, valuesLugar,
                "idLugar = ?", new String[]{idLugar});

        if (esFavorito) {
            // Insertar o reemplazar en tabla favoritos
            ContentValues values = new ContentValues();
            values.put("usuarioId", usuarioId);
            values.put("idLugar", idLugar);

            db.insertWithOnConflict(
                    DbHelper.TABLE_FAVORITOS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        } else {
            // Eliminar si ya no es favorito
            db.delete(
                    DbHelper.TABLE_FAVORITOS,
                    "usuarioId = ? AND idLugar = ?",
                    new String[]{usuarioId, idLugar}
            );
        }

        db.close();
    }

    public void actualizarVisitado(String usuarioId,
                                   String idLugar,
                                   boolean esVisitado,
                                   String fecha,
                                   int rating) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Actualiza columna visitado en tabla lugar
        ContentValues valuesLugar = new ContentValues();
        valuesLugar.put("visitado", esVisitado ? 1 : 0);
        db.update(DbHelper.TABLE_LUGAR, valuesLugar,
                "idLugar = ?", new String[]{idLugar});

        if (esVisitado) {
            // Insertar o reemplazar en tabla visitados
            ContentValues values = new ContentValues();
            values.put("usuarioId", usuarioId);
            values.put("idLugar", idLugar);
            values.put("fecha", fecha);
            values.put("rating", rating);

            db.insertWithOnConflict(
                    DbHelper.TABLE_VISITADOS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        } else {
            // Eliminar si deja de estar visitado
            db.delete(
                    DbHelper.TABLE_VISITADOS,
                    "usuarioId = ? AND idLugar = ?",
                    new String[]{usuarioId, idLugar}
            );
        }

        db.close();
    }

    public int contarFavoritos(String usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int conteo = 0;

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + DbHelper.TABLE_FAVORITOS +
                        " WHERE usuarioId = ?",
                new String[]{usuarioId}
        );

        if (c != null) {
            if (c.moveToFirst()) {
                conteo = c.getInt(0);
            }
            c.close();
        }

        db.close();
        return conteo;
    }

    public int contarVisitados(String usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int conteo = 0;

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + DbHelper.TABLE_VISITADOS +
                        " WHERE usuarioId = ?",
                new String[]{usuarioId}
        );

        if (c != null) {
            if (c.moveToFirst()) {
                conteo = c.getInt(0);
            }
            c.close();
        }

        db.close();
        return conteo;
    }
    public void actualizarRatingGlobal(String idLugar,
                                       float ratingGlobal,
                                       int ratingCount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ratingGlobal", ratingGlobal);
        values.put("ratingCount", ratingCount);

        db.update(
                DbHelper.TABLE_LUGAR,
                values,
                "idLugar = ?",
                new String[]{idLugar}
        );

        db.close();
    }
}
