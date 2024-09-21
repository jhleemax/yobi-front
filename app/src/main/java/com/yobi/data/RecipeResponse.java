package com.yobi.data;

import java.util.List;

public class RecipeResponse {
    private int totalPages;
    private int totalElements;
    private int size;
    private List<APIRecipe> content;  // 레시피 리스트가 content 필드에 있음

    // Getters and Setters
    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<APIRecipe> getContent() {
        return content;
    }

    public void setContent(List<APIRecipe> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "totalPages=" + totalPages +
                ", totalElements=" + totalElements +
                ", size=" + size +
                ", content=" + content.toString() +  // content 필드 리스트 출력
                '}';
    }

}
