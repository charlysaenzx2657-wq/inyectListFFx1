package com.sensisgoodff.sensor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.MotionEvent;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.VelocityTracker;

public class TouchAccessibilityService extends AccessibilityService {

    private static final float TOUCH_FAST = 3000f;
    private static final float TOUCH_MID  = 1000f;

    // Reset a LENTO si no hay movimiento en 300ms
    private static final long RESET_DELAY = 300;

    private VelocityTracker velocityTracker;
    private Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable resetLevel = () -> {
        SensorService.touchLevel = 0;
    };

    @Override
    protected boolean onGesture(int gestureId) {
        return false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();

        if (type == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            float scrollX = Math.abs(event.getScrollX());
            float scrollY = Math.abs(event.getScrollY());
            float speed = (scrollX + scrollY) * 60f; // aproximar px/s

            int level;
            if (speed >= TOUCH_FAST) level = 2;
            else if (speed >= TOUCH_MID) level = 1;
            else level = 0;

            SensorService.touchLevel = level;

            // Reset después de 300ms sin scroll
            handler.removeCallbacks(resetLevel);
            handler.postDelayed(resetLevel, RESET_DELAY);
        }

        if (type == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START) {
            SensorService.touchLevel = 0;
        }

        if (type == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
            handler.postDelayed(resetLevel, RESET_DELAY);
        }
    }

    @Override
    public void onInterrupt() {}
}
