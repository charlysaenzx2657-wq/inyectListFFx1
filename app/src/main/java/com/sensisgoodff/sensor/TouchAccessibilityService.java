package com.sensisgoodff.sensor;

import android.accessibilityservice.AccessibilityService;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;

public class TouchAccessibilityService extends AccessibilityService {

    private VelocityTracker velocityTracker;

    // Umbrales touch (px/segundo) — calibrado para panel del Honor X5C Plus
    private static final float TOUCH_FAST = 2500f;
    private static final float TOUCH_MID  = 800f;

    @Override
    public boolean onKeyEvent(KeyEvent event) { return false; }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();

        if (type == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            // Detectar velocidad de scroll
            float scrollX = event.getScrollX();
            float scrollY = event.getScrollY();
            float speed = Math.abs(scrollX) + Math.abs(scrollY);

            int level;
            if (speed > 50) level = 2;
            else if (speed > 15) level = 1;
            else level = 0;

            notifySensorService(level);
        }
    }

    private void notifySensorService(int level) {
        // Comunicar nivel al SensorService vía variable estática
        SensorService.touchLevel = level;
    }

    @Override
    public void onInterrupt() {}
}
