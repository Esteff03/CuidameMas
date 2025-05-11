package com.synunezcamacho.cuidame;

public class Mensaje {

    private String emisor;
    private String receptor;
    private String contenido;
    private String hora;

    public Mensaje(){}
    public Mensaje(String emisor, String receptor, String contenido, String hora) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.contenido = contenido;
        this.hora = hora;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptos) {
        this.receptor = receptos;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
