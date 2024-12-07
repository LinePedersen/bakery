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
import androidx.appcompat.app.AppCompatActivity;

public class finish extends RobotActivity implements RobotLifecycleCallbacks {
    private QiContext qiContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish); // Ensure the layout is named "finish.xml"
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext; // Save the QiContext for later use

        // Get the text from the TextView
        TextView captionTextView = findViewById(R.id.caption);
        String captionText = captionTextView.getText().toString();

        // Make Pepper speak the text
        Say say = SayBuilder.with(qiContext)
                .withText(captionText)
                .build();
        say.run();
    }


    @Override
    public void onRobotFocusLost() {
        this.qiContext = null; // Clear QiContext when focus is lost
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Handle focus refusal if needed
    }
}