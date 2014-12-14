package com.android.workshop.abevents;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BaseActivity extends ActionBarActivity {

    protected View ab_checkView;    // init view to display events

    protected ViewGroup ab_container;    // init container of ActionBar

    // init the states of events (default = false -> not visible)
    protected boolean  isDisplayed = false,
                       isFailed = false,
                       isChecking = false;

    // init elements of ab_checkView
    TextView checkText;
    ProgressBar checkProgress;
    ImageView checkCloseImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ab_checkView = getLayoutInflater().inflate(R.layout.ab_check, null);    // inflation of ab_checkView

        // create a separate thread to let the time to UI to be displayed
        ab_checkView.post(new Runnable() {
            @Override
            public void run() {
                ab_container = (ViewGroup) findViewById(R.id.action_bar_container);
            }
        });

        if(savedInstanceState != null) {    // check if orientation has changed

            if(savedInstanceState.getBoolean("display")) {    // check if the events are displayed
                isDisplayed = true;

                String mTextSavedMsg = savedInstanceState.getString("text");    // get the message displayed
                if(mTextSavedMsg.length() != 0) {

                    // create a separate thread to let the time to UI to be displayed
                    ab_checkView.post(new Runnable() {
                        @Override
                        public void run() {
                            ab_container.addView(ab_checkView);    // add ab_checkView to ActionBar container
                        }
                    });

                    // get the views of ab_checkView
                    checkText = (TextView) ab_checkView.findViewById(R.id.ab_text);
                    checkProgress = (ProgressBar) ab_checkView.findViewById(R.id.ab_progress);
                    checkCloseImg = (ImageView) ab_checkView.findViewById(R.id.ab_close);

                    checkText.setText(mTextSavedMsg);    // set the message saved before orientation

                    if(!savedInstanceState.getBoolean("progress")) {    // if the state is NOT "verification" (= in progress)
                        checkProgress.setVisibility(View.GONE);

                        if(savedInstanceState.getBoolean("fail")) {    // if the state is an error (=> red background)
                            isFailed = true;
                            ab_checkView.setBackgroundColor(getResources().getColor(R.color.red));
                            checkCloseImg.setVisibility(View.VISIBLE);
                        } else {
                            ab_checkView.setBackgroundColor(getResources().getColor(R.color.green));
                            checkCloseImg.setVisibility(View.GONE);
                        }

                        // close the ab_checkView with click event
                        ab_checkView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideMsg(ab_checkView);
                            }
                        });
                    } else {    // if the state IS "verification" (= in progress)
                        isChecking = true;
                    }
                }
            }
        }
    }

    // prepare and display the inflated view (ab_checkView)
    protected void prepareMsg() {
        if(!isDisplayed) {
            // set the states
            isDisplayed = true;
            isChecking = true;

            ab_container.addView(ab_checkView);    // add the inflated view

            // get the views of ab_checkView
            checkText = (TextView) ab_checkView.findViewById(R.id.ab_text);
            checkProgress = (ProgressBar) ab_checkView.findViewById(R.id.ab_progress);
            checkCloseImg = (ImageView) ab_checkView.findViewById(R.id.ab_close);

            ab_checkView.setVisibility(View.GONE);    // hide the inflated view

            showMsg(ab_container,ab_checkView);    // animate and show the inflated view
        }
    }

    // animate and show the events on ActionBar
    protected void showMsg(View v, final View v2){
        final int targetHeight = v.getMeasuredHeight();    // set the target height (actionbar's height)
        v2.getLayoutParams().height = 0;    // set the height to 0
        v2.setVisibility(View.VISIBLE);

        // set up an Animation
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v2.getLayoutParams().height = (int)(targetHeight * interpolatedTime);
                v2.requestLayout();
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(90);
        a.setFillAfter(true);

        v2.startAnimation(a);    // start the animation previously created
    }

    // animate and hide the events on ActionBar
    protected void hideMsg(final View v){
        final int initialHeight = v.getMeasuredHeight();    // get the inflated view height

        // set up an Animation
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(90);
        a.setFillAfter(true);

        // set up an Animation Listener
        a.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                // reinit the inflated view
                v.setBackgroundColor(getResources().getColor(R.color.yellow));
                checkText.setText(getResources().getText(R.string.check_hint));
                checkCloseImg.setVisibility(View.GONE);
                checkProgress.setVisibility(View.VISIBLE);

                ab_container.removeView(v);    // remove the inflated view

                // reinit the states
                isFailed = false;
                if(isChecking) isChecking = false;
                if(isDisplayed) isDisplayed = false;

            }
            @Override
            public void onAnimationRepeat(Animation arg0) { }
            @Override
            public void onAnimationStart(Animation arg0) { }
        });

        v.startAnimation(a);    // start the animation previously created
    }

    // display result message
    protected void resultMsg(int i, String result) {
        // set the states
        isChecking = false;
        isDisplayed = true;

        // if there is an error (i!=0)
        if(i != 0) {
            ab_checkView.setBackgroundColor(getResources().getColor(R.color.red));
            checkCloseImg.setVisibility(View.VISIBLE);
            isFailed = true;
        } else {
            ab_checkView.setBackgroundColor(getResources().getColor(R.color.green));
        }

        // disable the progress bar and set result message
        checkProgress.setVisibility(View.GONE);
        checkText.setText(result);

        // close the ab_checkView with click event
        ab_checkView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMsg(ab_checkView);
            }
        });
    }

    // save the different states when orientation changes
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        if(isDisplayed) {
            savedState.putString("text", checkText.getText().toString());
            savedState.putBoolean("display", isDisplayed);
            savedState.putBoolean("fail", isFailed);
            savedState.putBoolean("progress", isChecking);
        }
    }

}

