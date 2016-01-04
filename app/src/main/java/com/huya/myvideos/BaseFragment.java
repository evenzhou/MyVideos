package com.huya.myvideos;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by even on 2016-01-04.
 */
public abstract class BaseFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null);
        TextView textView = (TextView) view.findViewById(R.id.container);
        textView.setText(initContent());
        return view;
    }

    public abstract String initContent();
}
