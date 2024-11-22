package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class strawberries extends android.app.Activity{
    //strawberries to chocolate

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strawberry);
        Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent chocolate = new Intent (strawberries.this, chocolate.class);
                startActivity(chocolate);
            }
        });
}}
