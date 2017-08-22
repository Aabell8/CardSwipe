package com.austinabell8.cardswipe.Utility;

import android.view.View;

/**
 * Created by aabell on 8/22/2017.
 */
public interface SwipeCallback {
    void cardSwipedLeft(View card);
    void cardSwipedRight(View card);
    void cardSwipedTop(View card);
    void cardSwipedBottom(View card);
    void cardOffScreen(View card);
    void cardActionDown();
    void cardActionUp();

    /**
     * Check whether we can start dragging current view.
     * @return true if we can start dragging view, false otherwise
     */
    boolean isDragEnabled();
}
