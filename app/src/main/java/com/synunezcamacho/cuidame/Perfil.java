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
    Button btnVerPerfilPublico;
    TextView cambio1, cambio2,cambio3, cambio4;

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

        //cambio de texto buscoCudiador o SoyCuidador
        cambio1 = findViewById(R.id.cambio1);
        cambio2 = findViewById(R.id.cambio2);
        cambio3 = findViewById(R.id.cambio3);
        cambio4 = findViewById(R.id.cambio4);


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

}
