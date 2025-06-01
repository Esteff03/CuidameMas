package com.synunezcamacho.cuidame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {

    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
/*
        imagen = findViewById(R.id.logo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        // Escucha el fin de la animación
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Puedes dejarlo vacío
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Cuando termina la animación, ir a Welcome
                Intent intent = new Intent(Splash.this, Welcome.class);
                startActivity(intent);
                finish(); // Finaliza el splash para que no se pueda volver con "atrás"
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Puedes dejarlo vacío
            }
        });

        imagen.startAnimation(rotate);*/

        setContentView(R.layout.activity_splash); // tu layout con la animación

        LottieAnimationView lottie = findViewById(R.id.lottie);
        lottie.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Ir a la siguiente actividad
                startActivity(new Intent(Splash.this, Welcome.class));
                finish();
            }
        });
    }
}
