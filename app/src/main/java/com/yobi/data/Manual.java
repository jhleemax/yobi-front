package com.yobi.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Manual implements Serializable {

    private int manualId;
    private int stepNumber;
    private String description;

    @SerializedName("image")
    private String imageUrl;

    public int getManualId() {
        return manualId;
    }

    public void setManualId(int manualId) {
        this.manualId = manualId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
