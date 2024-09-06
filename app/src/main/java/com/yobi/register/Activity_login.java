package com.yobi.register;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.oauth.view.NidOAuthLoginButton;
import com.yobi.R;
import com.yobi.data.NullOnEmptyConverterFactory;
import com.yobi.data.User;
import com.yobi.main.Activity_main;
import com.yobi.retrofit.RetrofitAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_login extends AppCompatActivity {

    // 데이터
    SharedPreferences autoLogin;

    // 컴포넌트
    TextView login, register;
    AppCompatButton google;
    NidOAuthLoginButton naver;
    ConstraintLayout constraintLayout_autoLogin;
    CheckBox checkBox_autoLogin;

    // API
    GoogleSignInClient mGoogoleSigninClient;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 컴포넌트 초기화
        login = findViewById(R.id.textView_login_login);
        register = findViewById(R.id.textView_login_register);
        google = findViewById(R.id.appCompatButton_login_google);
        naver = findViewById(R.id.appCompatButton_login_naver);
        constraintLayout_autoLogin = findViewById(R.id.constraintLayout_autoLogin);
        checkBox_autoLogin = findViewById(R.id.checkBox_autoLogin);

        // Retrofit Setting
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 구글 로그인
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogoleSigninClient = GoogleSignIn.getClient(Activity_login.this, gso);

        // 네이버 로그인
        NaverIdLoginSDK.INSTANCE.initialize(this, "InT0mn3ckZlsmF7N2TZB", "iyFAmsTbT4", "YOBI");

        // 컴포넌트 리스너
        constraintLayout_autoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox_autoLogin.setChecked(!checkBox_autoLogin.isChecked());
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_login_normal.class);
                startActivity(intent);
                //finish(); 뒤로가기의 케이스 고려
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_register.class);
                startActivity(intent);
                //finish(); 뒤로가기의 케이스 고려
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignin();
            }
        });

        naver.setOAuthLogin(new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                String accessToken = NaverIdLoginSDK.INSTANCE.getAccessToken();
                Log.e("네이버로그인 accessToken", accessToken);

                new Thread(() -> {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    Request request = new Request.Builder()
                            .url("https://openapi.naver.com/v1/nid/me")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + NaverIdLoginSDK.INSTANCE.getAccessToken())
                            .build();
                    try {
                        okhttp3.Response response = client.newCall(request).execute();
                        String jsonString = response.body().string();
                        Log.e("Naver response", jsonString);

                        JSONObject jObject = new JSONObject(jsonString);
                        Log.e("response", jObject.get("response").toString());
                        JSONObject userJson = jObject.getJSONObject("response");
                        Log.e("response json", userJson.toString());

                        SharedPreferences userPref = getSharedPreferences("user", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor userEdit = userPref.edit();
                        userEdit.putString("socialType", "naver");
                        userEdit.putString("userId", userJson.getString("id"));
                        userEdit.putString("userName", userJson.getString("name"));
                        userEdit.putString("nickeName", userJson.getString("nickname"));
                        userEdit.putString("email", userJson.getString("email"));

                        User user = new User();
                        user.setName(userJson.getString("name"));
                        user.setEmail(userJson.getString("email"));
                        user.setPassWord("0000");
                        user.setPhoneNumber(userJson.getString("mobile"));
                        user.setSocialType("naver");
                        user.setNickName(userJson.getString("nickname"));
                        user.setUserId(userJson.getString("id"));

                        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                        retrofitAPI.signUp(user).enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                if(response.code() == 200) {

                                    try {
                                        Log.e("네이버 회원가입 id : ", userJson.getString("id"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Intent intent = new Intent(getApplicationContext(), Activity_main.class);
                                    intent.putExtra("loginType", "naver");
                                    intent.putExtra("accessToken", accessToken);
                                    startActivity(intent);

                                } else {
                                    try {
                                        retrofitAPI.singIn(userJson.getString("id"), "naver").enqueue(new Callback<Integer>() {
                                            @Override
                                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                                try {
                                                    Log.e("네이버 로그인 id : ", userJson.getString("id"));
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                Intent intent = new Intent(getApplicationContext(), Activity_main.class);
                                                intent.putExtra("loginType", "naver");
                                                intent.putExtra("accessToken", accessToken);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onFailure(Call<Integer> call, Throwable throwable) {
                                                Log.e("retrofit_onFailure", throwable.getMessage());
                                            }
                                        });
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable throwable) {
                                Log.e("retrofit_onFailure", throwable.getMessage());
                            }
                        });

                        // 자동 로그인 체크시
                        if(checkBox_autoLogin.isChecked()) {
                            userEdit.putString("autoLogin", "y");
                        } else {
                            userEdit.putString("autoLogin", "n");
                        }
                        userEdit.commit();

                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

            @Override
            public void onFailure(int i, @NonNull String s) {
                Log.e("네아로", "onFailure: httpStatus - " + i + " / message - " + s);
            }

            @Override
            public void onError(int i, @NonNull String s) {
                Log.e("네아로", "onError: errorCode - " + i + " / message - " + s);
            }
        });

        autoLogin = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 자동로그인 사용 시
        if(autoLogin.getString("autoLogin", "n").equals("y")) {
            // 구글 로그인 시
            if(autoLogin.getString("socialType", "null").equals("google")) {
                google.callOnClick();
            } else if(autoLogin.getString("socialType", "null").equals("naver")) {
                naver.callOnClick();
            }
        }
    }

    private void googleSignin() {
        Intent signinIntent = mGoogoleSigninClient.getSignInIntent();
        startActivityForResult(signinIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if(acct != null) {

                RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

                User user = new User();
                user.setName(acct.getDisplayName());
                user.setSocialType("google");
                user.setUserId(acct.getId());
                user.setPassWord("000000");
                user.setNickName(acct.getFamilyName());
                user.setEmail(acct.getEmail());
                user.setPhoneNumber("000-0000-0000");

                SharedPreferences userPref = getSharedPreferences("user", Activity.MODE_PRIVATE);
                SharedPreferences.Editor userEdit = userPref.edit();
                userEdit.putString("socialType", "google");
                userEdit.putString("userId", acct.getId());
                userEdit.putString("userName", acct.getDisplayName());
                userEdit.putString("nickeName", acct.getFamilyName());
                userEdit.putString("email", acct.getEmail());

                // 자동 로그인 체크시
                if(checkBox_autoLogin.isChecked()) {
                    userEdit.putString("autoLogin", "y");
                } else {
                    userEdit.putString("autoLogin", "n");
                }
                userEdit.commit();

                // 회원가입
                retrofitAPI.signUp(user).enqueue(new Callback<Integer>() {
                    // 성공 시
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        Log.e("response_code", response.toString());
                        // 회원가입 정상
                        if(response.code() == 200) {
                            String name = acct.getId();

                            Intent intent = new Intent(getApplicationContext(), Activity_main.class);
                            intent.putExtra("loginType", "google");
                            intent.putExtra("account", acct);

                            startActivity(intent);

                            Log.e("구글 회원가입 getId : ", name);
                        } else {
                            // 로그인 시도
                            retrofitAPI.singIn(acct.getId(), "google").enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    Log.e("response_code", response.toString());

                                    String name = acct.getId();

                                    Intent intent = new Intent(getApplicationContext(), Activity_main.class);
                                    intent.putExtra("loginType", "google");
                                    intent.putExtra("account", acct);

                                    startActivity(intent);

                                    Log.e("구글 로그인 getId : ", name);
                                }
                                @Override
                                public void onFailure(Call<Integer> call, Throwable throwable) {
                                    Log.e("retrofit_onFailure", throwable.getMessage());
                                }
                            });
                        }
                    }

                    // 실패 시
                    @Override
                    public void onFailure(Call<Integer> call, Throwable throwable) {
                        Log.e("retrofit_onFailure", throwable.getMessage());
                    }
                });
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}