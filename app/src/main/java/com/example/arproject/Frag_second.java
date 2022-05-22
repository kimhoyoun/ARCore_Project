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
    private  String title;
    private int page;

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
        TextView tvLabel_01 = (TextView) view.findViewById(R.id.manual);
        TextView tvLabel_02 = (TextView) view.findViewById(R.id.editTxt_02);

        tvLabel_01.setText("[ Pocket SolAR 가이드 ]\n" );
        tvLabel_01.setTextSize(Dimension.SP, 40);

        tvLabel_02.setText("1. 시작하기 버튼을 누르고 이미지를 인식하세요.\n\n" +
                "2. 오른쪽 하단에 길잡이(가명)가 여러분을 도와줍니다!\n\n" +
                "3. 행성들이 생성된 후 터치를 해보세요!\n\n" +
                "4. 더블터치로 행성 정보를 확인할 수 있어요.\n\n" +
                "5. 두개의 행성을 각각 길게 터치하여 거리를 확인할 수 있어요.\n\n");
        tvLabel_02.setTextSize(Dimension.SP, 25);
        return view;
    }
}
