package com.yobi.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class APIRecipe implements Serializable {

    private String title;
    private String userId;
    private String recipeId;
    private String category;
    private String ingredient;

    @SerializedName("recipeThumbnail")
    private String recipeThumbnail;

    @SerializedName("manuals")
    private List<Manual> manuals;

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

    public String getRecipeThumbnail() {
        return recipeThumbnail;
    }

    public List<Manual> getManuals() {
        return manuals;
    }

    public void setManuals(List<Manual> manuals) {
        this.manuals = manuals;
    }

    // 필요할 경우 getRecipeOrderDetails 메소드를 다시 작성
    public ArrayList<RecipeOrderDetail> getRecipeOrderDetails() {
        ArrayList<RecipeOrderDetail> recipeOrderDetails = new ArrayList<>();
        for (Manual manual : manuals) {
            recipeOrderDetails.add(new RecipeOrderDetail(manual.getImageUrl(), manual.getDescription()));
        }
        return recipeOrderDetails;
    }
}
