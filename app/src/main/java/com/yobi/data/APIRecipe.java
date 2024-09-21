package com.yobi.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class APIRecipe implements Serializable {


    private String title;
    private String userId;
    private String recipeId;
    private String category;
    private String ingredient;
    private String manuals;
    private String recipeThumbnail;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getManuals() {
        return manuals;
    }

    public void setManuals(String manuals) {
        this.manuals = manuals;
    }

    public String getRecipeThumbnail() {
        return recipeThumbnail;
    }


    public ArrayList<RecipeOrderDetail> getRecipeOrderDetails() {
        ArrayList<RecipeOrderDetail> recipeOrderDetails = new ArrayList<>();

//        if(this.manual01 != null && !(this.manual01.isEmpty()))
//            recipeOrderDetails.add(new RecipeOrderDetail(this.manual_IMG01, this.manual01));

        return recipeOrderDetails;
    }
}
