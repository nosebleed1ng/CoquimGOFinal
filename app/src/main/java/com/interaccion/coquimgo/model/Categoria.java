package com.interaccion.coquimgo.model;

import java.util.Map;

public class Categoria {

    private String idCategoria;
    private String nombre;
    private Map<String, Boolean> lugares;

    public Categoria() { }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Map<String, Boolean> getLugares() {
        return lugares;
    }

    public void setLugares(Map<String, Boolean> lugares) {
        this.lugares = lugares;
    }
}
