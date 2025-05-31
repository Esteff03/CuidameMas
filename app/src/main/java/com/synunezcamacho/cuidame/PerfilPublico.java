package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilPublico extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txtNombre, txtFechaNacimiento, txtSexo, txtDireccion;
    private TextView txtSalario, txtExperiencia, txtReferencia, txtTipoTiempo, txtSobreMi;
    private ImageView imgAtras;
    private BottomNavigationView botonNavigationView;

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

        //actividad de nav_menu
        botonNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.page_chat) {
                startActivity(new Intent(PerfilPublico.this, Contacto.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.page_mapa) {
                startActivity(new Intent(PerfilPublico.this, Mapa.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.page_perfil) {
                // Ya estamos en PerfilPublico, no hacemos nada
                return true;
            }
            return false;
        });

    }
}