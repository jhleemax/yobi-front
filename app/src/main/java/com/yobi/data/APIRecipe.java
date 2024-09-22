package com.yobi.data;

import java.io.Serializable;
import java.util.List;

public class APIRecipe implements Serializable {
    private String userId;
    private int recipeId;
    private String title;
    private String category;
    private String ingredient;
    private Integer views;
    private Integer reportCount;
    private String createDate;
    private String updateDate;
    private String recipeThumbnail;
    private List<Manual> manuals;

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getRecipeThumbnail() {
        return recipeThumbnail;
    }

    public void setRecipeThumbnail(String recipeThumbnail) {
        this.recipeThumbnail = recipeThumbnail;
    }

    public List<Manual> getManuals() {
        return manuals;
    }

    public void setManuals(List<Manual> manuals) {
        this.manuals = manuals;
    }

    @Override
    public String toString() {
        return "APIRecipe{" +
                "userId='" + userId + '\'' +
                ", recipeId=" + recipeId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", ingredient='" + ingredient + '\'' +
                ", views=" + views +
                ", reportCount=" + reportCount +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", recipeThumbnail='" + recipeThumbnail + '\'' +
                ", manuals=" + manuals +
                '}';
    }

    // Manual class to represent the manuals array
    public static class Manual implements Serializable {
        private int manualId;
        private String contentType;
        private String contentId;
        private int stepNumber;
        private String description;
        private String image;

        // Getters and Setters
        public int getManualId() {
            return manualId;
        }

        public void setManualId(int manualId) {
            this.manualId = manualId;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public String toString() {
            return "Manual{" +
                    "manualId=" + manualId +
                    ", stepNumber=" + stepNumber +
                    ", description='" + description + '\'' +
                    ", image='" + image + '\'' +
                    '}';
        }
    }
}
