package com.yobi.retrofit;

import com.yobi.data.APIRecipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("/api/list")
    Call<List<APIRecipe>> getAPIRecipes();
}
