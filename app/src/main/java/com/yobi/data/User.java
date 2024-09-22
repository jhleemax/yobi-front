package com.yobi.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @SerializedName("userId")
    private String userId;

    @SerializedName("socialType")
    private String socialType;

    @SerializedName("passWord")
    private String passWord;

    @SerializedName("nickName")
    private String nickName;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    // API 응답 값을 저장할 필드 추가
    @SerializedName("followersCount")
    private Integer followersCount;

    @SerializedName("followingCount")
    private Integer followingCount;

    @SerializedName("userProfile")
    private String userProfile;

    @SerializedName("boardCount")
    private int boardCount;

    @SerializedName("recipeCount")
    private int recipeCount;

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", socialType='" + socialType + '\'' +
                ", passWord='" + passWord + '\'' +
                ", nickName='" + nickName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", followersCount=" + followersCount +
                ", followingCount=" + followingCount +
                ", userProfile='" + userProfile + '\'' +
                ", boardCount=" + boardCount +
                ", recipeCount=" + recipeCount +
                '}';
    }
}
