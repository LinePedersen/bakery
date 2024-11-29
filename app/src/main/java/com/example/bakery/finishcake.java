package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class finishcake extends android.app.Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cake);
        Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent cleanup = new Intent (finishcake.this, cleanup.class);
                startActivity(cleanup);
            }
        });


    }}
