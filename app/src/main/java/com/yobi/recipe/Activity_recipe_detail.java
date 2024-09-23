package com.yobi.recipe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yobi.R;
import com.yobi.adapter.RecyclerViewAdapter;
import com.yobi.data.APIRecipe;
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
    TextView textTitle, textViewIngredient, profileName, ingredient, profileDescription;
    ImageView mainImage;
    CircleImageView profileImg;
    AppCompatButton follow, start;
    RecyclerView order;
    Button backButton;

    // 데이터
    APIRecipe apiRecipe;
    RetrofitAPI retrofitAPI;
    Retrofit retrofit;
    int recipeId;
    String defaultProfileImageUrl = "drawable/yobi_profile.png";

    @SuppressLint("MissingInflatedId")
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
        ingredient = findViewById(R.id.textView_recipe_detail_ingredient_title);
        order = findViewById(R.id.recyclerView_recipe_detail_order);
        backButton = findViewById(R.id.appCompatButton_recipe_detail_backspace);
        follow = findViewById(R.id.AppCompatButton);
        start = findViewById(R.id.button_recipe_detail_start); // start 버튼

        // 뒤로가기 버튼 리스너 설정
        backButton.setOnClickListener(v -> finish());

        // Retrofit 초기화
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-13-125-91-233.ap-northeast-2.compute.amazonaws.com:8080")  // 실제 API base URL로 교체해야 합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);

        // 서버에 recipeId로 API 요청
        if (recipeId != -1) {
            getRecipeDetailsFromServer(recipeId);
        }

        // start 버튼 클릭 리스너 추가
        start.setOnClickListener(v -> {
            // Intent 생성
            Intent intentToOrder = new Intent(Activity_recipe_detail.this, Activity_recipe_detail_order.class);
            intentToOrder.putExtra("recipeId", recipeId);  // recipeId 전달
            startActivity(intentToOrder);  // Activity 전환
        });
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

        // 레시피 재료 설정 (줄 바꿈 처리)
        String ingredientText = recipe.getIngredient();
        if (ingredientText != null) {
            // ,로 구분된 재료를 줄 바꿈으로 표시
            ingredientText = ingredientText.replace(", ", "\n");
            textViewIngredient.setText(ingredientText);
        }
        // updateDate에서 날짜만 가져와서 설정 (10자리만)
        String updateDate = recipe.getUpdateDate();
        if (updateDate != null && updateDate.length() >= 10) {
            profileDescription.setText(updateDate.substring(0, 10));
        }

        // 레시피 순서(Manual) 설정
        List<APIRecipe.Manual> manuals = recipe.getManuals();
        if (manuals != null && !manuals.isEmpty()) {
            ArrayList<APIRecipe.Manual> manualArrayList = new ArrayList<>(manuals);
            RecyclerViewAdapter<APIRecipe.Manual> adapter = new RecyclerViewAdapter<>(manualArrayList, this);
            order.setLayoutManager(new LinearLayoutManager(this));
            order.setAdapter(adapter);
        } else {
            order.setVisibility(View.GONE);
        }
    }

    private void fetchUserInfo(String userId) {
        // userId가 null인 경우 기본값 설정
        if (userId == null || userId.isEmpty()) {
            profileName.setText("요비");

            // 기본 프로필 이미지 설정 (drawable 리소스 ID 사용)
            Glide.with(Activity_recipe_detail.this)
                    .load(R.drawable.yobi_profile_color)  // drawable 리소스 사용
                    .into(profileImg);

            return;  // 더 이상 서버 요청을 하지 않도록 종료
        }

        // userId가 null이 아닌 경우에만 서버 요청
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
                        // 기본 프로필 이미지 설정 (drawable 리소스 ID 사용)
                        Glide.with(Activity_recipe_detail.this)
                                .load(R.drawable.yobi_profile_color)  // drawable 리소스 사용
                                .into(profileImg);
                    }
                } else {
                    // 실패 시 기본값 설정
                    profileName.setText("요비");
                    Glide.with(Activity_recipe_detail.this)
                            .load(R.drawable.yobi_profile_color)  // drawable 리소스 사용
                            .into(profileImg);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 실패 시 기본값 설정
                profileName.setText("요비");
                Glide.with(Activity_recipe_detail.this)
                        .load(R.drawable.yobi_profile_color)  // drawable 리소스 사용
                        .into(profileImg);
            }
        });
    }
}
