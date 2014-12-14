package com.android.workshop.abevents;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class FragmentForm extends Fragment {

    private FragCallbacks callbacks;    // init callbacks

    private EditText name;    // init EditText

    // create the fragment's interface
    public static interface FragCallbacks {
        void onPreExecute();
        void onCancelled();
        void onPostExecute(String result);
    }

    // attach the interface to parent activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (FragCallbacks) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // call setRetainInstance method to retain
        // fragment's state across the Activity re-creation
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_form, container, false);

        name = (EditText) v.findViewById(R.id.name);    // get the EditText

        return v;
    }

    // call the AsyncTask method
    public void prepareAction() {
        new checkText().execute(name.getText().toString());
    }

    // create AsyncTask method
    class checkText extends AsyncTask<String,Void,String> {

        // attach onPreExecute to parent activity
        @Override
        protected void onPreExecute() {
            if (callbacks != null) {
                callbacks.onPreExecute();
            }
        }

        // do some actions in background
        @Override
        protected String doInBackground(String... params) {
            // sleep during 5 sec
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // check the input text and return the result
            if(params[0].trim().length() == 0) {
                return "1";
            } else if(params[0].equals("used")) {
                return "2";
            } else if(params[0].equals("error")) {
                return "3";
            } else {
                return params[0];
            }
        }

        // attach onCancelled to parent activity
        @Override
        protected void onCancelled() {
            if (callbacks != null) {
                callbacks.onCancelled();
            }
        }

        // attach onPostExecute to parent activity and send result
        @Override
        protected void onPostExecute(String result) {
            if (callbacks != null) {
                callbacks.onPostExecute(result);
            }
        }
    }

    // detach the interface
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
