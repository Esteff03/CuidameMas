package com.synunezcamacho.cuidame;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

            Intent intent = new Intent(Perfil.this, PerfilPublico.class);
            // Pasar el objeto Usuario al Intent
            intent.putExtra("perfil", usuario);
            startActivity(intent);
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
        String salarioDesdeText = salarioDesde.getText().toString();
        String salarioHastaText = salarioHasta.getText().toString();
        double salarioDesdeValor = Double.parseDouble(salarioDesdeText);
        double salarioHastaValor = Double.parseDouble(salarioHastaText);

            if (salarioHastaValor < salarioDesdeValor) {
            } else {
                usuario.setSalarioDesde(salarioDesdeText);
                usuario.setSalarioHasta(salarioHastaText);
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

    private void sobreUsuario(Usuario usuario){
        String informacion = edtSobreMi.getText().toString();
        usuario.setSobremi(informacion);
    }
    }