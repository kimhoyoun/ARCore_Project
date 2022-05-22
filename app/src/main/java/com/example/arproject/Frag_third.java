package com.example.arproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Frag_third extends Fragment {
    private  String title;
    private int page;

    public static Frag_third newInstance(int page, String title) {
        Frag_third fragment = new Frag_third();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_03, container, false);

        TextView info_member_01 = (TextView) view.findViewById(R.id.info_member_01);
        info_member_01.setText("이름 : 전경진\n"+ "담당 : -----");

        TextView info_member_02 = (TextView) view.findViewById(R.id.info_member_02);
        info_member_02.setText("이름 : 김민수\n"+ "담당 : -----");

        TextView info_member_03 = (TextView) view.findViewById(R.id.info_member_03);
        info_member_03.setText("이름 : 김진솔\n"+ "담당 : -----");

        TextView info_member_04 = (TextView) view.findViewById(R.id.info_member_04);
        info_member_04.setText("이름 : 김호윤\n"+ "담당 : -----");
        return view;
    }
}
