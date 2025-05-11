package com.synunezcamacho.cuidame;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// Interfaz ApiService que define las rutas de la API
public interface ApiService {


    // Define el endpoint para obtener la lista de usuarios (ajustar según tu API)
    @GET("usuarios")  // Asegúrate de que el endpoint sea el correcto
    Call<List<Usuarios>> getUsuarios();

    // Obtener cuidadores
    @GET("cuidadores")
    Call<List<Usuarios>> getCuidadores();

    // Obtener buscadores
    @GET("buscadores")
    Call<List<Usuarios>> getBuscadores();

    // Insertar un nuevo usuario
    @POST("usuarios")
    Call<Void> insertUsuario(@Body Usuarios usuario);

}
