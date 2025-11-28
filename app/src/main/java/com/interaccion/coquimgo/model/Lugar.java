package com.interaccion.coquimgo.model;

public class Lugar {
    private String IdLugar;
    private String NombreLugar;
    private String DescripcionLugar;
    private String UbicacionLugar;
    private String HorarioLugar;
    private String CostoLugar;
    private String CategoriaId;


    private float RatingGlobal;
    private int RatingCount;
    private boolean Favorito;
    private boolean Visitado;

    public Lugar() { }

    public String getIdLugar() {
        return IdLugar;
    }

    public void setIdLugar(String idLugar) {
        IdLugar = idLugar;
    }

    public String getNombreLugar() {
        return NombreLugar;
    }

    public void setNombreLugar(String nombreLugar) {
        NombreLugar = nombreLugar;
    }

    public String getDescripcionLugar() {
        return DescripcionLugar;
    }

    public void setDescripcionLugar(String descripcionLugar) {
        DescripcionLugar = descripcionLugar;
    }

    public String getUbicacionLugar() {
        return UbicacionLugar;
    }

    public void setUbicacionLugar(String ubicacionLugar) {
        UbicacionLugar = ubicacionLugar;
    }

    public String getHorarioLugar() {
        return HorarioLugar;
    }

    public void setHorarioLugar(String horarioLugar) {
        HorarioLugar = horarioLugar;
    }

    public String getCostoLugar() {
        return CostoLugar;
    }

    public void setCostoLugar(String costoLugar) {
        CostoLugar = costoLugar;
    }

    public String getCategoriaId() {
        return CategoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        CategoriaId = categoriaId;
    }

    public boolean getFavorito() {
        return Favorito;
    }

    public void setFavorito(boolean favorito) {
        Favorito = favorito;
    }

    public boolean getVisitado() {
        return Visitado;
    }

    public void setVisitado(boolean visitado) {
        Visitado = visitado;
    }

    public float getRatingGlobal() {
        return RatingGlobal;
    }

    public void setRatingGlobal(float ratingGlobal) {
        RatingGlobal = ratingGlobal;
    }

    public int getRatingCount() {
        return RatingCount;
    }

    public void setRatingCount(int ratingCount) {
        RatingCount = ratingCount;
    }
}
