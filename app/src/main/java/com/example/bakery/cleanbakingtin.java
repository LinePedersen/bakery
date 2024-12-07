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

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

public class cleanbakingtin extends RobotActivity implements RobotLifecycleCallbacks {

    private ImageView bakingtin, box;
    private int wrongAttempts = 0; // Counter for wrong attempts
    private QiContext qiContext; // QiContext for robot interaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleanbakingtin);

        // Initialize views
        bakingtin = findViewById(R.id.bakingtin);
        box = findViewById(R.id.box);

        // Set tags for views (if not already set in XML)
        bakingtin.setTag("bakingtin");
        findViewById(R.id.strawberries).setTag("strawberries");

        // Set TouchListener for draggable items
        setDraggableListeners(R.id.bakingtin, R.id.strawberries);

        // Set DragListener for the target box
        box.setOnDragListener(new DragEventListener());
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        // Optionally, say instructions here if needed
        sayText("Please place the baking tin into the box.");
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

                    Log.d("TAG_CHECK", "Dragged tag: " + draggedTag);

                    if ("bakingtin".equals(draggedTag)) {
                        // Correct item
                        sayText("Correct! Moving to the next step.");
                        navigateToNextStep();
                        return true;
                    } else {
                        // Incorrect item
                        wrongAttempts++;
                        sayText("This is not the right item. Try again.");

                        if (wrongAttempts >= 3) {
                            sayText("Let me help you!");
                            autoDropBakingTin();
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
    private void autoDropBakingTin() {
        new Handler().postDelayed(() -> {
            // Directly call the success logic
            sayText("Let me help you! Moving to the next step.");
            navigateToNextStep();
        }, 2000); // Delay to simulate "helping"
    }

    private void navigateToNextStep() {
        Intent intent = new Intent(cleanbakingtin.this, cleanstrawberries.class);
        startActivity(intent);
    }
}
