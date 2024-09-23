package com.yobi.recipe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yobi.R;
import com.yobi.data.APIRecipe;
import com.yobi.retrofit.RetrofitAPI;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_recipe_detail_order extends AppCompatActivity implements TextToSpeech.OnInitListener {

    // 데이터
    APIRecipe apiRecipe;
    int orderNum;
    int recipeId;

    // 컴포넌트
    Spinner difficulty, amount;
    LinearLayout start;
    Button backButton;
    AppCompatButton nextButton;
    FrameLayout frameLayout;

    // Fragment Manager
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment_recipe_detail_order fragmentRecipeDetailOrder;

    // Retrofit
    RetrofitAPI retrofitAPI;
    Retrofit retrofit;

    // TTS 객체 추가
    private TextToSpeech tts;
    private String currentDescription = "";  // 현재 설명을 저장
    private boolean isTTSInitialized = false; // TTS 초기화 완료 여부 확인

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail_order);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TTS 초기화
        tts = new TextToSpeech(this, this);

        // 전달받은 Intent 값에서 recipeId 가져오기
        recipeId = getIntent().getIntExtra("recipeId", -1); // 기본값 -1

        // 컴포넌트 연결
        nextButton = findViewById(R.id.appCompatButton_recipe_detail_order_next);
        amount = findViewById(R.id.spinner_recipe_detail_order_amount);
        difficulty = findViewById(R.id.spinner_recipe_detail_order_difficulty);
        start = findViewById(R.id.linearLayout_recipe_detail_order_start);
        backButton = findViewById(R.id.button_login_normal_backspace_01);
        frameLayout = findViewById(R.id.frameLayout_recipe_detail_order);

        fragmentRecipeDetailOrder = new Fragment_recipe_detail_order();

        // 비가시 처리
        amount.setVisibility(View.GONE);
        difficulty.setVisibility(View.GONE);
        start.setVisibility(View.GONE);

        // Retrofit 초기화
        retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-13-125-91-233.ap-northeast-2.compute.amazonaws.com:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);

        // 서버에서 recipeId로 API 요청하여 레시피 정보를 가져오기
        if (recipeId != -1) {
            getRecipeDetailsFromServer(recipeId);
        }

        // 이벤트
        backButton.setOnClickListener(v -> {
            if (orderNum == 1)
                finish();
            else
                setFrag(--orderNum);
        });

        nextButton.setOnClickListener(v -> {
            if (apiRecipe != null && orderNum < apiRecipe.getManuals().size()) {
                setFrag(++orderNum);
            } else {
                Toast.makeText(this, "마지막 단계입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 서버에서 recipeId로 API 요청하여 레시피 정보를 가져오기
    private void getRecipeDetailsFromServer(int recipeId) {
        retrofitAPI.getRecipeDetails(recipeId).enqueue(new Callback<APIRecipe>() {
            @Override
            public void onResponse(Call<APIRecipe> call, Response<APIRecipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 서버로부터 받아온 데이터를 사용하여 화면을 업데이트합니다.
                    apiRecipe = response.body();
                    orderNum = 1; // 초기 orderNum 설정
                    setFrag(orderNum); // 처음 fragment 설정
                } else {
                    Log.e("RecipeDetailOrder", "Failed to load recipe. Response code: " + response.code());
                    Toast.makeText(Activity_recipe_detail_order.this, "레시피 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIRecipe> call, Throwable t) {
                Log.e("RecipeDetailOrder", "Error: " + t.getMessage());
                Toast.makeText(Activity_recipe_detail_order.this, "서버 요청에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fragment 설정
    private void setFrag(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment_recipe_detail_order fragment = new Fragment_recipe_detail_order();

        // Manual 데이터에서 step에 해당하는 데이터를 추출
        APIRecipe.Manual currentManual = apiRecipe.getManuals().get(n - 1);  // step 1은 인덱스 0에 해당

        String mainDescription = currentManual.getDescription();
        String imgURL = currentManual.getImage();

        // Log 추가
        Log.d("ActivityRecipeDetailOrder", "Step " + n + ": " + mainDescription);
        Log.d("ActivityRecipeDetailOrder", "Image URL: " + imgURL);

        Bundle bundle = new Bundle();
        bundle.putSerializable("apiRecipe", apiRecipe);  // 'apiRecipe'를 전달
        bundle.putInt("orderNum", n);
        bundle.putString("mainDescription", mainDescription);
        bundle.putString("imgURL", imgURL);
        fragment.setArguments(bundle);

        // Description을 저장하고 Fragment를 교체
        currentDescription = mainDescription;
        fragmentTransaction.replace(R.id.frameLayout_recipe_detail_order, fragment).commit();

        // TTS로 설명을 읽음
        if (isTTSInitialized) {
            speak(currentDescription);  // 초기화가 끝났다면 바로 음성을 읽음
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.KOREAN);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TTS: 언어를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            } else {
                isTTSInitialized = true;  // TTS 초기화 완료
                speak(currentDescription);  // 초기화 후에 설명을 읽음
            }
        } else {
            Toast.makeText(this, "TTS 초기화 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak(String text) {
        if (isTTSInitialized && text != null && !text.isEmpty()) {
            Log.d("TTS", "Speaking: " + text); // TTS로 말할 내용을 로그로 확인
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.d("TTS", "TTS not initialized or empty text.");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
