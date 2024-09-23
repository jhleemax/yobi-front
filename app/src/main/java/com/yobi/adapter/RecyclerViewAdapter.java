package com.yobi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import java.util.List;

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

    // ViewHolder 정의
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeTitle;
        private TextView recipeGenre;
        private TextView recipeIngredient;
        private ImageView imageView;

        private TextView manualDescription;
        private ImageView manualImage;

        private final RecyclerViewAdapter<T> adapter;

        @SuppressLint("WrongViewCast")
        public ViewHolder(View view, OnItemClickListener listener, OnTextChangeListener textChangeListener, OnItemSelectedListener itemSelectedListener, RecyclerViewAdapter<T> adapter) {
            super(view);
            this.adapter = adapter;

            if (adapter.dataSet.isEmpty()) {
                Log.e("ViewHolder", "dataSet is Empty");
                return;
            }

            // 각 데이터 타입에 따라 다른 뷰 초기화
            if (adapter.dataSet.get(0) instanceof APIRecipe) {
                // API 레시피 관련 뷰
                recipeTitle = view.findViewById(R.id.textView_recipe_itemlist_01);
                recipeGenre = view.findViewById(R.id.textView_recipe_itemlist_02);
                recipeIngredient = view.findViewById(R.id.textView_recipe_itemlist_06);
                imageView = view.findViewById(R.id.imageView_recipe_itemlist);

                // API 레시피 클릭 이벤트 처리
                view.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        APIRecipe clickedRecipe = (APIRecipe) adapter.dataSet.get(position);
                        Intent intent = new Intent(view.getContext(), Activity_recipe_detail.class);
                        intent.putExtra("recipeId", clickedRecipe.getRecipeId());
                        view.getContext().startActivity(intent);
                    }
                });

            } else if (adapter.dataSet.get(0) instanceof APIRecipe.Manual) {
                // Manual 관련 뷰
                manualDescription = view.findViewById(R.id.textView_recipe_detail_order_itemlist_description);
                manualImage = view.findViewById(R.id.imageView_recipe_detail_order_itemlist);
            }
        }

        // 바인딩 메서드
        public void onBind(T item, int position) {
            if (item instanceof APIRecipe) {
                APIRecipe recipe = (APIRecipe) item;

                recipeTitle.setText(recipe.getTitle());
                recipeGenre.setText(recipe.getCategory());
                recipeIngredient.setText(recipe.getIngredient());

                // 이미지 로드
                if (recipe.getRecipeThumbnail() != null && !recipe.getRecipeThumbnail().isEmpty()) {
                    Glide.with(context)
                            .load(recipe.getRecipeThumbnail())
                            .into(imageView);
                }

            } else if (item instanceof APIRecipe.Manual) {
                APIRecipe.Manual manual = (APIRecipe.Manual) item;

                // Manual 설명 및 이미지 설정
                manualDescription.setText(manual.getDescription());
                if (manual.getImage() != null && !manual.getImage().isEmpty()) {
                    Glide.with(context)
                            .load(manual.getImage())
                            .into(manualImage);
                }
            }
        }
    }

    public RecyclerViewAdapter(ArrayList<T> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        // 데이터 타입에 따른 뷰 선택
        if (dataSet.get(0) instanceof APIRecipe) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_itemlist, parent, false);
        } else if (dataSet.get(0) instanceof APIRecipe.Manual) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_detail_order_itemlist, parent, false);
        }

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
