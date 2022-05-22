package com.example.arproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;

public class Frag_first extends Fragment {
    private  String title;
    private int page;

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
        EditText tvLabel = (EditText) view.findViewById(R.id.editTxt_01);
        tvLabel.setText("Pocket SolAR");
        tvLabel.setTextSize(Dimension.SP, 100);
        return view;
    }
}
