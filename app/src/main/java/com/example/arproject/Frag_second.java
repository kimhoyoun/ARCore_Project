package com.example.arproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;

public class Frag_second extends Fragment {

    public static Frag_second newInstance(int page, String title) {
        Frag_second fragment = new Frag_second();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_02, container, false);
        TextView manual_title = (TextView) view.findViewById(R.id.manual_title);
        TextView manual_01 = (TextView) view.findViewById(R.id.manual_01);
        TextView manual_02 = (TextView) view.findViewById(R.id.manual_02);
        TextView manual_03= (TextView) view.findViewById(R.id.manual_03);
        TextView manual_04 = (TextView) view.findViewById(R.id.manual_04);
//        TextView manual_05 = (TextView) view.findViewById(R.id.manual_05);

        manual_title.setText("[ Pocket SolAR 가이드 ]\n" );

        manual_01.setText("1. 시작하기를 누르고 이미지를 인식하세요.\n\n");
        manual_02.setText("2. 오른쪽 아래에 피카츄가 여러분을 도와줍니다!\n\n");
        manual_03.setText("3. 행성들이 자리를 잡은 후 두번 터치하면 행성 정보를 확인할 수 있어요.\n\n");
        manual_04.setText("4. 두개의 행성을 따로 길게 터치하면 거리를 확인할 수 있어요.\n\n");

        manual_title.setAutoSizeTextTypeUniformWithConfiguration(50, 200, 4, TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        manual_01.setAutoSizeTextTypeUniformWithConfiguration(30, 120, 2, TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        manual_02.setAutoSizeTextTypeUniformWithConfiguration(30, 120, 2, TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        manual_03.setAutoSizeTextTypeUniformWithConfiguration(30, 120, 2, TextView.AUTO_SIZE_TEXT_TYPE_NONE);
        manual_04.setAutoSizeTextTypeUniformWithConfiguration(30, 120, 2, TextView.AUTO_SIZE_TEXT_TYPE_NONE);


        return view;
    }
}
