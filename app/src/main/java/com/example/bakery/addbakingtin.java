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

import androidx.appcompat.app.AppCompatActivity;

public class addbakingtin extends android.app.Activity{

    private ImageView bowl, bakingtin;
    private TextView explanation;
    private int wrongAttempts = 0; // Counter for wrong attempts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbakingtin);

        // Initialize views
        bowl = findViewById(R.id.bowl);
        bakingtin = findViewById(R.id.bakingtin);
        explanation = findViewById(R.id.explanation);

        // Set tags for views (if not already set in XML)
        bowl.setTag("bowl");
        bakingtin.setTag("bakingtin");

        // Set TouchListener for draggable items
        bowl.setOnTouchListener(new DragTouchListener());

        // Set DragListener for the target bakingtin
        bakingtin.setOnDragListener(new DragEventListener());
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

                    if ("bowl".equals(draggedTag)) {
                        // Correct item
                        Toast.makeText(addbakingtin.this, "Great! Moving to the next step.", Toast.LENGTH_SHORT).show();
                        // Move to oven activity
                        Intent intent = new Intent(addbakingtin.this, oven.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear previous activities
                        startActivity(intent);
                        finish(); // Ensure addbakingtin is removed from the stack
                        return true;
                    } else {
                        // Incorrect item
                        wrongAttempts++;
                        Toast.makeText(addbakingtin.this, "This is not the right item. Try again.", Toast.LENGTH_SHORT).show();

                        if (wrongAttempts >= 3) {
                            explanation.setText("Let me help you!");
                            autoDropBowl();
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

    // Handle "auto-drop" by simulating the correct outcome
    private void autoDropBowl() {
        new Handler().postDelayed(() -> {
            // Directly call the success logic
            Toast.makeText(addbakingtin.this, "Let me help you! Moving to the next step.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(addbakingtin.this, oven.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear previous activities
            startActivity(intent);
            finish(); // Ensure addbakingtin is removed from the back stack
        }, 2000); // Delay to simulate "helping"
    }
}
