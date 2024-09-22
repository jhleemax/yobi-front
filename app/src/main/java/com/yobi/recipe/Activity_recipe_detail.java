package com.yobi.recipe;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yobi.R;
import com.yobi.adapter.RecyclerViewAdapter;
import com.yobi.data.APIRecipe;
import com.yobi.data.Manual;
import com.yobi.data.User;
import com.yobi.retrofit.RetrofitAPI;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_recipe_detail extends AppCompatActivity {

    // 컴포넌트
    TextView textTitle, textViewIngredient, textViewTools, profileName, profileDescription;
    ImageView mainImage;
    LinearLayout linearLayout01;
    CircleImageView profileImg;
    AppCompatButton follow, start;
    RecyclerView ingredient, tools, order;
    Button backButton;

    // 데이터
    APIRecipe apiRecipe;
    RetrofitAPI retrofitAPI;
    Retrofit retrofit;
    int recipeId;
    String defaultProfileImageUrl = "URL_TO_DEFAULT_IMAGE"; // Set your default profile image URL here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // 전달받은 Intent 값에서 recipeId 가져오기
        Intent intent = getIntent();
        recipeId = intent.getIntExtra("recipeId", -1); // 기본값 -1, 제대로 전달 안됐을 때 처리

        // 컴포넌트 연결
        textTitle = findViewById(R.id.textview_recipe_detail_title);
        mainImage = findViewById(R.id.imageView_recipe_detail_01);
        profileName = findViewById(R.id.textView_recipe_detail_profile_name);
        profileDescription = findViewById(R.id.textView_recipe_detail_profile_description);
        profileImg = findViewById(R.id.imageView_recipe_detail_profile);
        textViewIngredient = findViewById(R.id.textView_recipe_detail_ingredient);
        textViewTools = findViewById(R.id.textView_recipe_detail_tools);
        ingredient = findViewById(R.id.recyclerView_recipe_detail_ingredient);
        tools = findViewById(R.id.recyclerView_recipe_detail_tools);
        order = findViewById(R.id.recyclerView_recipe_detail_order);
        backButton = findViewById(R.id.appCompatButton_recipe_detail_backspace);
        follow = findViewById(R.id.AppCompatButton);
        start = findViewById(R.id.button_recipe_detail_start);

        // 뒤로가기 버튼 리스너 설정
        backButton.setOnClickListener(v -> finish());

        // Retrofit 초기화
        retrofit = new Retrofit.Builder()
                .baseUrl("http://your-api-base-url.com")  // 실제 API base URL로 교체해야 합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);

        // 서버에 recipeId로 API 요청
        if (recipeId != -1) {
            getRecipeDetailsFromServer(recipeId);
        }
    }

    private void getRecipeDetailsFromServer(int recipeId) {
        retrofitAPI.getRecipeDetails(recipeId).enqueue(new Callback<APIRecipe>() {
            @Override
            public void onResponse(Call<APIRecipe> call, Response<APIRecipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 서버로부터 받아온 데이터를 사용하여 화면을 업데이트합니다.
                    apiRecipe = response.body();
                    setupRecipeDetails(apiRecipe);
                    fetchUserInfo(apiRecipe.getUserId()); // 유저 정보 가져오기
                } else {
                    Log.e("Retrofit", "Failed to get recipe details. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<APIRecipe> call, Throwable t) {
                Log.e("Retrofit_onFailure", "Error: " + t.getMessage());
            }
        });
    }

    private void setupRecipeDetails(APIRecipe recipe) {
        // 레시피 제목 설정
        textTitle.setText(recipe.getTitle());

        // 레시피 이미지 설정
        Glide.with(this)
                .load(recipe.getRecipeThumbnail())
                .into(mainImage);

        // 레시피 재료 설정
        textViewIngredient.setText(recipe.getIngredient());

        // updateDate에서 날짜만 가져와서 설정 (10자리만)
        String updateDate = recipe.getUpdateDate();
        if (updateDate != null && updateDate.length() >= 10) {
            profileDescription.setText(updateDate.substring(0, 10));
        }

        // 레시피 순서(Manual) 설정
        List<APIRecipe.Manual> manuals = recipe.getManuals();
        if (manuals != null && !manuals.isEmpty()) {
            RecyclerViewAdapter<Manual> adapter = new RecyclerViewAdapter<>(new ArrayList<>(manuals), this);
            order.setLayoutManager(new LinearLayoutManager(this));
            order.setAdapter(adapter);
        } else {
            order.setVisibility(View.GONE);
        }
    }

    private void fetchUserInfo(String userId) {
        retrofitAPI.getUserInfo(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    profileName.setText(user.getName());

                    // 프로필 이미지 설정 (null 체크)
                    if (user.getUserProfile() != null && !user.getUserProfile().isEmpty()) {
                        Glide.with(Activity_recipe_detail.this)
                                .load(user.getUserProfile())
                                .into(profileImg);
                    } else {
                        // 기본 프로필 이미지 설정
                        Glide.with(Activity_recipe_detail.this)
                                .load(defaultProfileImageUrl)
                                .into(profileImg);
                    }
                } else {
                    // 실패 시 기본 프로필 이미지 설정
                    Glide.with(Activity_recipe_detail.this)
                            .load(defaultProfileImageUrl)
                            .into(profileImg);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 실패 시 기본 프로필 이미지 설정
                Glide.with(Activity_recipe_detail.this)
                        .load(defaultProfileImageUrl)
                        .into(profileImg);
            }
        });
    }
}
