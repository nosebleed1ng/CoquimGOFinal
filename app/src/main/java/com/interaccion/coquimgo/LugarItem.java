package com.interaccion.coquimgo;

public class LugarItem {

    private final String nombreIntent;
    private final int tituloResId;
    private final int imagenResId;
    private final String categoria;

    public LugarItem(String nombreIntent, int tituloResId, int imagenResId, String categoria) {
        this.nombreIntent = nombreIntent;
        this.tituloResId = tituloResId;
        this.imagenResId = imagenResId;
        this.categoria = categoria;
    }

    public String getNombreIntent() {
        return nombreIntent;
    }

    public int getTituloResId() {
        return tituloResId;
    }

    public int getImagenResId() {
        return imagenResId;
    }

    public String getCategoria() {
        return categoria;
    }
}
