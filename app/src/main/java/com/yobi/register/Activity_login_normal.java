package com.yobi.register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yobi.R;
import com.yobi.main.Activity_main;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity_login_normal extends AppCompatActivity {
    // 컴포넌트
    EditText et_email, et_pwd;
    Button btn_login, btn_back;
    Button btnroot;

    // 데이터
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_normal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        et_email = findViewById(R.id.editText_login_normal_email);
        et_pwd = findViewById(R.id.editText_login_normal_pwd);
        btn_login = findViewById(R.id.button_login_normal_submit);
        btn_back = findViewById(R.id.button_login_normal_backspace_01);

        btnroot = findViewById(R.id.rootbutton);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnroot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_login_normal.this, Activity_main.class); // 다음 화면 액티비티로 변경
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser() {
        String email = et_email.getText().toString().trim();
        String password = et_pwd.getText().toString().trim();

        new LoginTask().execute(email, password);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            HttpURLConnection conn = null;
            BufferedReader br = null;

            try {
                URL url = new URL("http://10.0.2.2:8080/user/login"); // 서버 주소로 변경
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userId", email);
                jsonParam.put("userPass", password);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                InputStream is;
                if (responseCode >= 200 && responseCode < 400) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }

                br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                Log.d("LoginTask", "Response Code: " + responseCode);
                Log.d("LoginTask", "Response: " + response.toString());

                // 서버 응답을 직접 로그로 출력
                Log.d("LoginTask", "Raw Response: " + response.toString());

                if (responseCode >= 200 && responseCode < 300) {
                    return response.toString(); // 성공 응답 처리
                } else {
                    // 오류 응답 처리
                    JSONObject errorResponse = new JSONObject(response.toString());
                    return errorResponse.getString("message");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "An error occurred: " + e.getMessage();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("LoginTask", "onPostExecute: " + result);
            Toast.makeText(Activity_login_normal.this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("로그인 성공")) { // 서버에서 "로그인 성공" 문자열을 반환하는 경우
                Intent intent = new Intent(Activity_login_normal.this, Activity_main.class); // 다음 화면 액티비티로 변경
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", et_email.getText().toString());
                editor.commit();
                startActivity(intent);
                finish();
            }
        }
    }
}