package com.yobi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yobi.R;
import com.yobi.data.APIRecipe;
import com.yobi.data.Image;
import com.yobi.data.RecipeOrderDetail;
import com.yobi.data.UserRecipeIngredient;
import com.yobi.data.UserRecipeOrder;

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

        // API 레시피 순서용
        private ImageView order_img;
        private TextView order_description;

        // 사용자 레시피 작성/수정용 (레시피 순서)
        private EditText order_content;
        private TextView step;
        private ImageView remove;

        // 사용자 레시피 작성/수정용 (레시피 재료)
        private EditText ingredient;
        private EditText countE;
        private TextView plus;
        private TextView minus;
        private TextView delete;
        private Spinner unit;

        // 사용자 레시피 작성/수정용 (레시피 사진)
        private ImageView image;
        private FloatingActionButton imageDelete;

        private final RecyclerViewAdapter<T> adapter; // 상위 클래스 참조

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
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(v, position);
                            }
                        }
                    });

                } else if (adapter.dataSet.get(0) instanceof RecipeOrderDetail) {
                    // 레시피 순서 관련 뷰 초기화
                    order_img = view.findViewById(R.id.imageView_recipe_detail_order_itemlist);
                    order_description = view.findViewById(R.id.textView_recipe_detail_order_itemlist_description);

                } else if (adapter.dataSet.get(0) instanceof UserRecipeOrder) {
                    // 사용자 레시피 순서 관련 뷰 초기화
                    order_content = view.findViewById(R.id.editText_recipe_write_order_itemlist);
                    step = view.findViewById(R.id.textView_recipe_write_order_itemlist);
                    remove = view.findViewById(R.id.imageView_recipe_write_order_itemlist_substract);

                    // - 버튼에 클릭 리스너 부착
                    remove.setOnClickListener(v -> {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(v, position);
                            }
                        }
                    });

                } else if (adapter.dataSet.get(0) instanceof UserRecipeIngredient) {
                    // 레시피 재료 관련 뷰 초기화
                    ingredient = view.findViewById(R.id.editText_recipe_write_ingredient_itemlist_01);
                    countE = view.findViewById(R.id.editText_recipe_write_ingredient_itemlist_02);
                    plus = view.findViewById(R.id.textView_recipe_write_ingredient_itemlist_01);
                    minus = view.findViewById(R.id.textView_recipe_write_ingredient_itemlist_02);
                    delete = view.findViewById(R.id.textView_recipe_write_ingredient_itemlist_03);
                    unit = view.findViewById(R.id.spinner_unit);

                    // 재료 수량 관리 버튼
                    plus.setOnClickListener(v -> countE.setText(Double.toString(Double.parseDouble(countE.getText().toString()) + 1.0)));
                    minus.setOnClickListener(v -> {
                        if (Double.parseDouble(countE.getText().toString()) - 1.0 >= 0)
                            countE.setText(Double.toString(Double.parseDouble(countE.getText().toString()) - 1.0));
                    });
                    delete.setOnClickListener(v -> {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(v, position);
                            }
                        }
                    });
                } else if (adapter.dataSet.get(0) instanceof Image) {
                    // 이미지 관련 뷰 초기화
                    image = view.findViewById(R.id.imageView_recipe_write_addition);
                    imageDelete = view.findViewById(R.id.floatingActionButton_delete);

                    // 이미지 클릭 리스너 설정
                    image.setOnClickListener(v -> {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(v, position);
                            }
                        }
                    });

                    // 삭제 버튼 클릭 리스너 설정
                    imageDelete.setOnClickListener(v -> {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(v, position);
                            }
                        }
                    });
                }
            }
        }

        public void onBind(T item, int position) {
            if (item instanceof APIRecipe) {
                // API 레시피 바인딩
                recipe_title.setText(((APIRecipe) item).getTitle());
                recipe_genre.setText(((APIRecipe) item).getCategory());
                recipe_ingredient.setText(((APIRecipe) item).getIngredient());

                Glide.with(context)
                        .load(((APIRecipe) item).getRecipeThumbnail())
                        .into(imageView);

            } else if (item instanceof RecipeOrderDetail) {
                // 레시피 순서 바인딩
                order_description.setText(((RecipeOrderDetail) item).getMainDescription());
                Glide.with(context)
                        .load(((RecipeOrderDetail) item).getImg())
                        .into(order_img);

            } else if (item instanceof UserRecipeOrder) {
                // 사용자 레시피 순서 바인딩
                order_content.setText(((UserRecipeOrder) item).getContents());
                step.setText("STEP " + ((UserRecipeOrder) item).getNum());

                order_content.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (textChangeListener != null) {
                            textChangeListener.onTextChanged(position, s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

            } else if (item instanceof UserRecipeIngredient) {
                // 사용자 레시피 재료 바인딩
                ingredient.setText(((UserRecipeIngredient) item).getIngredient());
                countE.setText(((UserRecipeIngredient) item).getCount());

                countE.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (textChangeListener != null) {
                            textChangeListener.onTextChanged(position, s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
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
