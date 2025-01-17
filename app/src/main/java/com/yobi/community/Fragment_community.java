package com.yobi.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yobi.R;
import com.yobi.data.Community;
import com.yobi.recipe.Activity_recipe_write;
import com.yobi.retrofit.RetrofitAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_community#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_community extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // API
    Retrofit retrofit;

    // 컴포넌트
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_community() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_community.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_community newInstance(String param1, String param2) {
        Fragment_community fragment = new Fragment_community();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community, container, false);

        // 초기화
        recyclerView = v.findViewById(R.id.recyclerView_community_01);
        floatingActionButton = v.findViewById(R.id.floatingActionButton_community);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        retrofitAPI.getCommunityByTitle("").enqueue(new Callback<List<Community>>() {
            @Override
            public void onResponse(Call<List<Community>> call, Response<List<Community>> response) {
                Log.e("retrofit_onResponse", "Success");
                List<Community> communities = response.body();
                if(communities != null) {
                    for(Community c : communities) {
                        int i = 0;
                        Log.e("community" + i, communities.get(i).getTitle());
                        i++;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Community>> call, Throwable throwable) {
                Log.e("retrofit_onFailure", throwable.getMessage());
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Activity_recipe_write.class);
                intent.putExtra("type", "community");
                intent.putExtra("separator", "w");

                startActivity(intent);
            }
        });

        return v;
    }
}