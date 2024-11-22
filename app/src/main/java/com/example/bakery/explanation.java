package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//explantion to  addsugar
public class explanation extends android.app.Activity {
    //bowl to task explanation
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explanation);
        Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent addsugar= new Intent (explanation.this, addsugar.class);
                startActivity(addsugar);
            }
        });
    }}