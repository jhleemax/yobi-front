package com.yobi.recipe;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yobi.R;
import com.yobi.adapter.RecyclerViewAdapter;
import com.yobi.data.APIRecipe;
import com.yobi.retrofit.RetrofitAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_recipe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_recipe extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Retrofit
    Retrofit retrofit;
    // 데이터 변수
    List<APIRecipe> apiRecipesList;
    // 컴포넌트
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_recipe() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_recipe.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_recipe newInstance(String param1, String param2) {
        Fragment_recipe fragment = new Fragment_recipe();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        // 초기화
        recyclerView = v.findViewById(R.id.recyclerView_recipe);
        floatingActionButton = v.findViewById(R.id.floatingActionButton_recipe);

        // 레트로핏 통신
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        retrofitAPI.getAPIRecipes().enqueue(new Callback<List<APIRecipe>>() {
            @Override
            public void onResponse(Call<List<APIRecipe>> call, Response<List<APIRecipe>> response) {
                if(response.isSuccessful()) {
                    apiRecipesList = response.body();
                }

                if(apiRecipesList != null && !apiRecipesList.isEmpty()) {
                    ArrayList<APIRecipe> apiRecipesArrayList = new ArrayList<>(apiRecipesList);

                    RecyclerViewAdapter<APIRecipe> apiRecipeRecyclerViewAdapter = new RecyclerViewAdapter<>(apiRecipesArrayList, getActivity().getApplicationContext());
                    recyclerView.setAdapter(apiRecipeRecyclerViewAdapter);

                    // 리사이클러뷰 아이템 클릭 리스너
                    apiRecipeRecyclerViewAdapter.setOnItemClickListener((view, position) -> {
                        // 아이템 클릭 처리
                        APIRecipe clickedRecipe = apiRecipesArrayList.get(position);
                        // 예를 들어, 클릭된 레시피 상세 정보 액티비티 시작
                        Intent intent = new Intent(getContext(), Activity_recipe_detail.class);
                        intent.putExtra("clickedRecipe", clickedRecipe);
                        intent.putExtra("separator", "api");
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<APIRecipe>> call, Throwable throwable) {
                Log.e("retrofit_onFailure", throwable.getMessage());
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Activity_recipe_write.class);
                intent.putExtra("separator", "w");
                startActivity(intent);
            }
        });

        return v;
    }
}