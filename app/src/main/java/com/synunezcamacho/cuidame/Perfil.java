package com.synunezcamacho.cuidame;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class Perfil extends AppCompatActivity {
    Spinner spinnerTipoTiempo, spinnerExperiencia;
    Toolbar toolbar;
    Bundle bundle;
    EditText edtNombre, edtDireccion, edtFechaNacimiento;
    EditText salarioDesde, salarioHasta, edtSobreMi;
    RadioGroup sexoGroup;
    RadioButton rbMuje, rbHombre;
    CheckBox checkBoxReferencia;
    Button btnVerPerfilPublico,btnGuardar;
    TextView cambio1, cambio2,cambio3, cambio4;
    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        edtNombre = findViewById(R.id.edtNombre);
        edtDireccion = findViewById(R.id.edtDireccion);
        edtFechaNacimiento = findViewById(R.id.edtFechaNacimiento);
        sexoGroup = findViewById(R.id.sexoGroup);
        rbMuje = findViewById(R.id.rbMujer);
        rbHombre = findViewById(R.id.rbHombre);
        salarioDesde = findViewById(R.id.salarioDesde);
        salarioHasta = findViewById(R.id.salarioHasta);
        spinnerTipoTiempo =  findViewById(R.id.spinnerTipoTiempo);
        spinnerExperiencia = findViewById(R.id.spinnerExperiencia);
        checkBoxReferencia = findViewById(R.id.checkBoxReferencia);
        edtSobreMi = findViewById(R.id.edtSobreMi);
        btnVerPerfilPublico = findViewById(R.id.btnVerPerfilPublico);
        BottomNavigationView nav_menu = findViewById(R.id.bottom_navigation);
        btnGuardar = findViewById(R.id.btnGuardar);
        //cambio de texto buscoCudiador o SoyCuidador
        cambio1 = findViewById(R.id.cambio1);
        cambio2 = findViewById(R.id.cambio2);
        cambio3 = findViewById(R.id.cambio3);
        cambio4 = findViewById(R.id.cambio4);


        btnGuardar.setOnClickListener(view -> {
            // Primero preparas tu objeto usuario, lo validas, etc.
            Usuario usuario = crearUsuarioDesdeCampos();

            if (salarioValidar(usuario)) {
                guardarPerfilEnSupabase(usuario);
            }
        });
        // la barra del toolbar
        toolbar = findViewById(R.id.miToolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.principal));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //configuracion del Spinner
        configuracionSpinner(spinnerTipoTiempo, new String[]{"Por Horas", "Internas"});
        configuracionSpinner(spinnerExperiencia, new String[]{"Sin experiencia", "1-2 años", "3+ años"});

        //Recojo los valores del Usuario
        bundle = getIntent().getExtras();
        Usuario usuario = (Usuario)bundle.getSerializable("perfil");
        edtNombre.setText(usuario.getNombre() +" " + usuario.getApellido());
        edtDireccion.setText(usuario.getDireccion());

        //Fecha de Nacimiento Formato Calendario
        edtFechaNacimiento.setOnClickListener(v -> {
            final Calendar calendario = Calendar.getInstance();
            int dia = calendario.get(Calendar.DAY_OF_MONTH);
            int mes = calendario.get(Calendar.MONTH);
            int anio = calendario.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Perfil.this,
                    (view, year, month, dayOfMonth) -> {
                        // Sumamos 1 al mes porque en Calendar enero = 0
                        String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                        edtFechaNacimiento.setText(fechaSeleccionada);

                        usuario.setFechaNacimiento(edtFechaNacimiento.getText().toString());
                    },
                    anio, mes, dia
            );
            datePickerDialog.show();

        });

        //boton para Perfil publico
        btnVerPerfilPublico.setOnClickListener(view->{

            //Datos actualizados Usuario
            actualizarSexoUsuario(usuario);
            spinnersUsuario(usuario);
            checkboxUsuario(usuario);
            sobreUsuario(usuario);
            if (!salarioValidar(usuario)) return;

            Intent intent = new Intent(Perfil.this, PerfilPublico.class);
            // Pasar el objeto Usuario al Intent
            intent.putExtra("perfil", usuario);
            startActivity(intent);
        });
        configurarTextosPorPerfil(usuario);


        //la parte del menu_nav
        nav_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.page_mapa) {
                    Intent intent = new Intent(Perfil.this, Mapa.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.page_chat) {
                    Intent intent = new Intent(Perfil.this, ChatActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.page_perfil) {
                    Intent intent = new Intent(Perfil.this, PerfilPublico.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }


    private void configuracionSpinner(Spinner spinner, String[] opciones) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void actualizarSexoUsuario(Usuario usuario) {
        int idSeleccionado = sexoGroup.getCheckedRadioButtonId();
        String generoSeleccionado = "";

        if (idSeleccionado == R.id.rbMujer) {
            generoSeleccionado = "Mujer";
        } else if (idSeleccionado == R.id.rbHombre) {
            generoSeleccionado = "Hombre";
        }

        // Asignar el valor al usuario
        usuario.setGenero(generoSeleccionado);
    }

    private boolean salarioValidar(Usuario usuario){
        String salarioDesdeText = salarioDesde.getText().toString();
        String salarioHastaText = salarioHasta.getText().toString();
        double salarioDesdeValor = Double.parseDouble(salarioDesdeText);
        double salarioHastaValor = Double.parseDouble(salarioHastaText);

        try{

            if (salarioHastaValor < salarioDesdeValor) {
                Toast.makeText(Perfil.this, "El salario Incorrecto", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                usuario.setSalarioDesde(salarioDesdeText);
                usuario.setSalarioHasta(salarioHastaText);
                return true;
            }
        }catch (NumberFormatException e){
            Toast.makeText(this, "Introduce números válidos en el salario", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void spinnersUsuario(Usuario usuario) {
        String tipoTiempoSeleccionado = spinnerTipoTiempo.getSelectedItem().toString();
        String experienciaSeleccionada = spinnerExperiencia.getSelectedItem().toString();

        usuario.setTipotiempo(tipoTiempoSeleccionado);
        usuario.setExperiencia(experienciaSeleccionada);
    }

    private void checkboxUsuario(Usuario usuario){
        boolean tieneReferencias = checkBoxReferencia.isChecked();
        String referencia = " ";
        if(tieneReferencias){
            referencia = "Si";
            usuario.setReferencias(referencia);
        }else{
            referencia = "No";
            usuario.setReferencias(referencia);
        }
    }

    private void sobreUsuario(Usuario usuario){
        String informacion = edtSobreMi.getText().toString();
        usuario.setSobremi(informacion);
    }

    private void configurarTextosPorPerfil(Usuario usuario) {

        if (usuario.getCuidado() != null && usuario.getCuidado()) {
            // Perfil: soy cuidador/a
            cambio2.setText("Mi Experiencia");
            cambio3.setText("¿Cuántos años de experiencia tienes?");
            cambio4.setText("Tengo referencias");
        } else {
            cambio1.setVisibility(View.VISIBLE);
            // Perfil: busco cuidador/a
            cambio1.setText("Describe lo que Buscas");
            cambio2.setText("Experiencia");
            cambio3.setText("¿Cuántos años de experiencia?");
            cambio4.setText("¿Con Referencias?");
        }
    }

    private void guardarPerfilEnSupabase(Usuario usuario) {
        new Thread(() -> {
            try {
                // Obtener id del usuario desde SharedPreferences (guardado en Registro)
                String userId = getSharedPreferences("session", MODE_PRIVATE).getString("user_id", null);
                if (userId == null) {
                    runOnUiThread(() -> Toast.makeText(Perfil.this, "Error: usuario no autenticado", Toast.LENGTH_LONG).show());
                    return;
                }

                JSONObject json = new JSONObject();
                json.put("id", userId);  // UUID, PK y FK
                json.put("fecha_nacimiento", formatearFechaParaBD(usuario.getFechaNacimiento())); // Debes asegurarte de que sea formato ISO yyyy-MM-dd
                json.put("sexo", usuario.getGenero());

                // Según tabla solo un campo salario numeric, por ejemplo puedes usar salarioDesde o promedio
                double salario = calcularSalarioPromedio(usuario.getSalarioDesde(), usuario.getSalarioHasta());
                json.put("salario", salario);

                json.put("experiencia", usuario.getExperiencia());
                json.put("referencias", usuario.getReferencias());
                json.put("tipo_tiempo", usuario.getTipotiempo());
                // En la tabla tienes "describe_busqueda" para "Describe lo que buscas"
                // Aquí asumo que ese texto está en alguna variable que debes capturar, por ejemplo si es "cambio1" visible:
                json.put("describe_busqueda", obtenerDescribeBusquedaSegunPerfil(usuario));
                json.put("sobre_mi", usuario.getSobremi());

                URL url = new URL(SUPABASE_URL + "/rest/v1/informacion_adicional");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", SUPABASE_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                conn.setRequestProperty("Prefer", "return=minimal");  // No devuelve contenido
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(Perfil.this, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Perfil.this, "Error al guardar perfil: " + responseCode, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(Perfil.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // Formatea fecha de dd/MM/yyyy a yyyy-MM-dd (ISO) para BD
    private String formatearFechaParaBD(String fecha) {
        try {
            String[] partes = fecha.split("/");
            if (partes.length == 3) {
                return partes[2] + "-" + (partes[1].length() == 1 ? "0" + partes[1] : partes[1]) + "-" + (partes[0].length() == 1 ? "0" + partes[0] : partes[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // o fecha sin formato
    }

    // Calcula promedio de salario desde y hasta, o solo uno si solo uno está definido
    private double calcularSalarioPromedio(String desde, String hasta) {
        try {
            double desdeVal = Double.parseDouble(desde);
            double hastaVal = Double.parseDouble(hasta);
            return (desdeVal + hastaVal) / 2;
        } catch (Exception e) {
            try {
                return Double.parseDouble(desde);
            } catch (Exception ex) {
                return 0;
            }
        }
    }

    // Obtiene "describe_busqueda" dependiendo del perfil (si es cuidador o paciente)
    private String obtenerDescribeBusquedaSegunPerfil(Usuario usuario) {
        // Por ejemplo, si tienes un EditText para esta info, o si usas cambio1 (como label)
        // Aquí asumo que para paciente usas un campo para descripción de búsqueda, para cuidador vacío o algo
        if (usuario.getCuidado() != null && usuario.getCuidado()) {
            return ""; // o algún texto específico para cuidador
        } else {
            // Si tienes un EditText o algo para describir búsqueda, deberías obtenerlo
            // Por ejemplo, si usas edtDescribeBusqueda:
            // return edtDescribeBusqueda.getText().toString();
            return cambio1.getText().toString(); // Solo de ejemplo, reemplaza por tu campo real
        }
    }

    private Usuario crearUsuarioDesdeCampos() {
        Usuario usuario = new Usuario();

        String nombreCompleto = edtNombre.getText().toString().trim();
        String[] partes = nombreCompleto.split(" ", 2);
        if (partes.length == 2) {
            usuario.setNombre(partes[0]);
            usuario.setApellido(partes[1]);
        } else {
            usuario.setNombre(nombreCompleto);
            usuario.setApellido("");
        }

        usuario.setDireccion(edtDireccion.getText().toString());
        usuario.setFechaNacimiento(edtFechaNacimiento.getText().toString());

        int idSeleccionado = sexoGroup.getCheckedRadioButtonId();
        if (idSeleccionado == R.id.rbMujer) {
            usuario.setGenero("Mujer");
        } else if (idSeleccionado == R.id.rbHombre) {
            usuario.setGenero("Hombre");
        } else {
            usuario.setGenero("");
        }

        usuario.setSalarioDesde(salarioDesde.getText().toString());
        usuario.setSalarioHasta(salarioHasta.getText().toString());

        usuario.setTipotiempo(spinnerTipoTiempo.getSelectedItem().toString());
        usuario.setExperiencia(spinnerExperiencia.getSelectedItem().toString());

        usuario.setReferencias(checkBoxReferencia.isChecked() ? "Si" : "No");
        usuario.setSobremi(edtSobreMi.getText().toString());

        // Aquí puedes asignar otros campos si es necesario

        return usuario;
    }

}
