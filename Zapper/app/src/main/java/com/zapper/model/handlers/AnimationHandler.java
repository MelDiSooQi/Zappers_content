package com.zapper.model.handlers;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Observable;

/**
 * Created by MelDiSooQi on 5/17/2016.
 */
public class AnimationHandler extends Observable {

    public static void layerAnimation(final Activity activity, final View view, final int animation)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                view.startAnimation(AnimationUtils.loadAnimation(activity, animation));
            }
        });
    }

    public void layerAnimationWithListener(final Activity activity, final View view, final int animation)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                Animation openAnimation = AnimationUtils.loadAnimation(
                        activity, animation);

                openAnimation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //setChanged();
                        //notifyObservers("onAnimationStart");
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //setChanged();
                        //notifyObservers("onAnimationRepeat");
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setChanged();
                        notifyObservers("onAnimationEnd");
                    }
                });
                view.startAnimation(openAnimation);
            }
        });
    }

    public static void pageAnimation(final Activity activity, final int enterAnimation, final int exitAnimation)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                activity.overridePendingTransition(enterAnimation, exitAnimation);
            }
        });
    }
}
