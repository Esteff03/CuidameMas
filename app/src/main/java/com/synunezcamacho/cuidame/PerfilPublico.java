package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.HttpURLConnection;
import java.net.URL;

public class PerfilPublico extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txtNombre, txtFechaNacimiento, txtSexo, txtDireccion;
    private TextView txtSalario, txtExperiencia, txtReferencia, txtTipoTiempo, txtSobreMi;
    private ImageView imgAtras;
    private BottomNavigationView botonNavigationView;
    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_publico);

        // la barra del toolbar
        toolbar = findViewById(R.id.miToolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.principal));

        //nav_menu
        botonNavigationView = findViewById(R.id.bottom_navigation);
        botonNavigationView.setSelectedItemId(R.id.page_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombre = findViewById(R.id.txtNombre);
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        txtSexo = findViewById(R.id.txtSexo);

        txtSalario = findViewById(R.id.txtSalario);
        txtExperiencia = findViewById(R.id.txtExperiencia);
        txtReferencia = findViewById(R.id.txtReferencia);
        txtTipoTiempo = findViewById(R.id.txtTipoTiempo);
        txtSobreMi = findViewById(R.id.txtSobreMi);

        imgAtras = findViewById(R.id.btnAtras);

        //boton para atras
        imgAtras.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilPublico.this, Login.class);
            startActivity(intent);
            finish();
        });

        //recuerar la imagen


        //visualizacion de los datos almacenador
        Usuario usuario = (Usuario) getIntent().getSerializableExtra("perfil");

        if (usuario != null) {
            txtNombre.setText(usuario.getNombre() + " " + usuario.getApellido());
            txtFechaNacimiento.setText("Fecha Nacimiento: "+usuario.getFechaNacimiento());
            txtSexo.setText("Sexo: " + usuario.getGenero());
            txtDireccion.setText("Direccion: " + usuario.getDireccion());
            txtSalario.setText("Salario: Desde "+ usuario.getSalarioDesde() + " Hasta "+ usuario.getSalarioHasta());
            txtExperiencia.setText("Experiencia: "+ usuario.getExperiencia());
            txtReferencia.setText("Referencias: " + usuario.getReferencias());
            txtTipoTiempo.setText("Tipo de Tiempo: " + usuario.getTipotiempo());
            txtSobreMi.setText("Sobre Mi: " + usuario.getSobremi());

        }

        botonNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.page_mapa) {
                startActivity(new Intent(PerfilPublico.this, Mapa.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.page_chat) {
                startActivity(new Intent(PerfilPublico.this, Contacto.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.page_perfil) {

                return true;
            }
            return false;
        });



        cargarPerfilDesdeSupabase();

    }

    private void cargarPerfilDesdeSupabase() {
        new Thread(() -> {
            try {
                String userId = getSharedPreferences("session", MODE_PRIVATE).getString("user_id", null);
                if (userId == null) {
                    runOnUiThread(() -> Toast.makeText(PerfilPublico.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show());
                    return;
                }

                // 1. Consulta datos adicionales
                String url1 = SUPABASE_URL + "/rest/v1/informacion_adicional?id=eq." + userId;
                HttpURLConnection conn1 = (HttpURLConnection) new URL(url1).openConnection();
                conn1.setRequestMethod("GET");
                conn1.setRequestProperty("apikey", SUPABASE_KEY);
                conn1.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                conn1.setRequestProperty("Accept", "application/json");

                int responseCode1 = conn1.getResponseCode();
                if (responseCode1 != HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> Toast.makeText(PerfilPublico.this, "Error al cargar perfil adicional", Toast.LENGTH_SHORT).show());
                    return;
                }

                String response1 = new java.util.Scanner(conn1.getInputStream()).useDelimiter("\\A").next();
                org.json.JSONArray array1 = new org.json.JSONArray(response1);
                if (array1.length() == 0) return;
                org.json.JSONObject datosAdicionales = array1.getJSONObject(0);

                // 2. Consulta nombre/apellido/dirección en tabla Users
                String url2 = SUPABASE_URL + "/rest/v1/Users?id=eq." + userId;
                HttpURLConnection conn2 = (HttpURLConnection) new URL(url2).openConnection();
                conn2.setRequestMethod("GET");
                conn2.setRequestProperty("apikey", SUPABASE_KEY);
                conn2.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                conn2.setRequestProperty("Accept", "application/json");

                int responseCode2 = conn2.getResponseCode();
                if (responseCode2 != HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> Toast.makeText(PerfilPublico.this, "Error al cargar datos de usuario", Toast.LENGTH_SHORT).show());
                    return;
                }

                String response2 = new java.util.Scanner(conn2.getInputStream()).useDelimiter("\\A").next();
                org.json.JSONArray array2 = new org.json.JSONArray(response2);
                org.json.JSONObject datosUsuario = array2.length() > 0 ? array2.getJSONObject(0) : null;

                // Mostrar en UI
                runOnUiThread(() -> {
                    String nombreCompleto = (datosUsuario != null ? datosUsuario.optString("nombre", "") + " " + datosUsuario.optString("apellido", "") : "");
                    String direccion = datosUsuario != null ? datosUsuario.optString("direccion", "") : "";

                    txtNombre.setText(nombreCompleto);
                    txtFechaNacimiento.setText("Fecha Nacimiento: " + formatearFechaDesdeBD(datosAdicionales.optString("fecha_nacimiento", "")));
                    txtSexo.setText("Sexo: " + datosAdicionales.optString("sexo", ""));
                    txtSalario.setText("Salario: " + datosAdicionales.optDouble("salario", 0));
                    txtExperiencia.setText("Experiencia: " + datosAdicionales.optString("experiencia", ""));
                    txtReferencia.setText("Referencias: " + datosAdicionales.optString("referencias", ""));
                    txtTipoTiempo.setText("Tipo de Tiempo: " + datosAdicionales.optString("tipo_tiempo", ""));
                    txtSobreMi.setText("Sobre Mi: " + datosAdicionales.optString("sobre_mi", ""));
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(PerfilPublico.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }


    // Cambia el formato fecha ISO -> dd/MM/yyyy para mostrar en EditText
    private String formatearFechaDesdeBD(String fechaBD) {
        try {
            String[] partes = fechaBD.split("-");
            if (partes.length == 3) {
                return partes[2] + "/" + partes[1] + "/" + partes[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // Para seleccionar item del Spinner por texto
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }


}