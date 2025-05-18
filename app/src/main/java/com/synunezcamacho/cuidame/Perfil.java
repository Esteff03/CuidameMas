package com.synunezcamacho.cuidame;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class Perfil extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    Spinner spinnerTipoTiempo, spinnerExperiencia;
    Toolbar toolbar;
    Bundle bundle;
    EditText edtNombre, edtDireccion, edtFechaNacimiento;
    EditText salarioDesde, salarioHasta, edtSobreMi;
    private String imgSeleccionada = null;
    ImageView imgPerfil;
    RadioGroup sexoGroup;
    RadioButton rbMuje, rbHombre;
    CheckBox checkBoxReferencia;
    Button btnVerPerfilPublico, btnGuardar;
    TextView cambio1, cambio2,cambio3, cambio4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        imgPerfil = findViewById(R.id.imgPerfil);
        btnGuardar = findViewById(R.id.btnGuardar);

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
        nav_menu.setSelectedItemId(R.id.page_search);

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
        usuario.setImgPerfil(imgSeleccionada != null ? imgSeleccionada : "android.resource://" + getPackageName() + "/" + R.drawable.camara);
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


        //Parte para acceder a la galeria
        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria();
            }
        });


 /*
        //la parte del menu_nav

        nav_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        startActivity(new Intent(Perfil.this, MapaActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.page_fav:
                        startActivity(new Intent(Perfil.this, ChatActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.page_search:
                        // Ya estás en perfil
                        return true;
                }
                return false;
            }
        });
*/
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

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            imgSeleccionada = selectedImageUri.toString();
            imgPerfil.setImageURI(selectedImageUri);
        }
    }


}
