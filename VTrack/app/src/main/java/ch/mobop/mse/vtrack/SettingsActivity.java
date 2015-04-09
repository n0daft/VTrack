package ch.mobop.mse.vtrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;

/**
 * Provides methods for the application settings functionality.
 * Created by n0daft on 28.03.2015.
 */
public class SettingsActivity extends FragmentActivity {

    private final Handler handler = new Handler();
    private SharedPreferences mSharedpreferences;
    private Editor mEditor;
    private TextView mLblValidityThreshold;

    private int mThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mLblValidityThreshold = (TextView) findViewById(R.id.lblValidityThreshold);

        mSharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = mSharedpreferences.edit();
        mThreshold = mSharedpreferences.getInt(Constants.VALIDITY_THRESHOLD, Config.defaultValidityThreshold);
        updateValidityThresholdLabel();
        ColorDrawable color = new ColorDrawable(mSharedpreferences.getInt(Constants.actionBarColor,Config.defaultActionBarColor.getColor()));
        getActionBar().setBackgroundDrawable(color);

    }


    public void onColorClicked(View v) {
        int color = Color.parseColor(v.getTag().toString());
        ColorDrawable oldColor = new ColorDrawable(mSharedpreferences.getInt(Constants.actionBarColor,Config.defaultActionBarColor.getColor()));
        ColorDrawable newColor = new ColorDrawable(color);

        mEditor.putInt(Constants.actionBarColor, color);
        mEditor.commit();

        changeColor(oldColor, newColor);
    }

    public void handlePreviousNumber(View v){
        if(mThreshold - 1 >= 1){
            mThreshold--;
            updateValidityThresholdLabel();
        }
    }

    public void handleNextNumber(View v){
        mThreshold++;
        updateValidityThresholdLabel();
    }

    private void updateValidityThresholdLabel(){
        mLblValidityThreshold.setText("" + mThreshold);

        mEditor.putInt(Constants.VALIDITY_THRESHOLD, mThreshold);
        mEditor.commit();

        Config.currentValidityThreshold = mThreshold;
    }


    private void changeColor(ColorDrawable oldColor, ColorDrawable newColor){
        TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldColor, newColor });

        // workaround for broken ActionBarContainer drawable handling on
        // pre-API 17 builds
        // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            td.setCallback(drawableCallback);
        } else {
            getActionBar().setBackgroundDrawable(td);
        }

        td.startTransition(200);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };



}
