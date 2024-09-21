package com.yobi.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.navercorp.nid.NaverIdLoginSDK;
import com.yobi.R;
import com.yobi.community.Fragment_community;
import com.yobi.data.NullOnEmptyConverterFactory;
import com.yobi.my.Fragment_my;
import com.yobi.recipe.Fragment_recipe;
import com.yobi.retrofit.RetrofitAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_main extends AppCompatActivity {

    // 컴포넌트
    BottomNavigationView bottomNavigationView;
    Fragment_main fragment_main;
    Fragment_recipe fragment_recipe;
    Fragment_community fragment_community;
    Fragment_my fragment_my;

    // 데이터 or Manager
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 초기화
        bottomNavigationView = findViewById(R.id.bottomNavView_main);
        fragment_main = new Fragment_main();
        fragment_recipe = new Fragment_recipe();
        fragment_community = new Fragment_community();
        fragment_my = new Fragment_my();

        Intent intent = getIntent();

        // 이벤트 리스너
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.fragment_home)
                    setFrag(0);
                if (menuItem.getItemId() == R.id.fragment_recipe)
                    setFrag(1);
                if (menuItem.getItemId() == R.id.fragment_community)
                    setFrag(2);
                if (menuItem.getItemId() == R.id.fragment_my)
                    setFrag(3);

                return true;
            }
        });


        setFrag(0); // 메인 화면으로 설정

        /*
        if(intent.getStringExtra("loginType").equals("naver")) {

            Retrofit retrofitNaver = new Retrofit.Builder()
                    .baseUrl("https://openapi.naver.com")
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitAPI retrofitAPINaver = retrofitNaver.create(RetrofitAPI.class);

            String header = "Bearer " + NaverIdLoginSDK.INSTANCE.getAccessToken();
            Log.e("Naver Header", header);

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Authorization", header);
            headerMap.put("X-Naver-Client-Id", "InT0mn3ckZlsmF7N2TZB");
            headerMap.put("X-Naver-Client-Secret", "iyFAmsTbT4");

            retrofitAPINaver.callNaverProfile(headerMap).enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    Log.e("Naver response_code", response.toString());
                    Log.e("Naver response_code", response.body().toString());
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable throwable) {
                    Log.e("retrofitNaver_onFailure", throwable.getMessage());
                }
            });

        }
         */
    }
    private void setFrag(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n) {
            case 0:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_main).commitAllowingStateLoss();
                break;
            case 1:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_recipe).commitAllowingStateLoss();
                break;
            case 2:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_community).commitAllowingStateLoss();
                break;
            case 3:
                fragmentTransaction.replace(R.id.frameLayout_main, fragment_my).commitAllowingStateLoss();
                break;
        }
    }
}