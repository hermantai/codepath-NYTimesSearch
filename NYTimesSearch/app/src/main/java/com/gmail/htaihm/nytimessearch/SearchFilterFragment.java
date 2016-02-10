package com.gmail.htaihm.nytimessearch;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import butterknife.Bind;

public class SearchFilterFragment extends DialogFragment {
    @Bind(R.id.btnDatePicker) Button mBtnDatePicker;

    public static SearchFilterFragment newInstance() {
        SearchFilterFragment frag = new SearchFilterFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_search_filter, container);
    }

    @Override
    public void onResume() {
        // Assign window properties to fill the parent
        getDialog().getWindow().setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        super.onResume();
    }
}
