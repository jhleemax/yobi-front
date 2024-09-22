package com.yobi.retrofit;

import com.yobi.data.APIRecipe;
import com.yobi.data.Community;
import com.yobi.data.RecipeResponse;
import com.yobi.data.User;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @POST("/api/list") // (구서버) 레시피조회
    Call<List<APIRecipe>> getAPIRecipes();

    @POST("/user") // 회원가입
    Call<Integer> signUp(@Body User user);

    @GET("/user/{userId}/{socialType}") // 로그인
    Call<Integer> singIn(@Path("userId") String userId, @Path("socialType") String socialType);

    @GET("/board/title/{title}") // 게시판 제목으로 검색
    Call<List<Community>> getCommunityByTitle(@Path("title") String title);

    @POST("/board") // 게시판 작성
    Call<Integer> saveCommunity(@Body Community community);

    // 네이버 로그인 프로필 정보 가져오기
    @GET("/v1/nid/me")
    Call<JSONObject> callNaverProfile(@HeaderMap Map<String, String> headers);

    // 레시피 전체검색
    @GET("/recipe/all/{page}/10")
    Call<RecipeResponse> getRecipeAll(
            @Path("page") int page
    );

    // 레시피 이름으로 검색
    @GET("/recipe/title/{title}/{page}/10")
    Call<RecipeResponse> getRecipeByTitle(
            @Path("title") String title,
            @Path("page") int page
    );

    // 레시피 상세정보 보기
    @GET("/recipe/{recipeId}")
    Call<APIRecipe> getRecipeDetails(
            @Path("recipeId") int recipeId
    );

    // 유저 정보 가져오기
    @GET("/user/{userId}")
    Call<User> getUserInfo(
            @Path("userId") String userId
    );
}
