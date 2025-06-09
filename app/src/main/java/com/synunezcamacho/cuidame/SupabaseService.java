package com.synunezcamacho.cuidame;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseService {

    public interface SupabaseCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    public static void enviarMensajeEnChat(String supabaseUrl, String apiKey,
                                           String remitenteId, String destinatarioId,
                                           String contenido, String username,
                                           SupabaseCallback callback) {
        String url = supabaseUrl + "/rest/v1/messages";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", remitenteId);
            jsonBody.put("contact_id", destinatarioId);
            jsonBody.put("content", contenido);
            jsonBody.put("username", username);
        } catch (Exception e) {
            callback.onError("Error creando JSON: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de red: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onError("Error HTTP: " + response.code() + " - " + responseBody);
                }
            }
        });
    }

    public static void obtenerMensajesChat(String supabaseUrl, String apiKey,
                                           String usuario1, String usuario2,
                                           SupabaseCallback callback) {
        String url = supabaseUrl + "/rest/v1/messages?" +
                "select=*&" +
                "or=(and(user_id.eq." + usuario1 + ",contact_id.eq." + usuario2 + ")," +
                "and(user_id.eq." + usuario2 + ",contact_id.eq." + usuario1 + "))" +
                "&order=inserted_at.asc";

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Accept", "application/json")
                .build(); // 👈 Esto es GET por defecto

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Fallo de red: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d("SupabaseService", "Respuesta del servidor: " + responseBody);

                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onError("Error HTTP " + response.code() + ": " + responseBody);
                }
            }
        });
    }

    public static void enviarTokenFCM(Context context, String token) {
        String uuid = UUIDManager.getUUID(context);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("fcm_token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                Mapa.SUPABASE_URL + "/rest/v1/profiles?id=eq." + uuid,
                jsonBody,
                response -> Log.d("FCM", "Token guardado en Supabase"),
                error -> Log.e("FCM", "Error al guardar token: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", Mapa.SUPABASE_API_KEY);
                headers.put("Authorization", "Bearer " + Mapa.SUPABASE_API_KEY);
                headers.put("Content-Type", "application/json");
                headers.put("Prefer", "resolution=merge-duplicates");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
}
