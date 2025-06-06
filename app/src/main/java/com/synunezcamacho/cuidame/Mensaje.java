package com.synunezcamacho.cuidame;

public class Mensaje {
    private String contenido;
    private String remitente_id;
    private String enviado_en;
    private String id;
    private String hora;
    public Mensaje(String id, String contenido, String remitente_id, String enviado_en,String hora) {
        this.id = id;
        this.contenido = contenido;
        this.remitente_id = remitente_id;
        this.enviado_en = enviado_en;
        this.hora = hora;
    }

    public String getContenido() {
        return contenido;
    }

    public String getRemitenteId() {
        return remitente_id;
    }

    public String getEnviadoEn() {
        return enviado_en;
    }
    public String  getHora(){
        return hora;
    }
}
