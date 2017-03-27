package com.zapper.view.activates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.zapper.R;
import com.zapper.model.handlers.AnimationHandler;
import com.zapper.model.handlers.StatusBarHandler;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by MelDiSooQi on 3/24/2017.
 */

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarHandler.transparantStatusBar(this);
        setContentView(R.layout.splash);

        ImageView logo = (ImageView) findViewById(R.id.logo);

        AnimationHandler animationHandler = new AnimationHandler();
        animationHandler.layerAnimationWithListener(this, logo, R.anim.fade_in);
        animationHandler.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                openNewPage();
            }
        });

    }

    private void openNewPage()
    {
            Intent i = new Intent(this, FragmentLayout.class);
            startActivity(i);
            AnimationHandler.pageAnimation(this, R.anim.fade_in, R.anim.fade_out);

            finish();
    }
}
