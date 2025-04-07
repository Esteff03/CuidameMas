package com.synunezcamacho.cuidame;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);


        ImageView fondo = findViewById(R.id.fondo);

        Glide.with(this)
                .load(R.drawable.fondo) // Tu imagen de fondo
                .transform(new CenterCrop(), new BlurTransformation(10, 1)) // <-- Aquí aplicamos el desenfoque
                .into(fondo);

    }
}