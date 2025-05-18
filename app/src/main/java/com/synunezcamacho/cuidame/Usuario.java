package com.synunezcamacho.cuidame;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre, apellido, email, password, direccion, telefono;
    private String fechaNacimiento, genero, salarioDesde, salarioHasta, experiencia, referencias, tipotiempo, sobremi, imgPerfil;
    private Boolean cuidado;
    public Usuario(){}

    public Boolean getCuidado() {
        return cuidado;
    }

    public void setCuidado(Boolean cuidado) {
        this.cuidado = cuidado;
    }

    public String getImgPerfil() {
        return imgPerfil;
    }

    public void setImgPerfil(String imgPerfil) {
        this.imgPerfil = imgPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSalarioDesde() {
        return salarioDesde;
    }

    public void setSalarioDesde(String salarioDesde) {
        this.salarioDesde = salarioDesde;
    }

    public String getSalarioHasta() {
        return salarioHasta;
    }

    public void setSalarioHasta(String salarioHasta) {
        this.salarioHasta = salarioHasta;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getReferencias() {
        return referencias;
    }

    public void setReferencias(String referencias) {
        this.referencias = referencias;
    }

    public String getTipotiempo() {
        return tipotiempo;
    }

    public void setTipotiempo(String tipotiempo) {
        this.tipotiempo = tipotiempo;
    }

    public String getSobremi() {
        return sobremi;
    }

    public void setSobremi(String sobremi) {
        this.sobremi = sobremi;
    }

}
