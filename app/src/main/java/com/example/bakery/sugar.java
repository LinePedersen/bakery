package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//sugar to flour
public class sugar extends android.app.Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sugar);
        Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent sugar = new Intent (sugar.this, flour.class);
                startActivity(flour);
            }
        });




    }}

}
