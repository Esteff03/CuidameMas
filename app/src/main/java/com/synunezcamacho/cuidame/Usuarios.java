package com.synunezcamacho.cuidame;

public class Usuarios {
    private String id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String barrio;
    private Double latitud;
    private Double longitud;
    private String rol; // "cuidador" o "buscador"

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
