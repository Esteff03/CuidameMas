package com.synunezcamacho.cuidame;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SupabaseService {

    public interface Callback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void obtenerOcrearChat(String supabaseUrl, String apiKey, String usuarioActualId, String usuarioOtroId, Callback callback) {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... voids) {
                try {
                    String urlStr = supabaseUrl + "/rest/v1/chats?select=*&or=(usuario1_id.eq." + usuarioActualId + ",usuario2_id.eq." + usuarioOtroId + ")";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("apikey", apiKey);
                    conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                    conn.setRequestProperty("Content-Type", "application/json");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        JSONArray chats = new JSONArray(response.toString());
                        if (chats.length() > 0) {
                            JSONObject chat = chats.getJSONObject(0);
                            return chat.getString("id");
                        } else {
                            // Crear nuevo chat
                            url = new URL(supabaseUrl + "/rest/v1/chats");
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("apikey", apiKey);
                            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);

                            JSONObject nuevoChat = new JSONObject();
                            nuevoChat.put("usuario1_id", usuarioActualId);
                            nuevoChat.put("usuario2_id", usuarioOtroId);

                            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                            wr.writeBytes(nuevoChat.toString());
                            wr.flush();
                            wr.close();

                            responseCode = conn.getResponseCode();
                            if (responseCode == 201) {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                                reader.close();

                                JSONArray nuevoChatArray = new JSONArray(sb.toString());
                                JSONObject chatCreado = nuevoChatArray.getJSONObject(0);
                                return chatCreado.getString("id");
                            } else {
                                return null;
                            }
                        }
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError("Error al obtener o crear el chat");
                }
            }
        }.execute();
    }

    public static void enviarMensaje(String supabaseUrl, String apiKey, String chatId, String remitenteId, String contenido, Callback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(supabaseUrl + "/rest/v1/mensajes");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("apikey", apiKey);
                    conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    JSONObject mensaje = new JSONObject();
                    mensaje.put("chat_id", chatId);
                    mensaje.put("remitente_id", remitenteId);
                    mensaje.put("contenido", contenido);

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(mensaje.toString());
                    wr.flush();
                    wr.close();

                    int responseCode = conn.getResponseCode();
                    return responseCode == 201;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            protected void onPostExecute(Boolean success) {
                if (success) {
                    callback.onSuccess("Mensaje enviado");
                } else {
                    callback.onError("Error al enviar el mensaje");
                }
            }
        }.execute();
    }

    public static void obtenerMensajes(String supabaseUrl, String apiKey, String chatId, Callback callback) {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... voids) {
                try {
                    String urlStr = supabaseUrl + "/rest/v1/mensajes?chat_id=eq." + chatId + "&order=inserted_at.asc";

                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("apikey", apiKey);
                    conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                    conn.setRequestProperty("Content-Type", "application/json");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        return response.toString();
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError("Error al obtener los mensajes");
                }
            }
        }.execute();
    }
}
