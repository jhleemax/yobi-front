package com.yobi.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Community implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("category")
    private String category;
    @SerializedName("userId")
    private String userId;
    @SerializedName("boardThumbnail")
    private String boardThumbnail;
}
