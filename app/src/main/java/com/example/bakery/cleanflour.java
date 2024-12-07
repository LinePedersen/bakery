package com.example.bakery;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

public class cleanflour extends RobotActivity implements RobotLifecycleCallbacks {

    private ImageView flour, box;
    private TextView explanation;
    private int wrongAttempts = 0; // Counter for wrong attempts
    private QiContext qiContext; // QiContext for robot interaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleanflour);

        // Initialize views
        flour = findViewById(R.id.flour);
        box = findViewById(R.id.box);
        explanation = findViewById(R.id.caption);

        // Set tags for views (if not already set in XML)
        flour.setTag("flour");
        findViewById(R.id.eggs).setTag("eggs");
        findViewById(R.id.sugar).setTag("sugar");
        findViewById(R.id.strawberries).setTag("strawberries");
        findViewById(R.id.chocolate).setTag("chocolate");
        findViewById(R.id.whisk).setTag("whisk");
        findViewById(R.id.bakingtin).setTag("bakingtin");

        // Set TouchListener for draggable items
        setDraggableListeners(R.id.flour, R.id.eggs, R.id.sugar, R.id.strawberries, R.id.chocolate, R.id.whisk, R.id.bakingtin);

        // Set DragListener for the target box
        box.setOnDragListener(new DragEventListener());
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        // Speak the initial instructions from the TextView
        if (explanation != null) {
            String captionText = explanation.getText().toString();
            sayText(captionText);
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

    // Helper method to make Pepper speak a given text
    private void sayText(String text) {
        if (qiContext != null) {
            Say say = SayBuilder.with(qiContext)
                    .withText(text)
                    .build();
            say.run();
        }
    }

    private void setDraggableListeners(int... viewIds) {
        for (int id : viewIds) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnTouchListener(new DragTouchListener());
            }
        }
    }

    // TouchListener for drag initiation
    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Create a ClipData holding the item's tag
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                ClipData dragData = new ClipData(
                        (CharSequence) v.getTag(),
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);

                // Create and start the drag
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                v.startDrag(dragData, myShadow, v, 0);

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

                    Log.d("TAG_CHECK", "Dragged tag: " + draggedTag);

                    if ("flour".equals(draggedTag)) {
                        // Correct item
                        String successMessage = "Correct! Moving to the next step.";
                        updateCaption(successMessage);
                        sayText(successMessage);

                        // Move to the next activity
                        Intent intent = new Intent(cleanflour.this, cleaneggs.class);
                        startActivity(intent);
                        return true;
                    } else {
                        // Incorrect item
                        wrongAttempts++;
                        String failureMessage = "This is not the right item. Try again.";
                        updateCaption(failureMessage);
                        sayText(failureMessage);

                        if (wrongAttempts >= 3) {
                            String helpMessage = "Let me help you!";
                            updateCaption(helpMessage);
                            sayText(helpMessage);
                            autoDropFlour();
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

    // Helper method to update the TextView caption
    private void updateCaption(String text) {
        runOnUiThread(() -> explanation.setText(text)); // Ensure this runs on the main thread
    }

    // Handle "auto-drop" by simulating the correct outcome
    private void autoDropFlour() {
        new Handler().postDelayed(() -> {
            // Directly call the success logic
            String autoDropMessage = "Let me help you! Moving to the next step.";
            updateCaption(autoDropMessage);
            sayText(autoDropMessage);

            Intent intent = new Intent(cleanflour.this, cleaneggs.class);
            startActivity(intent);
        }, 2000); // Delay to simulate "helping"
    }
}
