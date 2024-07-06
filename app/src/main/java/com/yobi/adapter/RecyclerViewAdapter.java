package com.yobi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.yobi.main.Activity_main;

import java.util.ArrayList;

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter<T>.ViewHolder> {

    private ArrayList<T> dataSet;
    private OnItemClickListener listener;
    private Context context;

    // 아이템 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView recipe_title;
        private TextView recipe_genre;
        //private final TextView recipe_amount;
        //private final TextView recipe_time;
        //private final TextView recipe_difficulty;
        private TextView recipe_ingredient;
        private ImageView imageView;
        private final RecyclerViewAdapter<T> adapter; // 상위 클래스 참조

        @SuppressLint("WrongViewCast")
        public ViewHolder(View view, OnItemClickListener listener, RecyclerViewAdapter<T> adapter) {
            super(view);
            this.adapter = adapter;

            if(adapter.dataSet.isEmpty()) {
                Log.e("ViewHolder", "dataSet is Empty");
                return;
            } else {
                if(adapter.dataSet.get(0) instanceof APIRecipe) { // 데이터셋의 타입이 APIRecipe 일 경우
                    // 뷰 초기화
                    recipe_title = view.findViewById(R.id.textView_recipe_itemlist_01);
                    recipe_genre = view.findViewById(R.id.textView_recipe_itemlist_02);
        //          recipe_amount = view.findViewById(R.id.textView_recipe_itemlist_03);
        //          recipe_time = view.findViewById(R.id.textView_recipe_itemlist_04);
        //          recipe_difficulty = view.findViewById(R.id.textView_recipe_itemlist_05);
                    recipe_ingredient = view.findViewById(R.id.textView_recipe_itemlist_06);
                    imageView = view.findViewById(R.id.imageView_recipe_itemlist);


                    // itemView에 클릭 리스너 부착
                    view.setOnClickListener(v -> {
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
        public void onBind(T item) {
            if(item instanceof APIRecipe) { // api 레시피일 경우
                recipe_title.setText(((APIRecipe) item).getRcpnm());
                recipe_genre.setText(((APIRecipe) item).getRcp_PAT2());
//                recipe_amount.setText(item.getAmount());
//                recipe_time.setText(item.getTime());
//                recipe_difficulty.setText(item.getDifficulty());
                recipe_ingredient.setText(((APIRecipe) item).getRcp_PARTS_DTLS());

                Glide.with(context)
                     .load(((APIRecipe) item).getAtt_FILE_NO_MAIN())
                     .into(imageView);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_itemlist, parent, false);
        return new ViewHolder(view, listener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
