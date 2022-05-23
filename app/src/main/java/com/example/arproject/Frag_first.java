package com.example.arproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;

public class Frag_first extends Fragment {

    public static Frag_first newInstance(int page, String title) {
        Frag_first fragment = new Frag_first();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_01, container, false);
        TextView tvLabel_01= (TextView) view.findViewById(R.id.editTxt_01);
        TextView tvLabel_02 = (TextView) view.findViewById(R.id.editTxt_02);

        tvLabel_01.setText("Pocket SolAR");
        tvLabel_02.setText("옆으로 넘겨주세요 >>> ");

        tvLabel_01.setAutoSizeTextTypeUniformWithConfiguration(60, 240, 4, TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        tvLabel_02.setAutoSizeTextTypeUniformWithConfiguration(20, 50, 1, TextView.AUTO_SIZE_TEXT_TYPE_NONE);

        return view;
    }
}
