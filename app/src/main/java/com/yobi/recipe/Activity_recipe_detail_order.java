package com.yobi.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.yobi.R;
import com.yobi.data.APIRecipe;
import com.yobi.retrofit.RetrofitAPI;
import java.util.ArrayList;
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

    // TTS 및 STT
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private boolean ttsInitialized = false;

    // Retrofit
    RetrofitAPI retrofitAPI;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_order);

        // TTS 초기화
        tts = new TextToSpeech(this, this);

        // OnUtteranceProgressListener 설정
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // TTS 시작 시 작업
            }

            @Override
            public void onDone(String utteranceId) {
                // TTS가 끝난 후 STT를 자동으로 시작
                runOnUiThread(() -> startListening());
            }

            @Override
            public void onError(String utteranceId) {
                // 오류 처리
                Log.e("TTS", "Error occurred during speech.");
            }
        });

        // STT 초기화
        initSTT();

        // 전달받은 Intent 값에서 recipeId 가져오기
        recipeId = getIntent().getIntExtra("recipeId", -1);

        // 컴포넌트 연결
        nextButton = findViewById(R.id.appCompatButton_recipe_detail_order_next);
        amount = findViewById(R.id.spinner_recipe_detail_order_amount);
        difficulty = findViewById(R.id.spinner_recipe_detail_order_difficulty);
        start = findViewById(R.id.linearLayout_recipe_detail_order_start);
        backButton = findViewById(R.id.button_login_normal_backspace_01);
        frameLayout = findViewById(R.id.frameLayout_recipe_detail_order);

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

    // TTS 초기화
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            ttsInitialized = true;
        } else {
            Toast.makeText(this, "TTS 초기화 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // STT 초기화
    private void initSTT() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                startListening(); // 에러 시 다시 듣기 시작
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    handleVoiceCommand(matches.get(0)); // 명령어 처리
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        startListening();
    }

    // 음성 인식 시작
    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        speechRecognizer.startListening(intent);
    }

    // 음성 명령어 처리
    // 음성 명령어 처리
    private void handleVoiceCommand(String command) {
        command = command.trim().toLowerCase();

        switch (command) {
            case "다음": case "다음거": case "다음으로": case "다음 거": case "넘겨": case "넘겨줘": case "넘겨 줘": case "다음 단계": case "끝났어": case "스킵":
                nextButton.performClick();
                break;
            case "이전": case "이전으로": case "뒤로": case "뒤로 가줘": case "앞으로": case "앞으로 가줘": case "이전 단계":
                backButton.performClick();
                break;
            case "정지": case "일시정지": case "잠깐": case "잠깐만": case "잠시만": case "기다려": case "기다려 줘": case "멈춰": case "멈춰봐":
                if (tts != null) {
                    tts.stop();
                }
                break;
            case "다시": case "다시 읽어": case "다시 읽어 줘": case "뭐라고": case "못들었어": case "다시 말해줘": case "시작":
                if (apiRecipe != null && orderNum > 0) {
                    APIRecipe.Manual currentManual = apiRecipe.getManuals().get(orderNum - 1);
                    speak(currentManual.getDescription());
                }
                break;
            case "그만": case "그만하기": case "그만할래": case "끝내줘": case "꺼줘": case "종료": case "종료해 줘": case "종료해":
                finish(); // 현재 액티비티 종료
                break;
            default:
                // 알 수 없는 명령일 경우 Toast 출력 후 STT 재시작
                Toast.makeText(this, "알 수 없는 명령입니다.", Toast.LENGTH_SHORT).show();
                startListening(); // STT 재시작
                break;
        }
    }


    // 텍스트를 TTS로 읽음
    private void speak(String text) {
        if (ttsInitialized && tts != null) {
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "utteranceId");
        }
    }

    // 서버에서 recipeId로 API 요청하여 레시피 정보를 가져오기
    private void getRecipeDetailsFromServer(int recipeId) {
        retrofitAPI.getRecipeDetails(recipeId).enqueue(new Callback<APIRecipe>() {
            @Override
            public void onResponse(Call<APIRecipe> call, Response<APIRecipe> response) {
                if (response.isSuccessful() && response.body() != null) {
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

        Fragment_recipe_detail_order fragment = Fragment_recipe_detail_order.newInstance(n, apiRecipe);

        fragmentTransaction.replace(R.id.frameLayout_recipe_detail_order, fragment).commit();

        // 명령어에 따른 읽기 동작
        APIRecipe.Manual currentManual = apiRecipe.getManuals().get(n - 1);
        speak(currentManual.getDescription());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
