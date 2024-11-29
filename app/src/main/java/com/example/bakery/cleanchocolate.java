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

public class cleanchocolate extends android.app.Activity {

    private ImageView chocolate, box;
    private TextView explanation;
    private int wrongAttempts = 0; // Counter for wrong attempts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleanchocolate);

        // Initialize views
        chocolate = findViewById(R.id.chocolate);
        box = findViewById(R.id.box);
        explanation = findViewById(R.id.explanation);

        // Set tags for views (if not already set in XML)
        chocolate.setTag("chocolate");
        findViewById(R.id.bowl).setTag("bowl");
        findViewById(R.id.eggs).setTag("eggs");
        findViewById(R.id.flour).setTag("flour");
        findViewById(R.id.whisk).setTag("whisk");
        findViewById(R.id.strawberries).setTag("strawberries");
        findViewById(R.id.sugar).setTag("sugar");

        // Set TouchListener for draggable items
        chocolate.setOnTouchListener(new DragTouchListener());
        findViewById(R.id.bowl).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.eggs).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.flour).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.whisk).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.strawberries).setOnTouchListener(new DragTouchListener());
        findViewById(R.id.sugar).setOnTouchListener(new DragTouchListener());

        // Set DragListener for the target box
        box.setOnDragListener(new DragEventListener());
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

                    if ("chocolate".equals(draggedTag)) {
                        // Correct item
                        Toast.makeText(cleanchocolate.this, "Correct! Moving to the next step.", Toast.LENGTH_SHORT).show();
                        // Move to cleanbakingtin activity
                        Intent intent = new Intent(cleanchocolate.this, cleanbakingtin.class);
                        startActivity(intent);
                        return true;
                    } else {
                        // Incorrect item
                        wrongAttempts++;
                        Toast.makeText(cleanchocolate.this, "This is not the right item. Try again.", Toast.LENGTH_SHORT).show();

                        if (wrongAttempts >= 3) {
                            explanation.setText("Let me help you!");
                            autoDropChocolate();
                        }
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f); // Reset target highlight
                    return true;

                default:
                    return false;
            }
        }
    }

    // Handle "auto-drop" by simulating the correct outcome
    private void autoDropChocolate() {
        new Handler().postDelayed(() -> {
            // Directly call the success logic
            Toast.makeText(cleanchocolate.this, "Let me help you! Moving to the next step.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(cleanchocolate.this, cleanbakingtin.class);
            startActivity(intent);
        }, 2000); // Delay to simulate "helping"
    }
}
