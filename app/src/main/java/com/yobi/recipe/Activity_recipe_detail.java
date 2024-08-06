package com.yobi.recipe;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yobi.R;
import com.yobi.adapter.RecyclerViewAdapter;
import com.yobi.data.APIRecipe;
import com.yobi.data.RecipeOrderDetail;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_recipe_detail extends AppCompatActivity {

    // 컴포넌트
    TextView textView01, textViewIngredient, textViewTools;
    ImageView mainImage;
    TextView description, profileName, profileDescription;
    LinearLayout linearLayout01;
    CircleImageView profileImg;
    AppCompatButton follow, start;
    RecyclerView ingredient, tools, order;
    Button backButton;

    // 데이터
    APIRecipe apiRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 전달받은 Intent 값(APIRecipe) 가져오기
        Intent intent = getIntent();
        String separator = intent.getStringExtra("separator");
        if(separator.equals("api"))
            apiRecipe = (APIRecipe) intent.getSerializableExtra("clickedRecipe");

        // 컴포넌트 연결
        textView01 = (TextView) findViewById(R.id.textview_recipe_detail_title);
        mainImage = (ImageView) findViewById(R.id.imageView_recipe_detail_01);
        description = (TextView) findViewById(R.id.textView_recipe_detail_description);
        linearLayout01 = (LinearLayout) findViewById(R.id.linearLayout_recipe_detail_01);
        profileImg = (CircleImageView) findViewById(R.id.imageView_recipe_detail_profile);
        profileName = (TextView) findViewById(R.id.textView_recipe_detail_profile_name);
        profileDescription = (TextView) findViewById(R.id.textView_recipe_detail_profile_description);
        follow = (AppCompatButton) findViewById(R.id.AppCompatButton);
        start = (AppCompatButton) findViewById(R.id.button_recipe_detail_start);

        textViewIngredient = (TextView) findViewById(R.id.textView_recipe_detail_ingredient);
        textViewTools = (TextView) findViewById(R.id.textView_recipe_detail_tools);
        ingredient = (RecyclerView) findViewById(R.id.recyclerView_recipe_detail_ingredient);
        tools = (RecyclerView) findViewById(R.id.recyclerView_recipe_detail_tools);
        order = (RecyclerView) findViewById(R.id.recyclerView_recipe_detail_order);
        backButton = (Button) findViewById(R.id.appCompatButton_recipe_detail_backspace);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_recipe_detail.this, Activity_recipe_detail_order.class);
                if(separator.equals("api"))
                    intent.putExtra("dataSet", apiRecipe);
                intent.putExtra("order_num", 1);
                startActivity(intent);
            }
        });

        // 비가시 처리
        linearLayout01.setVisibility(View.GONE);
        textViewTools.setVisibility(View.GONE);
        textViewIngredient.setVisibility(View.GONE);
        ingredient.setVisibility(View.GONE);
        tools.setVisibility(View.GONE);

        if(separator.equals("api")) { // 공공 api 레시피일 경우

            // 프로필 처리
            profileImg.setBackgroundColor(getColor(R.color.main_theme));
            Drawable profile = getDrawable(R.drawable.yobi_profile);
            profileImg.setImageDrawable(profile);

            // 프로필 이름
            profileName.setText("YOBI Official");
            profileDescription.setText("YOBI 공식 프로필 입니다");

            // 레시피 제목
            textView01.setText(apiRecipe.getRcpnm());

            // 레시피 설명
            description.setText(apiRecipe.getRcp_NA_TIP());

            // 레시피 사진
            Glide.with(getApplicationContext())
                  .load(apiRecipe.getAtt_FILE_NO_MAIN())
                  .into(mainImage);

            RecyclerViewAdapter<RecipeOrderDetail> recipeOrderDetailRecyclerViewAdapter = new RecyclerViewAdapter<>(apiRecipe.getRecipeOrderDetails(), getApplicationContext());
            order.setAdapter(recipeOrderDetailRecyclerViewAdapter);

            order.setHasFixedSize(true);

        } else if(separator.equals("user")) { // 사용자 레시피일 경우

        }
    }
}