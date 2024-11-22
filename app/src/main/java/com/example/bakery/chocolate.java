package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class chocolate extends android.app.Activity {
    //chocolate to baking tin
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chocolate);
        Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent bakintin = new Intent (chocolate.this, bakingtin.class);
                startActivity(bakintin);
            }
        });
    }}
