package com.infobk.carpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
Button kaydet;
Button bul;
ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        kaydet = findViewById(R.id.buttonKaydet);
        bul = findViewById(R.id.buttonNerede);
        im=findViewById(R.id.imageView3);


        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i̇ntent = new Intent(MainActivity.this,MapsActivity.class);
                i̇ntent.putExtra("info","name");
                startActivity(i̇ntent);



            }
        });

        bul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SaveActivity.class);
                startActivity(intent);

            }
        });



    }



}