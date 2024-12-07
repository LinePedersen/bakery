package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

public class oven extends RobotActivity implements RobotLifecycleCallbacks {

    private TextView timerText;
    private QiContext qiContext; // QiContext for robot interaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oven);

        // Initialize timer text view
        timerText = findViewById(R.id.timerText);

        // Start the countdown timer
        startCountdownTimer();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        // Speak initial instructions when the activity starts
        sayText("Welcome to the oven! Let's bake your cake. The timer will start now.");
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null; // Clear QiContext reference when focus is lost
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Handle focus refusal if needed
    }

    // Helper method to make Pepper speak
    private void sayText(String text) {
        if (qiContext != null) {
            Say say = SayBuilder.with(qiContext)
                    .withText(text)
                    .build();
            say.run();
        }
    }

    private void startCountdownTimer() {
        // Create a 10-second countdown timer with 1-second intervals
        new CountDownTimer(10000, 1000) { // 10 seconds = 10000ms
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;

                // Update the TextView with the remaining time
                timerText.setText(String.valueOf(secondsLeft));

                // Pepper announces halfway mark and last 3 seconds
                if (secondsLeft == 5) {
                    sayText("We are halfway through the baking!");
                } else if (secondsLeft <= 3) {
                    sayText("Only " + secondsLeft + " seconds left!");
                }
            }

            @Override
            public void onFinish() {
                // When the countdown finishes
                String finishMessage = "Baking is done! Let's move to the next step.";
                sayText(finishMessage);
                Toast.makeText(oven.this, finishMessage, Toast.LENGTH_SHORT).show();

                // Navigate to the finishcake activity
                Intent intent = new Intent(oven.this, finishcake.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Ensure oven activity is removed from the stack
            }
        }.start();
    }
}
