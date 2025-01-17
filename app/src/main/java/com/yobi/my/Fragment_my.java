package com.yobi.my;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yobi.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_my#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_my extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // Fragments
    Fragment_my_boards fragment_my_boards;

    // 컴포넌트
    LinearLayout myBoards;

    // 데이터 or Manager
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public Fragment_my() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_my.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_my newInstance(String param1, String param2) {
        Fragment_my fragment = new Fragment_my();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my, container, false);

        // 초기화
        myBoards = v.findViewById(R.id.linearLayout_my_board);

        fragment_my_boards = new Fragment_my_boards();

        // 이벤트
        myBoards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrag(0);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    private void setFrag(int n) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n) {
            case 0:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_my_boards).commit();
                break;
        }
    }
}