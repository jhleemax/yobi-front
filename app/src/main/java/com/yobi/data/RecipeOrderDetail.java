package com.yobi.data;

public class RecipeOrderDetail {
    String
            img,
            mainDescription;

    public RecipeOrderDetail(String img, String mainDescription) {
        this.img = img;
        this.mainDescription = mainDescription;
    }

    public String getImg() {
        return img;
    }

    public String getMainDescription() {
        return mainDescription;
    }
}
