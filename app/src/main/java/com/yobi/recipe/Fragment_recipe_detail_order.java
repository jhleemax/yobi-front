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
import com.yobi.data.RecipeOrderDetail;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_recipe_detail_order#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_recipe_detail_order extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Required Info
    private APIRecipe apiRecipe;
    private int orderNum;

    // 컴포넌트
    ImageView imageView, image_ingredient, imageMic;
    TextView step, description, description_sub, description_ingredient, mic01, mic02;

    public Fragment_recipe_detail_order() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_recipe_detail_order.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_recipe_detail_order newInstance(String param1, String param2) {
        Fragment_recipe_detail_order fragment = new Fragment_recipe_detail_order();
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

            apiRecipe = (APIRecipe) getArguments().getSerializable("dataSet");
            orderNum = getArguments().getInt("orderNum");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_detail_order, container, false);

        // 초기화
        imageView = (ImageView) v.findViewById(R.id.imageView_recipe_detail_order);
        step = (TextView) v.findViewById(R.id.textView_recipe_detail_order_step);
        description = (TextView) v.findViewById(R.id.textView_recipe_detail_order_description_01);
        description_sub = (TextView) v.findViewById(R.id.textView_recipe_detail_order_description_02);
        image_ingredient = (ImageView) v.findViewById(R.id.imageView_recipe_detail_order_description);
        description_ingredient = (TextView) v.findViewById(R.id.textView_recipe_detail_order_description_03);
        imageMic = (ImageView) v.findViewById(R.id.imageView_recipe_detail_order_mic);
        mic01 = (TextView) v.findViewById(R.id.textView_recipe_detail_order_mic_01);
        mic02 = (TextView) v.findViewById(R.id.textView_recipe_detail_order_mic_02);

        // 비가시 처리
        description_sub.setVisibility(View.GONE);
        image_ingredient.setVisibility(View.GONE);
        description_ingredient.setVisibility(View.GONE);
        imageMic.setVisibility(View.GONE);
        mic01.setVisibility(View.GONE);
        mic02.setVisibility(View.GONE);

        // 바인딩
        ArrayList<RecipeOrderDetail> recipeOrderDetails = apiRecipe.getRecipeOrderDetails();

        step.setText("Step " + Integer.toString(orderNum) + "/" + Integer.toString(recipeOrderDetails.size()));
        description.setText(recipeOrderDetails.get(orderNum - 1).getMainDescription());

        Glide.with(getContext())
             .load(recipeOrderDetails.get(orderNum-1).getImg())
             .into(imageView);

        return v;
    }
}