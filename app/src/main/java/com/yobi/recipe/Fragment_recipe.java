package com.yobi.recipe;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yobi.R;
import com.yobi.adapter.RecyclerViewAdapter;
import com.yobi.data.APIRecipe;
import com.yobi.data.RecipeResponse;
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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Retrofit
    Retrofit retrofit;
    RetrofitAPI retrofitAPI;
    List<APIRecipe> apiRecipesList;
    ArrayList<APIRecipe> apiRecipesArrayList;

    // 컴포넌트
    RecyclerView recyclerView;
    RecyclerViewAdapter<APIRecipe> apiRecipeRecyclerViewAdapter;
    FloatingActionButton floatingActionButton;
    SearchView recipe_searchview;

    // 페이지 관리 변수
    private int currentPage = 0;

    // 검색어 변수
    private String currentQuery = "";

    private String mParam1;
    private String mParam2;

    public Fragment_recipe() {
        // Required empty public constructor
    }

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
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        recyclerView = v.findViewById(R.id.recyclerView_recipe);
        floatingActionButton = v.findViewById(R.id.floatingActionButton_recipe);
        recipe_searchview = v.findViewById(R.id.searchView_recipe);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 어댑터 연결
        apiRecipesArrayList = new ArrayList<>();
        apiRecipeRecyclerViewAdapter = new RecyclerViewAdapter<>(apiRecipesArrayList, getActivity().getApplicationContext());
        recyclerView.setAdapter(apiRecipeRecyclerViewAdapter);

        // Retrofit 초기화
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-13-125-91-233.ap-northeast-2.compute.amazonaws.com:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);

        // 기본 레시피 목록 로드
        currentPage = 0;  // 새 검색이 시작될 때 페이지를 0으로 초기화
        apiRecipesArrayList.clear();  // 기존 데이터를 비움
        loadRecipes(currentPage);  // 새로운 검색 결과 로드
        Log.e("lodRecipes", "기본 레시피 목록 로드 완료");

        // SearchView 리스너 설정
        recipe_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                currentPage = 0;  // 새 검색이 시작될 때 페이지를 0으로 초기화
                apiRecipesArrayList.clear();  // 기존 데이터를 비움
                loadRecipesByTitle(currentQuery, currentPage);  // 새로운 검색 결과 로드
                Log.e("lodRecipes", "검색어 제출됨");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // 플로팅 액션 버튼 클릭 시 작성 액티비티로 이동
        floatingActionButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), Activity_recipe_write.class);
            intent.putExtra("separator", "w");
            startActivity(intent);
        });

        setupScrollListener(); // 스크롤 리스너 추가

        return v;
    }

    // 기본 레시피 로드
    private void loadRecipes(int page) {
        retrofitAPI.getRecipeAll(page).enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Retrofit Response", "Response: " + response.body().toString());

                    // UI 스레드에서 RecyclerView 업데이트
                    getActivity().runOnUiThread(() -> {
                        List<APIRecipe> recipes = response.body().getContent();  // RecipeResponse의 content 필드에서 레시피 리스트 추출
                        if (recipes != null) {
                            apiRecipesArrayList.addAll(recipes);  // 리스트에 데이터 추가
                            apiRecipeRecyclerViewAdapter.notifyDataSetChanged();  // 어댑터 갱신
                        } else {
                            Log.e("Retrofit", "Recipe list is empty.");
                        }
                    });
                } else {
                    Log.e("Retrofit", "No recipes found. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("Retrofit_onFailure", "Failed to load recipes: " + t.getMessage());
            }
        });
    }

    // 검색어로 레시피 로드
    private void loadRecipesByTitle(String title, int page) {
        retrofitAPI.getRecipeByTitle(title, page).enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 서버에서 응답을 정상적으로 받았을 때 데이터를 처리하는 부분
                    List<APIRecipe> recipes = response.body().getContent();
                    if (recipes != null) {
                        apiRecipesArrayList.addAll(recipes);
                        apiRecipeRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Retrofit", "Recipe list is empty.");
                    }
                } else {
                    // 서버가 응답했지만 오류 코드가 반환되었을 때
                    Log.e("Retrofit", "No recipes found. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("Retrofit_onFailure", "Failed to load recipes: " + t.getMessage());
            }
        });
    }

    // RecyclerView가 스크롤될 때 추가 로딩을 위한 메서드
    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) { // 최하단에 도달했을 때
                    currentPage++;  // 페이지 증가
                    if (currentQuery.isEmpty()) {
                        loadRecipes(currentPage);  // 새로운 페이지의 레시피 로드
                    } else {
                        loadRecipesByTitle(currentQuery, currentPage);  // 새로운 페이지의 검색 결과 로드
                    }
                }
            }
        });
    }
}
