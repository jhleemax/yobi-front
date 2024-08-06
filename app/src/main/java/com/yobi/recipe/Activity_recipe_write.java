package com.yobi.recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yobi.R;
import com.yobi.adapter.RecyclerViewAdapter;
import com.yobi.data.UserRecipeImage;
import com.yobi.data.UserRecipeIngredient;
import com.yobi.data.UserRecipeOrder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Activity_recipe_write extends AppCompatActivity {

    // 컴포넌트
    AppCompatButton plusImage, submit, plusOrder, plusIngredient, back;
    EditText title, description;
    TextView textView_description;
    Spinner category;
    RecyclerView ingredient;
    RecyclerView image;
    RecyclerView order;
    ScrollView mainScrollView;

    // 데이터
    ArrayList<UserRecipeImage> userRecipeImages;
    RecyclerViewAdapter<UserRecipeImage> imageRecyclerViewAdaptor;
    Uri imageUri;
    int posi; // 이미지 전용 포지션 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_write);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 컴포넌트 초기화
        title = findViewById(R.id.editText_recipe_write_title);
        category = findViewById(R.id.spinner_recipe_write_category);
        description = findViewById(R.id.editText_recipe_write_description);
        textView_description = findViewById(R.id.textView_recipe_write_description);
        order = findViewById(R.id.recyclerView_recipe_write_order);
        plusOrder = findViewById(R.id.appCompatButton_recipe_write_orderAddition);
        ingredient = findViewById(R.id.listView_recipe_write_ingredient);
        image = findViewById(R.id.listView_recipe_write_image);
        plusImage = findViewById(R.id.appCompatButton_recipe_write_imageAddition);
        submit = findViewById(R.id.appCompatButton_recipe_write_submit);
        plusIngredient = findViewById(R.id.appCompatButton_recipe_write_ingredientAddition);
        back = findViewById(R.id.button_recipe_write_01);
        mainScrollView = findViewById(R.id.scrollView_recipe_write_main);

        // Spinner 초기화
        String[] array = getResources().getStringArray(R.array.category);
        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), R.layout.spinner_category_item, array);
        adapter.setDropDownViewResource(R.layout.spinner_category_item);
        category.setAdapter(adapter);

        // 컴포넌트 비활성화
        description.setVisibility(View.GONE);
        textView_description.setVisibility(View.GONE);

        // 뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 레시피 신규 작성일 경우
        if(getIntent().getStringExtra("separator").equals("w")) {

            // --레시피 순서 관련--
            ArrayList<UserRecipeOrder> userRecipeArrayList = new ArrayList<>();

            RecyclerViewAdapter<UserRecipeOrder> userRecipeOrderRecyclerViewAdapter = new RecyclerViewAdapter<>(userRecipeArrayList, getApplicationContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            order.setLayoutManager(linearLayoutManager);
            order.setAdapter(userRecipeOrderRecyclerViewAdapter);

            // 순서 삭제
            userRecipeOrderRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (userRecipeArrayList.size() > 1) {
                        userRecipeArrayList.remove(position);

                        userRecipeOrderRecyclerViewAdapter.notifyItemRemoved(position);
                        userRecipeOrderRecyclerViewAdapter.notifyItemRangeChanged(position, userRecipeArrayList.size());
                    }
                }
            });

            // 순서 추가
            plusOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userRecipeArrayList.size() < 20) {
                        userRecipeArrayList.add(new UserRecipeOrder("", Integer.toString(userRecipeArrayList.size() + 1)));

//                        Log.e("userRecipeOrder Size", Integer.toString(userRecipeArrayList.size()));
                        userRecipeOrderRecyclerViewAdapter.notifyItemInserted(userRecipeArrayList.size());
//                        userRecipeOrderRecyclerViewAdapter.notifyItemChanged(userRecipeArrayList.size());

                        // 맨 하단 포커싱
                        mainScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                mainScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                        order.scrollToPosition(userRecipeOrderRecyclerViewAdapter.getItemCount() - 1);
                    }
                }
            });

            // 순서 아이템 내용 변경
            /*
            userRecipeOrderRecyclerViewAdapter.setOnTextChangeListener(new RecyclerViewAdapter.OnTextChangeListener() {
                @Override
                public void onTextChanged(int position, String text) {
                    userRecipeOrderRecyclerViewAdapter.notifyItemChanged(position);
                }
            });
             */
            // ------------

            // --레시피 재료--
            // 재료 설정
            ArrayList<UserRecipeIngredient> userRecipeIngredients = new ArrayList<>();

            RecyclerViewAdapter<UserRecipeIngredient> ingredientRecyclerViewAdaptor = new RecyclerViewAdapter<>(userRecipeIngredients, getApplicationContext());
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager((Context) this);
            linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            ingredient.setLayoutManager(linearLayoutManager1);
            ingredient.setAdapter(ingredientRecyclerViewAdaptor);

            // 재료 데이터 변경 시 이벤트
            ingredientRecyclerViewAdaptor.setOnTextChangeListener(new RecyclerViewAdapter.OnTextChangeListener() {
                @Override
                public void onTextChanged(int position, String text) {
//                    ingredientRecyclerViewAdaptor.notifyItemChanged(position);
                }
            });

            ingredientRecyclerViewAdaptor.setOnItemSelectedListener(new RecyclerViewAdapter.OnItemSelectedListener() {
                @Override
                public void onItemSelected(View view, int position) {
                    ingredientRecyclerViewAdaptor.notifyItemChanged(position);
                }
            });

            // 재료 추가 이벤트
            plusIngredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userRecipeIngredients.add(new UserRecipeIngredient("","", ""));

                    ingredientRecyclerViewAdaptor.notifyItemInserted(userRecipeIngredients.size() - 1);
                }
            });

            // 재료 삭제 이벤트
            ingredientRecyclerViewAdaptor.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(view.getId() == R.id.textView_recipe_write_ingredient_itemlist_03) {
                        if(userRecipeIngredients.size() > 1) {
                            userRecipeIngredients.remove(position);

                            ingredientRecyclerViewAdaptor.notifyItemRemoved(position);
                            ingredientRecyclerViewAdaptor.notifyItemRangeChanged(position, userRecipeIngredients.size());
                        }
                    }
                }
            });


            // ------------
            // --레시피 사진--

            // 사진 설정
            userRecipeImages = new ArrayList<>();

            imageRecyclerViewAdaptor = new RecyclerViewAdapter<>(userRecipeImages, getApplicationContext());
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager((Context) this);
            linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
            image.setLayoutManager(linearLayoutManager2);
            image.setAdapter(imageRecyclerViewAdaptor);

            // 사진 추가 버튼 이벤트 리스너
            plusImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userRecipeImages.size() < 20) {
                        userRecipeImages.add(new UserRecipeImage(""));

                        imageRecyclerViewAdaptor.notifyItemInserted(userRecipeImages.size() - 1);
                    }
                }
            });
            // 사진 삭제 버튼 이벤트 리스너
            imageRecyclerViewAdaptor.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // 삭제 버튼일 경우
                    if(view.getId() == R.id.floatingActionButton_delete) {
                        if(userRecipeImages.size() > 1) {
                            userRecipeImages.remove(position);

                            imageRecyclerViewAdaptor.notifyItemRemoved(position);
                            imageRecyclerViewAdaptor.notifyItemRangeChanged(position, userRecipeImages.size());
                        }
                    }
                    // 이미지 클릭일 경우
                    if(view.getId() == R.id.imageView_recipe_write_addition) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityResult.launch(intent);
                        posi = position;
                    }
                }
            });
            // ------------
        } else if(getIntent().getStringExtra("separator").equals("u")) { // 레시피 수정일 경우

        }
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if ( result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    //iv_upload_image.setImageBitmap(bitmap);		//이미지를 띄울 이미지뷰 설정
                    userRecipeImages.set(posi, new UserRecipeImage(imageUri.toString()));

                    imageRecyclerViewAdaptor.notifyItemChanged(posi);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });
}