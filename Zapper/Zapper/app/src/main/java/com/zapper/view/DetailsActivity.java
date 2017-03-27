package com.zapper.view;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.zapper.R;
import com.zapper.model.handlers.Connectivity;

/**
 * Created by MelDiSooQi on 3/26/2017.
 */

// This is a secondary activity, to show what the user has selected when the
// screen is not large enough to show it all in one activity.

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Connectivity.isConnect(this))//IF not Connected
        {
            openDialog();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.

            // create fragment
            HomeActivity.DetailsFragment details = new HomeActivity.DetailsFragment();

            // get and set the position input by user (i.e., "coontactID")
            // which is the construction arguments for this fragment
            details.setArguments(getIntent().getExtras());

            //
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, details).commit();
        }
    }

    private void openDialog() {
        // Create Internet Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_internet_connection))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    finish();
                    }
                });
        final AlertDialog alert = builder.create();
        //2. now setup to change color of the button
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.parseColor("#2a64af"));
            }
        });

        alert.show();
    }
}