package com.yobi.recipe;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yobi.R;
import com.yobi.data.APIRecipe;
import com.yobi.data.APIRecipe.Manual;

import java.util.List;

public class Fragment_recipe_detail_order extends Fragment {

    // 컴포넌트
    ImageView imageView, image_ingredient, imageMic;
    TextView step, description, description_sub, description_ingredient, mic01, mic02;

    // 전달받을 데이터
    private int orderNum;
    private APIRecipe apiRecipe;

    public Fragment_recipe_detail_order() {
        // Required empty public constructor
    }

    public static Fragment_recipe_detail_order newInstance(int orderNum, APIRecipe apiRecipe) {
        Fragment_recipe_detail_order fragment = new Fragment_recipe_detail_order();
        Bundle args = new Bundle();
        args.putInt("orderNum", orderNum);
        args.putSerializable("apiRecipe", apiRecipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderNum = getArguments().getInt("orderNum");
            apiRecipe = (APIRecipe) getArguments().getSerializable("apiRecipe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_detail_order, container, false);

        // 초기화
        imageView = v.findViewById(R.id.imageView_recipe_detail_order);
        step = v.findViewById(R.id.textView_recipe_detail_order_step);
        description = v.findViewById(R.id.textView_recipe_detail_order_description_01);

        // 전달된 데이터 가져오기
        if (getArguments() != null) {
            String mainDescription = getArguments().getString("mainDescription");
            String imgURL = getArguments().getString("imgURL");

            // Description 설정
            description.setText(mainDescription);

            // 이미지 설정 (Glide 사용)
            if (imgURL != null && !imgURL.isEmpty()) {
                Glide.with(getContext())
                        .load(imgURL)
                        .into(imageView);
            }
        }

        return v;
    }

}
