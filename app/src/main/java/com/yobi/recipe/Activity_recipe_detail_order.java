package com.yobi.recipe;

import android.os.Bundle;
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

public class Activity_recipe_detail_order extends AppCompatActivity {

    // 데이터
    APIRecipe apiRecipe;
    int orderNum;

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

        apiRecipe = (APIRecipe) getIntent().getSerializableExtra("dataSet");
        orderNum = getIntent().getIntExtra("order_num", 1);

        nextButton = (AppCompatButton) findViewById(R.id.appCompatButton_recipe_detail_order_next);
        amount = (Spinner) findViewById(R.id.spinner_recipe_detail_order_amount);
        difficulty = (Spinner) findViewById(R.id.spinner_recipe_detail_order_difficulty);
        start = (LinearLayout) findViewById(R.id.linearLayout_recipe_detail_order_start);
        backButton = (Button) findViewById(R.id.button_login_normal_backspace_01);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout_recipe_detail_order);

        fragmentRecipeDetailOrder = new Fragment_recipe_detail_order();
        setFrag(orderNum);

        // 비가시 처리
        amount.setVisibility(View.GONE);
        difficulty.setVisibility(View.GONE);
        start.setVisibility(View.GONE);

        // 이벤트
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderNum == 1)
                    finish();
                else
                    setFrag(--orderNum);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderNum >= apiRecipe.getRecipeOrderDetails().size())
                    Toast.makeText(getApplicationContext(), "마지막 페이지 입니다", Toast.LENGTH_LONG).show();
                else
                    setFrag(++orderNum);
            }
        });
    }

    private void setFrag(int n) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment_recipe_detail_order fragment = new Fragment_recipe_detail_order();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dataSet", apiRecipe);
        bundle.putInt("orderNum", n);
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.frameLayout_recipe_detail_order, fragment).commit();
    }
}