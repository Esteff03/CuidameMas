package com.synunezcamacho.cuidame;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.synunezcamacho.cuidame.Constants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class Mapa extends AppCompatActivity implements OnMapReadyCallback {



    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String GEOCODING_API_KEY = "AIzaSyDDzubmOggWoNZguBTSnkug-U5y3AWicOE";
    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";

    private GoogleMap googleMap;
    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private List<Usuarios> usuariosVisibles = new ArrayList<>();
    private OkHttpClient httpClient = new OkHttpClient();
    private BottomNavigationView botonNavigationView;
    private Usuarios usuarioSeleccionado = null;
    private Button btnChat;
    private String uidDestinatario = null;

    private SharedPreferences prefs;
    private String tuUsuarioActualId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        listView = findViewById(R.id.listView);

        searchView = findViewById(R.id.searchView);
        String emailDestinatario = getIntent().getStringExtra("email_destinatario");

        if (emailDestinatario != null && !emailDestinatario.isEmpty()) {
            obtenerUidDesdeEmail(emailDestinatario);
        }


        //nav_menu
        botonNavigationView = findViewById(R.id.bottom_navigation);
        botonNavigationView.setSelectedItemId(R.id.page_mapa);
        btnChat = findViewById(R.id.btnChat);
        btnChat.setVisibility(View.GONE); // Ocultar al inicio

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Introduce una dirección:");


         tuUsuarioActualId  = getSharedPreferences("session", MODE_PRIVATE).getString("user_id", null);
        if (tuUsuarioActualId  != null) {
            // Puedes usar el uid_actual para hacer consultas o lo que necesites
            Log.d("MAPA", "UID Actual: " + tuUsuarioActualId );
        } else {
            Log.d("MAPA", "No hay UID guardado");
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(listAdapter);

        configurarBuscador();

        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            String direccion = searchView.getQuery().toString().trim();
            if (!direccion.isEmpty()) {
                buscarCercanosPorCalle(direccion);
                btnChat.setVisibility(View.VISIBLE); // Mostrar botón "Contactar"
            } else {
                Toast.makeText(this, "Por favor, escribe una dirección.", Toast.LENGTH_SHORT).show();
            }
        });


        //actividad del nav_menu
        botonNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.page_chat) {
                startActivity(new Intent(Mapa.this, Contacto.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.page_perfil) {
                startActivity(new Intent(Mapa.this, PerfilPublico.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });
    }


    private void guardarEnPrefs(String clave, String valor) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(clave, valor);
        editor.apply();
    }



    private String obtenerDePrefs(String clave) {
        return prefs.getString(clave, null);
    }


    private void configurarBuscador() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarCercanosPorCalle(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void buscarCercanosPorCalle(String direccion) {
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    URLEncoder.encode(direccion, "UTF-8") +
                    "&key=" + GEOCODING_API_KEY;

            Request request = new Request.Builder().url(url).build();
            httpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(Mapa.this, "Error de red al geocodificar", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(Mapa.this, "Error en Geocoding API", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    String body = response.body().string();

                    try {
                        JSONObject json = new JSONObject(body);
                        JSONArray results = json.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject location = results.getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");
                            LatLng coordenadas = new LatLng(lat, lng);

                            runOnUiThread(() -> mostrarUbicacionYUsuarios(coordenadas, direccion));
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(Mapa.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(Mapa.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error al construir petición", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarUbicacionYUsuarios(LatLng coordenadas, String direccion) {
        googleMap.clear();

        googleMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Dirección: " + direccion)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 14));

        googleMap.addCircle(new CircleOptions()
                .center(coordenadas)
                .radius(5000)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(2f));

        buscarUsuariosCercanos(coordenadas);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Log.e("MAPA", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAPA", "Can't find style.", e);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void buscarUsuariosCercanos(LatLng centro) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/Users?select=*")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .build();

                okhttp3.Response response = httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Error al obtener usuarios");
                }

                String json = response.body().string();
                JSONArray usuariosArray = new JSONArray(json);
                List<Usuarios> encontrados = new ArrayList<>();

                for (int i = 0; i < usuariosArray.length(); i++) {
                    JSONObject obj = usuariosArray.getJSONObject(i);
                    Double lat = obj.optDouble("Latitud", 0.0);
                    Double lng = obj.optDouble("Longitud", 0.0);

                    if (lat != 0.0 && lng != 0.0) {
                        float[] results = new float[1];
                        Location.distanceBetween(
                                centro.latitude, centro.longitude,
                                lat, lng,
                                results);

                        if (results[0] <= 5000) {
                            Usuarios u = new Usuarios();
                            u.setId(obj.optString("id", ""));
                            u.setNombre(obj.optString("Nombre", ""));
                            u.setTelefono(obj.optString("Telefono", ""));
                            u.setDireccion(obj.optString("Direccion", ""));
                            u.setBarrio(obj.optString("Barrio", ""));

                            u.setRol(obj.optString("Rol", ""));
                            u.setLatitud(lat);
                            u.setLongitud(lng);
                            encontrados.add(u);
                        }
                    }
                }

                runOnUiThread(() -> mostrarUsuariosEnMapa(encontrados));

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(Mapa.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void mostrarUsuariosEnMapa(List<Usuarios> lista) {
        listAdapter.clear();
        usuariosVisibles.clear();

        btnChat.setVisibility(View.GONE); // Ocultar por defecto
        usuarioSeleccionado = null;

        for (Usuarios u : lista) {
            LatLng loc = new LatLng(u.getLatitud(), u.getLongitud());

            googleMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(u.getNombre() + " (" + u.getRol() + ")")
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            "cuidadora".equalsIgnoreCase(u.getRol())
                                    ? BitmapDescriptorFactory.HUE_GREEN
                                    : BitmapDescriptorFactory.HUE_RED)));

            listAdapter.add(u.getNombre() + " - " + u.getRol()
                    + "\n" + u.getDireccion()
                    + "\nTel: " + u.getTelefono());

            usuariosVisibles.add(u);
        }

        listAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener((p, v, pos, id) -> {
            usuarioSeleccionado = usuariosVisibles.get(pos);
            LatLng loc = new LatLng(usuarioSeleccionado.getLatitud(), usuarioSeleccionado.getLongitud());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));

            // Mostrar datos del usuario
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Usuario seleccionado")
                    .setMessage("Nombre: " + usuarioSeleccionado.getNombre()
                            + "\nRol: " + usuarioSeleccionado.getRol()
                            + "\nDirección: " + usuarioSeleccionado.getDireccion()
                            + "\nTeléfono: " + usuarioSeleccionado.getTelefono())
                    .setPositiveButton("Contactar", (dialog, which) -> {
                        if (usuarioSeleccionado == null) {
                            Toast.makeText(Mapa.this, "Selecciona un usuario primero", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Usa uidDestinatario si existe, sino usa el id del usuario seleccionado
                        String uidAUsar = (uidDestinatario != null) ? uidDestinatario : usuarioSeleccionado.getId();

                        Intent intent = new Intent(Mapa.this, ChatActivity.class);
                        intent.putExtra(Constants.EXTRA_ID_CONTACTO, usuarioSeleccionado.getId());
                        intent.putExtra(Constants.EXTRA_NOMBRE_CONTACTO, usuarioSeleccionado.getNombre());
                        intent.putExtra("UUID_ACTUAL", tuUsuarioActualId); // ID del usuario actual
                        intent.putExtra("UUID_DESTINATARIO", uidDestinatario != null ? uidDestinatario : usuarioSeleccionado.getId()); // Usa UID obtenido o fallback
                        startActivity(intent);
                    })
                    .show();
        });

        btnChat.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                Intent intent = new Intent(Mapa.this, ChatActivity.class);
                intent.putExtra(Constants.EXTRA_ID_CONTACTO, usuarioSeleccionado.getId());
                intent.putExtra(Constants.EXTRA_NOMBRE_CONTACTO, usuarioSeleccionado.getNombre());
                startActivity(intent);
            } else {
                Toast.makeText(Mapa.this, "Selecciona un usuario primero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerUidDesdeEmail(String email) {
        new Thread(() -> {
            try {
                String urlString = SUPABASE_URL + "/rest/v1/rpc/get_user_id_by_email";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // JSON con el parámetro de la función
                String jsonInputString = "{\"p_email\": \"" + email + "\"}";

                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Scanner scanner = new Scanner(conn.getInputStream());
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNext()) {
                        response.append(scanner.nextLine());
                    }
                    scanner.close();

                    // La respuesta será algo como: "uuid" (un string simple)
                    String uid = response.toString().replace("\"", ""); // quita comillas

                    runOnUiThread(() -> {
                        uidDestinatario = uid;  // Guardar UID global
                        Toast.makeText(Mapa.this, "UID Destinatario: " + uidDestinatario, Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(Mapa.this, "Error en consulta: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(Mapa.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }




}
