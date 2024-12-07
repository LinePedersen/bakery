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
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;

import androidx.appcompat.app.AppCompatActivity;

public class whisk1 extends android.app.Activity {

    private ImageView whisk, bowl;
    private TextView explanation;
    private int wrongAttempts = 0; // Counter for wrong attempts
    private QiContext qiContext; // QiContext for Pepper's speech

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whisk1);

        // Initialize views
        whisk = findViewById(R.id.whisk);
        bowl = findViewById(R.id.bowl);
        explanation = findViewById(R.id.caption);

        // Set tags for views (if not already set in XML)
        whisk.setTag("whisk");
        findViewById(R.id.eggs).setTag("eggs");
        findViewById(R.id.sugar).setTag("sugar");
        findViewById(R.id.flour).setTag("flour");
        findViewById(R.id.strawberries).setTag("strawberries");
        findViewById(R.id.chocolate).setTag("chocolate");
        findViewById(R.id.bakingtin).setTag("bakingtin");

        // Set TouchListener for draggable items
        whisk.setOnTouchListener(new DragTouchListener());
        findViewById(R.id.eggs).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.sugar).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.flour).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.strawberries).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.chocolate).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.bakingtin).setOnTouchListener(new DragTouchListener());

        // Set DragListener for the target bowl
        bowl.setOnDragListener(new DragEventListener());
    }


    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        String initialText = explanation.getText().toString();
        sayText(initialText); // Pepper speaks the initial text
    }


    public void onRobotFocusLost() {
        this.qiContext = null;
    }

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

                    if ("whisk".equals(draggedTag)) {
                        // Correct item
                        String successMessage = "Correct! Moving to the next step.";
                        updateCaption(successMessage);
                        sayText(successMessage);
                        // Move to addflour activity
                        navigateToAddFlour();
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
                            autoDropWhisk();
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

    private void updateCaption(String text) {
        runOnUiThread(() -> explanation.setText(text)); // Ensure this runs on the main thread
    }

    // Handle "auto-drop" by simulating the correct outcome
    private void autoDropWhisk() {
        new Handler().postDelayed(() -> {
            // Directly call the success logic
            String autoDropMessage = "Let me help you! Moving to the next step.";
            updateCaption(autoDropMessage);
            sayText(autoDropMessage);
            navigateToAddFlour();
        }, 2000); // Delay to simulate "helping"
    }

    private void navigateToAddFlour() {
        // Move to the next activity (addflour)
        Intent intent = new Intent(whisk1.this, addflour.class);
        startActivity(intent);
        finish(); // Ensure whisk1 is removed from the stack
    }
}
