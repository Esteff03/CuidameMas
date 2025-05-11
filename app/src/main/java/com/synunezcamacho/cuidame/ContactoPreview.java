package com.synunezcamacho.cuidame;

public class ContactoPreview {
    private String nombreUsuario;
    private String ultimoMensaje;
    private String hora;
    private int imagenPerfil;

    public ContactoPreview(){}
    public ContactoPreview(String nombreUsuario, String ultimoMensaje, String hora, int imagenPerfil) {
        this.nombreUsuario = nombreUsuario;
        this.ultimoMensaje = ultimoMensaje;
        this.hora = hora;
        this.imagenPerfil = imagenPerfil;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }
    public String getHora() {
        return hora;
    }
    public int getImagenPerfil() {
        return imagenPerfil;
    }

}
