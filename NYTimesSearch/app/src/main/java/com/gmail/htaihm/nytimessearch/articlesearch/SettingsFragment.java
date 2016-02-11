package com.gmail.htaihm.nytimessearch.articlesearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.gmail.htaihm.nytimessearch.R;
import com.gmail.htaihm.nytimessearch.repo.QueryPreferences;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsFragment extends DialogFragment {
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
    private static final int REQUEST_DATE = 0;

    @Bind(R.id.btnDatePicker) Button mBtnDatePicker;
    @Bind(R.id.sSortOrder) Spinner mSSortOrder;
    @Bind(R.id.cbArts) CheckBox mCbArts;
    @Bind(R.id.cbFashionAndStyle) CheckBox mCbFashionAndStyle;
    @Bind(R.id.cbSports) CheckBox mCbSports;
    @Bind(R.id.btnSave) Button mBtnSave;

    private Date mBeginDate;

    public static SettingsFragment newInstance() {
        SettingsFragment frag = new SettingsFragment();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity();

        long beginDateInMillis = QueryPreferences.getFilterBeginDate(context);
        if (beginDateInMillis != 0) {
            mBeginDate = new Date(beginDateInMillis);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View v = inflater.inflate(R.layout.fragment_settings, container);
        ButterKnife.bind(this, v);

        final Date defaultDate = mBeginDate == null ? new Date() : mBeginDate;

        mBtnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment frag = DatePickerFragment.newInstance(defaultDate);
                frag.setTargetFragment(SettingsFragment.this, REQUEST_DATE);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                frag.show(fm, "DatePicker");
            }
        });
        if (mBeginDate != null) {
            mBtnDatePicker.setText(dateFormatter.format(mBeginDate));
        }

        ArrayAdapter<CharSequence> sortOrderAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.article_search_filter_sort_order_array,
                android.R.layout.simple_spinner_dropdown_item);
        mSSortOrder.setAdapter(sortOrderAdapter);

        int selectedSortOrder = 0;
        String storedSortOrder = QueryPreferences.getFilterSortOrder(getActivity());
        if (!TextUtils.isEmpty(storedSortOrder)) {
            selectedSortOrder = Arrays.asList(
                    getResources().getStringArray(
                            R.array.article_search_filter_sort_order_array))
                    .indexOf(storedSortOrder);
            if (selectedSortOrder == -1) {
                selectedSortOrder = 0;
            }
        }
        mSSortOrder.setSelection(selectedSortOrder);

        final Context context = getActivity();

        mCbArts.setChecked(QueryPreferences.isFilterNewsDeskValueArts(context));
        mCbFashionAndStyle.setChecked(QueryPreferences.isFilterNewsDeskValueArts(context));
        mCbSports.setChecked(QueryPreferences.isFilterNewsDeskValueSports(context));

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mBeingDate is set whenever a Date is picked, so we don't have to pull from the
                // button to get the value.
                if (mBeginDate != null) {
                    QueryPreferences.setFilterBeginDate(context, mBeginDate.getTime());
                }

                QueryPreferences.setFilterSortOrder(
                        context, (String) mSSortOrder.getSelectedItem());
                QueryPreferences.setIsFilterNewsDeskValueArts(context, mCbArts.isChecked());
                QueryPreferences.setIsFilterNewsDeskValueFashionAndStyle(
                        context, mCbFashionAndStyle.isChecked());
                QueryPreferences.setIsFilterNewsDeskValueSports(context, mCbSports.isChecked());
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        // Assign window properties to fill the parent
        getDialog().getWindow().setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DATE) {
            Date pickedDate = DatePickerFragment.getPickedDate(data);

            if (pickedDate != null) {
                mBeginDate = pickedDate;
                mBtnDatePicker.setText(dateFormatter.format(mBeginDate));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
