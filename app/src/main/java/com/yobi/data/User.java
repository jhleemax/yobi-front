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
}
