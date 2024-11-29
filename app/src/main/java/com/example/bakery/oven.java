package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class oven extends android.app.Activity {

    private TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oven);

        // Initialize timer text view
        timerText = findViewById(R.id.timerText);

        // Start the countdown timer
        startCountdownTimer();
    }

    private void startCountdownTimer() {
        // Create a 10-second countdown timer with 1-second intervals
        new CountDownTimer(10000, 1000) { // 10 seconds = 10000ms
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // When the countdown finishes
                Toast.makeText(oven.this, "Baking done! Moving to the next step.", Toast.LENGTH_SHORT).show();

                // Navigate to the finishcake activity
                Intent intent = new Intent(oven.this, finishcake.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Ensure oven activity is removed from the stack
            }
        }.start();
    }
}
