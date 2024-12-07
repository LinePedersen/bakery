package com.example.bakery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        // Set up the "Next" button to navigate to the next activity
        Button yes = findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent introduction = new Intent(MainActivity.this, introduction.class);
                startActivity(introduction);
            }
        });

        // Set up the "Repeat" button to repeat the speech
        Button repeat = findViewById(R.id.repeat);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qiContext != null) {
                    // Get the text from the TextView
                    TextView captionTextView = findViewById(R.id.caption);
                    String captionText = captionTextView.getText().toString();

                    // Create and run the speech action
                    Say say = SayBuilder.with(qiContext)
                            .withText(captionText)
                            .build();
                    say.run();
                }
            }
        });
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext; // Save qiContext for later use

        // Get the text from the TextView
        TextView captionTextView = findViewById(R.id.caption);
        String captionText = captionTextView.getText().toString();

        // Initial speech when activity starts
        Say say = SayBuilder.with(qiContext)
                .withText(captionText)
                .build();
        say.run();
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null; // Clear qiContext when focus is lost
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Handle focus refused if needed
    }
}

