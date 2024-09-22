package com.yobi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yobi.R;
import com.yobi.data.APIRecipe;
import com.yobi.data.Image;
import com.yobi.data.RecipeOrderDetail;
import com.yobi.data.UserRecipeIngredient;
import com.yobi.data.UserRecipeOrder;
import com.yobi.recipe.Activity_recipe_detail;

import java.util.ArrayList;

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter<T>.ViewHolder> {

    private ArrayList<T> dataSet;
    private OnItemClickListener listener;
    private OnTextChangeListener textChangeListener;
    private OnItemSelectedListener itemSelectedListener;
    private Context context;

    // 아이템 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // EditText change 이벤트 리스너
    public interface OnTextChangeListener {
        void onTextChanged(int position, String text);
    }

    // Spinner 아이템 클릭 이벤트 리스너
    public interface OnItemSelectedListener {
        void onItemSelected(View view, int position);
    }

    // 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnTextChangeListener(OnTextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // API 레시피 조회용
        private TextView recipe_title;
        private TextView recipe_genre;
        private TextView recipe_ingredient;
        private ImageView imageView;

        // 다른 사용자 정의 항목들
        private final RecyclerViewAdapter<T> adapter;

        @SuppressLint("WrongViewCast")
        public ViewHolder(View view, OnItemClickListener listener, OnTextChangeListener textChangeListener, OnItemSelectedListener itemSelectedListener, RecyclerViewAdapter<T> adapter) {
            super(view);
            this.adapter = adapter;

            if (adapter.dataSet.isEmpty()) {
                Log.e("ViewHolder", "dataSet is Empty");
                return;
            } else {
                if (adapter.dataSet.get(0) instanceof APIRecipe) {
                    // API 레시피에 대한 뷰 초기화
                    recipe_title = view.findViewById(R.id.textView_recipe_itemlist_01);
                    recipe_genre = view.findViewById(R.id.textView_recipe_itemlist_02);
                    recipe_ingredient = view.findViewById(R.id.textView_recipe_itemlist_06);
                    imageView = view.findViewById(R.id.imageView_recipe_itemlist);

                    // 아이템 클릭 리스너 설정
                    view.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            APIRecipe clickedRecipe = (APIRecipe) adapter.dataSet.get(position);

                            // Intent 생성 및 데이터 전달
                            Intent intent = new Intent(view.getContext(), Activity_recipe_detail.class);
                            intent.putExtra("recipeId", clickedRecipe.getRecipeId());  // recipeId 전달

                            // Activity 전환
                            view.getContext().startActivity(intent);
                        }
                    });

                } else if (adapter.dataSet.get(0) instanceof RecipeOrderDetail) {
                    // 레시피 순서 관련 뷰 초기화
                    // 비슷한 방식으로 설정 가능
                } else if (adapter.dataSet.get(0) instanceof UserRecipeOrder) {
                    // 사용자 레시피 순서 관련 뷰 초기화
                    // 비슷한 방식으로 설정 가능
                } else if (adapter.dataSet.get(0) instanceof UserRecipeIngredient) {
                    // 레시피 재료 관련 뷰 초기화
                    // 비슷한 방식으로 설정 가능
                } else if (adapter.dataSet.get(0) instanceof Image) {
                    // 이미지 관련 뷰 초기화
                    // 비슷한 방식으로 설정 가능
                }
            }
        }

        public void onBind(T item, int position) {
            if (item instanceof APIRecipe) {
                APIRecipe recipe = (APIRecipe) item;

                // 레시피 이름 설정
                recipe_title.setText(recipe.getTitle());

                // 레시피 카테고리 설정
                recipe_genre.setText(recipe.getCategory());

                // 재료 설정
                recipe_ingredient.setText(recipe.getIngredient());

                // 이미지 로드 (Glide 사용)
                if (recipe.getRecipeThumbnail() != null && !recipe.getRecipeThumbnail().isEmpty()) {
                    Glide.with(context)
                            .load(recipe.getRecipeThumbnail())
                            .into(imageView);
                }

            } else if (item instanceof RecipeOrderDetail) {
                // 레시피 순서 바인딩
                RecipeOrderDetail orderDetail = (RecipeOrderDetail) item;
                // 비슷한 방식으로 바인딩
            } else if (item instanceof UserRecipeOrder) {
                // 사용자 레시피 순서 바인딩
                // 비슷한 방식으로 바인딩
            } else if (item instanceof UserRecipeIngredient) {
                // 사용자 레시피 재료 바인딩
                // 비슷한 방식으로 바인딩
            }
        }
    }

    public RecyclerViewAdapter(ArrayList<T> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (dataSet.get(0) instanceof APIRecipe)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_itemlist, parent, false);
        else if (dataSet.get(0) instanceof RecipeOrderDetail)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_detail_order_itemlist, parent, false);
        else if (dataSet.get(0) instanceof UserRecipeOrder)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_write_order_itemlist, parent, false);
        else if (dataSet.get(0) instanceof UserRecipeIngredient)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_write_ingredient_itemlist, parent, false);
        else if (dataSet.get(0) instanceof Image)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_write_image_itemlist, parent, false);

        return new ViewHolder(view, listener, textChangeListener, itemSelectedListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(dataSet.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
