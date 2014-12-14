package com.android.workshop.abevents;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends BaseActivity implements FragmentForm.FragCallbacks {

    // init fragment and its tag
    FragmentForm mFragment;
    final static String FRAGMENT_FROM = "F_FORM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);    // set the layout

        // get the fragment with its tag
        mFragment = (FragmentForm)
                getSupportFragmentManager().findFragmentByTag(FRAGMENT_FROM);

        if(savedInstanceState == null || mFragment == null) {    // check if orientation has changed

            mFragment = new FragmentForm();    // init the fragment for the first time

            // add it to the FrameLayout
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_main, mFragment, FRAGMENT_FROM)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
            // get the action 'done'
            case R.id.action_done:
                mFragment.prepareAction();    // call prepareAction method
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*** AsyncTask callbacks ***/
    @Override
    public void onPreExecute() {
        prepareMsg();    // call prepareMsg method
    }

    public void onCancelled() { }

    @Override
    public void onPostExecute(String s) {
        String message = null;
        int error = 0;

        // retrieve state's result
         if (s.equals("1")) {
            error = 1;
            message = "Your name is empty.";
         } else if (s.equals("2")) {
            error = 2;
            message = "This name is already used.";
         } else if (s.equals("3")) {
            error = 3;
            message = "An error occurred.";
        } else {
            message = "Hi "+ s +", your name is great!";
        }

        resultMsg(error, message);    // send the result
    }

}