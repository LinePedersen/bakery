package com.example.bakery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

//goes from welcoome to introduction
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.welcome);
    Button yes = (Button) findViewById(R.id.yes);

    yes.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v) {
            Intent introdcution = new Intent (MainActivity.this, introduction.class);
            startActivity(introdcution);
        }
    });
}}



