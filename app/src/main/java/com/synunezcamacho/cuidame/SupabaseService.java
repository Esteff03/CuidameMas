package com.synunezcamacho.cuidame;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseService {

    public interface Callback {
        void onSuccess(String response);
        void onError(String error);
    }

    // Insertar mensaje en tabla chat
    public static void enviarMensajeEnChat(String supabaseUrl, String apiKey,
                                           String remitenteId, String destinatarioId,
                                           String contenido, Callback callback) {
        // Endpoint para insertar en tabla "chat"
        String url = supabaseUrl + "/rest/v1/messages";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("remitente_id", remitenteId);
            jsonBody.put("destinatario_id", destinatarioId);
            jsonBody.put("contenido", contenido);
            jsonBody.put("enviado_en", System.currentTimeMillis()); // timestamp o usa formato ISO

        } catch (Exception e) {
            callback.onError("Error creando JSON: " + e.getMessage());
            return;
        }

        // Construir petición POST
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                jsonBody.toString(),
                okhttp3.MediaType.parse("application/json")
        );

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onError(e.getMessage());
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onError("Error HTTP: " + response.code() + " - " + response.message());
                }
            }
        });
    }

    // Obtener mensajes entre dos usuarios
    public static void obtenerMensajesChat(String supabaseUrl, String apiKey,
                                           String usuario1, String usuario2, Callback callback) {
        // Filtro para obtener mensajes en ambas direcciones
        // ?select=*&or=(and(remitente_id.eq.usuario1,destinatario_id.eq.usuario2),and(remitente_id.eq.usuario2,destinatario_id.eq.usuario1))
        String url = supabaseUrl + "/rest/v1/chat?" +
                "select=*&" +
                "or=(and(remitente_id.eq." + usuario1 + ",destinatario_id.eq." + usuario2 + ")," +
                "and(remitente_id.eq." + usuario2 + ",destinatario_id.eq." + usuario1 + "))" +
                "&order=enviado_en.asc";

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onError(e.getMessage());
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    callback.onSuccess(resp);
                } else {
                    callback.onError("Error HTTP: " + response.code() + " - " + response.message());
                }
            }
        });
    }
}

