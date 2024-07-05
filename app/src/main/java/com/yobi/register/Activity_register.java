package com.yobi.register;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yobi.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity_register extends AppCompatActivity {
    // 컴포넌트
    EditText et_email, et_pwd, et_checkpwd, et_nickname;
    Button btn_back, btn_next, btn_backarrow, btn_checkemail;
    CheckBox cb_all, cb_term1, cb_term2, cb_term3, cb_term4, cb_term5, cb_term6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_email = findViewById(R.id.editText_recipe_write_title);
        et_pwd = findViewById(R.id.editText_register_pwd);
        et_checkpwd = findViewById(R.id.editText_register_checkPwd);
        et_nickname = findViewById(R.id.editText_register_nickname);
        btn_back = findViewById(R.id.button_register_backspace_02);
        btn_next = findViewById(R.id.button_register_submit);
        btn_backarrow = findViewById(R.id.button_register_backspace_01);
        btn_checkemail = findViewById(R.id.button_register_checkEmail);
        cb_all = findViewById(R.id.checkBox_register_agreementAll);
        cb_term1 = findViewById(R.id.checkBox_register_agreement_01);
        cb_term2 = findViewById(R.id.checkBox_register_agreement_02);
        cb_term3 = findViewById(R.id.checkBox_register_agreement_03);
        cb_term4 = findViewById(R.id.checkBox_register_agreement_04);
        cb_term5 = findViewById(R.id.checkBox_register_agreement_05);
        cb_term6 = findViewById(R.id.checkBox_register_agreement_06);

        btn_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_checkemail.setVisibility(View.GONE);
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cb_term1.setChecked(isChecked);
                cb_term2.setChecked(isChecked);
                cb_term3.setChecked(isChecked);
                cb_term4.setChecked(isChecked);
                cb_term5.setChecked(isChecked);
                cb_term6.setChecked(isChecked);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_pwd.getText().toString().trim();
                String checkPassword = et_checkpwd.getText().toString().trim();

                if (!password.equals(checkPassword)) {
                    Toast.makeText(Activity_register.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!cb_term1.isChecked() || !cb_term2.isChecked() || !cb_term3.isChecked() || !cb_term4.isChecked() ) {
                    Toast.makeText(Activity_register.this, "동의하지 않은 필수 약관이 있습니다.\n약관 동의 후 진행해 주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = et_email.getText().toString().trim();
        String password = et_pwd.getText().toString().trim();
        String nickname = et_nickname.getText().toString().trim();

        new RegisterTask().execute(email, password, nickname);
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String nickname = params[2];

            HttpURLConnection conn = null;
            BufferedReader br = null;

            try {
                URL url = new URL("http://10.0.2.2:8080/user/signup"); // 서버 주소로 변경
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userId", email);
                jsonParam.put("userPass", password);
                jsonParam.put("username", nickname);

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

                Log.d("RegisterTask", "Response Code: " + responseCode);
                Log.d("RegisterTask", "Response: " + response.toString());

                if (response.length() == 0) {
                    return responseCode == HttpURLConnection.HTTP_OK ? "회원가입에 성공했습니다!\n로그인해 주세요." : "An error occurred";
                }

                JSONObject responseObject = new JSONObject(response.toString());
                return responseObject.getString("message");

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
            Log.d("RegisterTask", "onPostExecute: " + result);
            Toast.makeText(Activity_register.this, result, Toast.LENGTH_LONG).show();
            if (result.equals("회원가입에 성공했습니다!\n로그인해 주세요.")) {
                Intent intent = new Intent(Activity_register.this, Activity_login_normal.class);
                startActivity(intent);
                finish();
            }
        }
    }
}