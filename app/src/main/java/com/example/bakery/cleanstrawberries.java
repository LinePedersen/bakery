package com.example.bakery;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

public class cleanstrawberries extends RobotActivity implements RobotLifecycleCallbacks {

    private ImageView strawberries, kitchen;
    private TextView explanation;
    private int wrongAttempts = 0; // Counter for wrong attempts
    private boolean isTransitioning = false; // Prevent multiple transitions
    private QiContext qiContext; // QiContext for robot interaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleanstrawberries);

        // Initialize views
        strawberries = findViewById(R.id.strawberries);
        kitchen = findViewById(R.id.kitchen);
        explanation = findViewById(R.id.caption);

        // Set tags for drag and drop
        strawberries.setTag("strawberries");

        // Set TouchListener for strawberries
        strawberries.setOnTouchListener(new DragTouchListener());

        // Set DragListener for the kitchen
        kitchen.setOnDragListener(new DragEventListener());
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        // Speak the initial instructions
        if (explanation != null) {
            String initialText = explanation.getText().toString();
            sayText(initialText);
        }
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
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

    // TouchListener to start the drag action
    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                ClipData dragData = new ClipData(
                        (CharSequence) v.getTag(),
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(dragData, shadowBuilder, v, 0);
                v.performClick();  // For accessibility compliance
                return true;
            }
            return false;
        }
    }

    // DragListener to handle drop events
    private class DragEventListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5f); // Highlight the drop target
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f); // Reset drop target appearance
                    return true;

                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    String draggedTag = (String) draggedView.getTag();

                    if ("strawberries".equals(draggedTag)) {
                        // Correct item
                        String successMessage = "Good job! Moving to the next step.";
                        updateCaption(successMessage);
                        sayText(successMessage);
                        navigateToNextStep();
                        return true;
                    } else {
                        // Incorrect item
                        wrongAttempts++;
                        String failureMessage = "This is not the right item. Try again.";
                        updateCaption(failureMessage);
                        sayText(failureMessage);

                        if (wrongAttempts >= 3) {
                            String helpMessage = "Hint: Drag the strawberries to the kitchen!";
                            updateCaption(helpMessage);
                            sayText(helpMessage);
                            autoDropStrawberries();
                        }
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f); // Reset drop target appearance
                    return true;

                default:
                    return false;
            }
        }
    }

    // Update the caption (text feedback)
    private void updateCaption(String text) {
        runOnUiThread(() -> explanation.setText(text)); // Ensure this runs on the main thread
    }

    // Navigate to the next step (cleankitchen activity)
    private void navigateToNextStep() {
        if (isTransitioning) return;
        isTransitioning = true;

        Intent intent = new Intent(cleanstrawberries.this, cleankitchen.class);
        startActivity(intent);
        finish(); // Ensure cleanstrawberries is removed from the stack
    }

    // Automatically help the user after multiple incorrect attempts
    private void autoDropStrawberries() {
        new Handler().postDelayed(() -> {
            String autoDropMessage = "Let me help you! Moving to the next step.";
            updateCaption(autoDropMessage);
            sayText(autoDropMessage);
            navigateToNextStep();
        }, 2000); // 2-second delay to simulate help
    }
}
