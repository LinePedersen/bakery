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
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class cleaneggs extends RobotActivity implements RobotLifecycleCallbacks {

    private ImageView eggs, box;
    private TextView explanation;
    private int wrongAttempts = 0; // Counter for wrong attempts
    private QiContext qiContext; // QiContext for robot interaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleaneggs);

        // Initialize views
        eggs = findViewById(R.id.eggs);
        box = findViewById(R.id.box);
        explanation = findViewById(R.id.caption);

        // Set tags for views (if not already set in XML)
        eggs.setTag("eggs");
        findViewById(R.id.sugar).setTag("sugar");
        findViewById(R.id.strawberries).setTag("strawberries");
        findViewById(R.id.chocolate).setTag("chocolate");
        findViewById(R.id.whisk).setTag("whisk");
        findViewById(R.id.bakingtin).setTag("bakingtin");

        // Set TouchListener for draggable items
        eggs.setOnTouchListener(new DragTouchListener());
        findViewById(R.id.sugar).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.strawberries).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.chocolate).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.whisk).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.bakingtin).setOnTouchListener(new DragTouchListener());

        // Set DragListener for the target box
        box.setOnDragListener(new DragEventListener());
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext; // Save the QiContext for later use

        // Speak the initial instruction
        String initialMessage = "Drag the eggs to the box to clean them.";
        updateCaption(initialMessage);
        sayText(initialMessage);
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null; // Clear QiContext when focus is lost
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Handle focus refusal if needed
    }

    // Helper method to make Pepper speak a given text
    private void sayText(String text) {
        if (qiContext != null) {
            Say say = SayBuilder.with(qiContext)
                    .withText(text)
                    .build();
            say.run();
        }
    }

    // TouchListener for drag initiation
    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Create a ClipData holding the item's ID
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                ClipData dragData = new ClipData(
                        (CharSequence) v.getTag(),
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);

                // Create and start the drag
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                v.startDrag(dragData, myShadow, v, 0); // Use startDrag for API 23

                // Perform click for accessibility compliance
                v.performClick();
                return true;
            }
            return false;
        }
    }

    // DragListener for handling drop events
    private class DragEventListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Accept drag only for plain text
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5f); // Highlight the target
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f); // Remove highlight
                    return true;

                case DragEvent.ACTION_DROP:
                    // Retrieve the dragged view's tag
                    View draggedView = (View) event.getLocalState();
                    String draggedTag = (String) draggedView.getTag();

                    if ("eggs".equals(draggedTag)) {
                        // Correct item
                        String successMessage = "Correct! Moving to the next step.";
                        updateCaption(successMessage); // Update TextView
                        sayText(successMessage); // Make Pepper say the message

                        // Move to the next activity
                        Intent intent = new Intent(cleaneggs.this, cleanwhisk.class);
                        startActivity(intent);
                        finish();
                        return true;
                    } else {
                        // Incorrect item
                        wrongAttempts++;
                        String failureMessage = "This is not the right item. Try again.";
                        updateCaption(failureMessage); // Update TextView
                        sayText(failureMessage); // Make Pepper say the message

                        if (wrongAttempts >= 3) {
                            String helpMessage = "Let me help you!";
                            updateCaption(helpMessage); // Update TextView
                            sayText(helpMessage); // Make Pepper say the message
                            autoDropEggs(); // Simulate the correct action after 3 wrong attempts
                        }
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f); // Reset target highlight
                    return true;

                default:
                    break;
            }
            return false;
        }
    }

    // Update the TextView content
    private void updateCaption(String text) {
        runOnUiThread(() -> explanation.setText(text)); // Ensure this runs on the main thread
    }

    // Handle "auto-drop" by simulating the correct outcome
    private void autoDropEggs() {
        new Handler().postDelayed(() -> {
            String autoDropMessage = "Let me help you! Moving to the next step.";
            updateCaption(autoDropMessage); // Update TextView
            sayText(autoDropMessage); // Make Pepper say the message

            Intent intent = new Intent(cleaneggs.this, cleanwhisk.class);
            startActivity(intent);
            finish();
        }, 2000); // Delay to simulate "helping"
    }
}
