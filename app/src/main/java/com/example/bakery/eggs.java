package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//goes from eggs to sugar
public class eggs extends android.app.Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eggs);
        Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent sugar = new Intent (eggs.this, sugar.class);
                startActivity(sugar);
            }
        });




    }}
