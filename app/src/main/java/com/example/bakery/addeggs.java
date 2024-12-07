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
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class addeggs extends RobotActivity implements RobotLifecycleCallbacks {

    private ImageView eggs, bowl;
    private TextView explanation;
    private int wrongAttempts = 0;
    private boolean isTransitioning = false; // Prevent multiple transitions
    private QiContext qiContext; // QiContext for robot interaction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addeggs);

        // Initialize views
        eggs = findViewById(R.id.eggs);
        bowl = findViewById(R.id.bowl);
        explanation = findViewById(R.id.caption);

        // Initialize touch and drag listeners
        initializeDragAndDrop();
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

    private void initializeDragAndDrop() {
        // Set tags for draggable items
        eggs.setTag("eggs");
        findViewById(R.id.sugar).setTag("sugar");
        findViewById(R.id.flour).setTag("flour");
        findViewById(R.id.strawberries).setTag("strawberries");
        findViewById(R.id.chocolate).setTag("chocolate");
        findViewById(R.id.whisk).setTag("whisk");
        findViewById(R.id.bakingtin).setTag("bakingtin");

        // Set TouchListener for draggable items
        setDraggableListeners(R.id.eggs, R.id.sugar, R.id.flour, R.id.strawberries, R.id.chocolate, R.id.whisk, R.id.bakingtin);

        // Set DragListener for the target bowl
        bowl.setOnDragListener(new DragEventListener());
    }

    private void setDraggableListeners(int... viewIds) {
        for (int id : viewIds) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnTouchListener(new DragTouchListener());
            }
        }
    }

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
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(dragData, shadowBuilder, v, 0);

                // Perform click for accessibility compliance
                v.performClick();
                return true;
            }
            return false;
        }
    }

    private class DragEventListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5f);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f);
                    return true;

                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    String draggedTag = (String) draggedView.getTag();

                    if ("eggs".equals(draggedTag)) {
                        String successMessage = "Correct! Moving to the next step.";
                        updateCaption(successMessage);
                        sayText(successMessage);
                        navigateToWhisk();
                        return true;
                    } else {
                        wrongAttempts++;
                        String failureMessage = "This is not the right item. Try again.";
                        updateCaption(failureMessage);
                        sayText(failureMessage);

                        if (wrongAttempts >= 3) {
                            String helpMessage = "Let me help you!";
                            updateCaption(helpMessage);
                            sayText(helpMessage);
                            autoDropEggs();
                        }
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    return true;

                default:
                    return false;
            }
        }
    }

    private void updateCaption(String text) {
        runOnUiThread(() -> explanation.setText(text)); // Ensure this runs on the main thread
    }

    private void autoDropEggs() {
        new Handler().postDelayed(() -> {
            if (!isTransitioning) {
                String autoDropMessage = "Let me help you! Moving to the next step.";
                updateCaption(autoDropMessage);
                sayText(autoDropMessage);
                navigateToWhisk();
            }
        }, 2000);
    }

    private void navigateToWhisk() {
        if (isTransitioning) return;
        isTransitioning = true;

        Log.d("ActivityTransition", "Navigating to whisk1");
        Intent intent = new Intent(addeggs.this, whisk1.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
