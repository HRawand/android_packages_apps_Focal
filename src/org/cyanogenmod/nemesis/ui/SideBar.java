package org.cyanogenmod.nemesis.ui;

import org.cyanogenmod.nemesis.CameraCapabilities;
import org.cyanogenmod.nemesis.R;
import org.cyanogenmod.nemesis.widgets.WidgetBase;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;

public class SideBar extends ScrollView {
    private final static String TAG = "SideBar";
    private final static int SLIDE_ANIMATION_DURATION_MS = 300;
    private CameraCapabilities mCapabilities;
    private ViewGroup mToggleContainer;

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SideBar(Context context) {
        super(context);
        initialize();
    }

    /**
     * Setup the bar
     */
    private void initialize() {
        this.setBackgroundColor(getResources().getColor(R.color.widget_background));
    }


    /**
     * Check the capabilities of the device, and populate the sidebar
     * with the toggle buttons.
     */
    public void checkCapabilities(Camera.Parameters param, ViewGroup widgetsContainer) {
        mToggleContainer = (ViewGroup) this.getChildAt(0);

        if (mCapabilities != null) {
            mToggleContainer.removeAllViews();
            mCapabilities = null;
        }

        mCapabilities = new CameraCapabilities(this.getContext());
        mCapabilities.populateSidebar(param, mToggleContainer, widgetsContainer);
    }

    /**
     * Toggles the visibility of the widget options (container)
     * @param widget The widget to toggle
     * @param isFling If it should be toggled with the fling-up animation
     */
    public void toggleWidgetVisibility(WidgetBase widget, boolean isFling) {
        if (widget.isOpen()) {
            Log.e(TAG, "Closing widget");
            widget.close();
        } else {
            Log.e(TAG, "Opening widget");
            widget.open();
        }
    }

    /**
     * Notify the bar of the rotation
     * @param target Target orientation
     */
    public void notifyOrientationChanged(float target) {
        mToggleContainer = (ViewGroup) this.getChildAt(0);
        int buttonsCount = mToggleContainer.getChildCount();

        for (int i = 0; i < buttonsCount; i++) {
            View child = mToggleContainer.getChildAt(i);

            child.animate().rotation(target)
            .setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    /**
     * Slides the sidebar off the provided distance, and clamp it at either
     * sides of the screen (out/in)
     * @param distance The distance to translate the bar
     */
    public void slide(float distance) {
        float finalY = this.getTranslationY() + distance;

        if (finalY > this.getHeight())
            finalY = this.getHeight();
        else if (finalY < 0)
            finalY = 0;

        this.setTranslationY(finalY);
    }

    /**
     * Clamp the sliding position of the bar. Basically, if the
     * bar is half-visible, it will animate it in its visible position,
     * and vice-versa.
     */
    public void clampSliding() {
        if (this.getTranslationY() > this.getHeight()/2)
            slideClose();
        else
            slideOpen();
    }

    /**
     * Smoothly close the sidebar
     */
    public void slideClose() {
        this.animate().translationY(this.getHeight())
        .setDuration(SLIDE_ANIMATION_DURATION_MS).start();
    }

    /**
     * Smoothly open the sidebar
     */
    public void slideOpen() {
        this.animate().translationY(0)
        .setDuration(SLIDE_ANIMATION_DURATION_MS).start();;
    }

}
