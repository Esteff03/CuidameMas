package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilPublico extends AppCompatActivity {
    Toolbar toolbar;
    TextView txtNombre, txtFechaNacimiento, txtSexo, txtDireccion;
    TextView txtSalario, txtExperiencia, txtReferencia, txtTipoTiempo, txtSobreMi;
    ImageView imgAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_publico);

        // la barra del toolbar
        toolbar = findViewById(R.id.miToolbar);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.principal));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombre = findViewById(R.id.txtNombre);
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        txtSexo = findViewById(R.id.txtSexo);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtSalario = findViewById(R.id.txtSalario);
        txtExperiencia = findViewById(R.id.txtExperiencia);
        txtReferencia = findViewById(R.id.txtReferencia);
        txtTipoTiempo = findViewById(R.id.txtTipoTiempo);
        txtSobreMi = findViewById(R.id.txtSobreMi);

        imgAtras = findViewById(R.id.btnAtras);

        //boton para atras
        imgAtras.setOnClickListener(v -> {
            finish();
        });

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

    }
}