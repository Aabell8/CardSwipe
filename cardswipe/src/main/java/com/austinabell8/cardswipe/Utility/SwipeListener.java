package com.austinabell8.cardswipe.Utility;

import android.animation.Animator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;


import com.austinabell8.cardswipe.SwipeDeck;

import static java.lang.Math.abs;

/**
 * Created by aabell on 8/22/2017.
 */

public class SwipeListener implements View.OnTouchListener {

    private float ROTATION_DEGREES = 15f;
    float OPACITY_END = 0.33f;
    private int initialX;
    private int initialY;

    private long startTime;
    private int mActivePointerId;
    private float initialXPress;
    private float initialYPress;
    private ViewGroup parent;
    private int rotationFlip;

    private View card;
    SwipeCallback callback;
    private boolean deactivated;
    private View rightView;
    private View leftView;

    public SwipeListener(View card, final SwipeCallback callback, int initialX, int initialY, float rotation,
                         float opacityEnd, SwipeDeck parent) {
        this.card = card;
        this.initialX = initialX;
        this.initialY = initialY;
        this.callback = callback;
        this.parent = parent;
        this.ROTATION_DEGREES = rotation;
        this.OPACITY_END = opacityEnd;
    }

    private boolean click = true;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (deactivated) {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                click = true;
                startTime = System.currentTimeMillis();
                //gesture has begun
                float x;
                float y;
                //cancel any current animations
                v.clearAnimation();

                mActivePointerId = event.getPointerId(0);

                x = event.getX();
                y = event.getY();

                if (event.findPointerIndex(mActivePointerId) == 0) {
                    callback.cardActionDown();
                }

                //TODO: should be middle with 2
                float middleY = (card.getY() + (card.getHeight() / 2)) + parent.getY() - 100;

                if (y > middleY){
                    rotationFlip = -1;
                }
                else{
                    rotationFlip = 1;
                }

                initialXPress = x;
                initialYPress = y;
                break;

            case MotionEvent.ACTION_MOVE:
                //gesture is in progress

                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                //Log.i("pointer index: " , Integer.toString(pointerIndex));
                if (pointerIndex < 0 || pointerIndex > 0) {
                    break;
                }

                final float xMove = event.getX(pointerIndex);
                final float yMove = event.getY(pointerIndex);

                //calculate distance moved
                final float dx = xMove - initialXPress;
                final float dy = yMove - initialYPress;

                //in this circumstance consider the motion a click
                if (abs(dx + dy) > 5) {
                    click = false;
                }

                // Check whether we are allowed to drag this card
                // We don't want to do this at the start of the branch, as we need to check whether we exceeded
                // moving threshold first
                if (!callback.isDragEnabled()) {
                    return false;
                }

//                Log.d("X:", "" + v.getX());

                //calc rotation here
                float posX = card.getX() + dx;
                float posY = card.getY() + dy;

                card.setX(posX);
                card.setY(posY);
                animateUnderCards(posX, card.getWidth());

                //card.setRotation
                float distobjectX = posX - initialX;
                float rotation = ROTATION_DEGREES * 2.f * distobjectX / parent.getWidth() * rotationFlip;
                card.setRotation(rotation);

                if (rightView != null && leftView != null) {
                    //set alpha of left and right image
                    float alpha = (((posX - parent.getPaddingLeft()) / (parent.getWidth() * OPACITY_END)));
                    //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                    //Log.i("alpha: ", Float.toString(alpha));
                    rightView.setAlpha(alpha);
                    leftView.setAlpha(-alpha);
                }

                break;

            case MotionEvent.ACTION_UP:
                //gesture has finished
                //check to see if card has moved beyond the left or right bounds or reset
                //card position
                checkCardForEvent(event);

                if (event.findPointerIndex(mActivePointerId) == 0) {
                    callback.cardActionUp();
                }
                //check if this is a click event and then perform a click
                //this is a workaround, android doesn't play well with multiple listeners

                if (click) {
                    v.performClick();
                }
                //if(click) return false;

                break;

            default:
                return false;
        }
        return true;
    }

    public void checkCardForEvent(MotionEvent event) {
        long totalTime = System.currentTimeMillis() - startTime;
        float x = card.getX() - initialX;
        float y = card.getY() - initialY;
        double distance = Math.sqrt(x*x + y*y);
        double speed = distance / totalTime;
//        Log.e("TEST", "Speed:"+speed);


        if (abs(x) > abs(y)){


            if (x<0 && (speed > 1.3 || x > (parent.getWidth() / 4.f))) {

                animateOffScreenLeft(SwipeDeck.ANIMATION_DURATION, card.getY(), rotationFlip)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen(card);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                Log.d("SwipeListener", "Animation Cancelled");
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                callback.cardSwipedLeft(card);
                this.deactivated = true;
            } else if ((speed > 1.3 || x > (parent.getWidth() / 4.f))) {
                animateOffScreenRight(SwipeDeck.ANIMATION_DURATION, card.getY(), rotationFlip)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen(card);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                callback.cardSwipedRight(card);
                this.deactivated = true;
            } else {
                resetCardPosition();
            }
        }
        else{
            if (y<0 && (speed > 1.3 || y > (parent.getHeight() / 4.f))) {
                animateOffScreenTop(SwipeDeck.ANIMATION_DURATION)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen(card);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                Log.d("SwipeListener", "Animation Cancelled");
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                callback.cardSwipedTop(card);
                this.deactivated = true;
            } else if ((speed > 1.3 || y > (parent.getHeight() / 4.f))) {
                animateOffScreenBottom(SwipeDeck.ANIMATION_DURATION)
                        .setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                callback.cardOffScreen(card);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                callback.cardSwipedBottom(card);
                this.deactivated = true;
            } else {
                resetCardPosition();
            }
        }

    }

    private boolean cardBeyondLeftBorder() {
        //check if cards middle is beyond the left quarter of the screen
        return (card.getX() + (card.getWidth() / 2) < (parent.getWidth() / 4.f));
    }

    private boolean cardBeyondRightBorder() {
        //check if card middle is beyond the right quarter of the screen
        return (card.getX() + (card.getWidth() / 2) > ((parent.getWidth() / 4.f) * 3));
    }

    private boolean cardBeyondTopBorder() {
        //check if card middle is beyond the right quarter of the screen
        return (card.getY() + (card.getHeight() / 2) < ((parent.getHeight() / 4.f)));
    }

    private boolean cardBeyondBottomBorder() {
        //check if card middle is beyond the right quarter of the screen
        return (card.getY() + (card.getHeight() / 2) > ((parent.getHeight() / 4.f) * 2));
    }

    private ViewPropertyAnimator resetCardPosition() {
        if (rightView != null) {
            rightView.setAlpha(0);
        }
        if (leftView != null) {
            leftView.setAlpha(0);
        }

        //todo: figure out why i have to set translationX to 0
        return card.animate()
                .setDuration(SwipeDeck.ANIMATION_DURATION)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .x(initialX)
                .y(initialY)
                .rotation(0)
                .translationX(0);
    }

    private ViewPropertyAnimator animateOffScreenLeft(int duration, float y, int rotation) {
        return card.animate()
                .setDuration(duration)
                .x(-(parent.getWidth()))
                .y(y)
                .rotation(rotationFlip*-30);
    }

    private ViewPropertyAnimator animateOffScreenRight(int duration, float y, int rotation) {
        return card.animate()
                .setDuration(duration)
                .x(parent.getWidth() * 2)
                .y(y)
                .rotation(rotationFlip*30);
    }

    private ViewPropertyAnimator animateOffScreenTop(int duration) {
        return card.animate()
                .setDuration(duration)
                .x(0)
                .y(-(parent.getHeight()))
                .rotation(0);
    }

    private ViewPropertyAnimator animateOffScreenBottom(int duration) {
        return card.animate()
                .setDuration(duration)
                .x(0)
                .y(parent.getHeight()*2)
                .rotation(0);
    }

    public void swipeCardLeft(int duration) {
        animateOffScreenLeft(duration, initialY, rotationFlip);
    }

    public void swipeCardTop(int duration){
        animateOffScreenTop(duration);
    }

    public void swipeCardRight(int duration) {
        animateOffScreenRight(duration, initialY, rotationFlip);
    }

    public void swipeCardBottom(int duration){
        animateOffScreenBottom(duration);
    }

    public void setRightView(View image) {
        this.rightView = image;
    }

    public void setLeftView(View image) {
        this.leftView = image;
    }

    //animate under cards by 0 - 100% of card spacing
    private void animateUnderCards(float xVal, int cardWidth) {
        // adjust xVal to middle of card instead of left
        //parent width 1080
        float xValMid = xVal + (cardWidth / 2);
    }
}
