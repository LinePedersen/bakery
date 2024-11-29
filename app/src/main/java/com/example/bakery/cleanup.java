package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class cleanup extends android.app.Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleanup);
        Button yes = (Button) findViewById(R.id.yes);

        yes.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent cleanflour = new Intent (cleanup.this, cleanflour.class);
                startActivity(cleanflour);
            }
        });


    }}