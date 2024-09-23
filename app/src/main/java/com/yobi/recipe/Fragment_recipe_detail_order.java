package com.yobi.recipe;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.yobi.R;
import com.yobi.data.APIRecipe;

public class Fragment_recipe_detail_order extends Fragment {

    // 컴포넌트
    ImageView imageView;
    TextView step, description;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 레이아웃 설정 및 뷰 초기화
        View v = inflater.inflate(R.layout.fragment_recipe_detail_order, container, false);

        // 초기화
        imageView = v.findViewById(R.id.imageView_recipe_detail_order);
        step = v.findViewById(R.id.textView_recipe_detail_order_step);
        description = v.findViewById(R.id.textView_recipe_detail_order_description_01);

        // 전달된 데이터 바인딩
        if (getArguments() != null) {
            Log.d("FragmentRecipeDetail", "Arguments received.");

            orderNum = getArguments().getInt("orderNum", 1); // 기본값 1로 설정
            apiRecipe = (APIRecipe) getArguments().getSerializable("apiRecipe");

            if (apiRecipe != null) {
                Log.d("FragmentRecipeDetail", "APIRecipe received: " + apiRecipe.toString());
                APIRecipe.Manual manual = apiRecipe.getManuals().get(orderNum - 1);  // 매뉴얼에서 데이터 가져오기

                // Step과 Description 설정
                step.setText("Step " + orderNum);
                description.setText(manual.getDescription());

                // 이미지 설정 (Glide 사용)
                String imgURL = manual.getImage();
                if (imgURL != null && !imgURL.isEmpty()) {
                    Glide.with(getContext())
                            .load(imgURL)
                            .into(imageView);
                }
            } else {
                Log.e("FragmentRecipeDetail", "APIRecipe is null");
            }
        } else {
            Log.e("FragmentRecipeDetail", "Arguments are null");
        }

        return v;
    }
}
