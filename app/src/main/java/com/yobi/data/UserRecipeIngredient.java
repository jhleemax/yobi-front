package com.yobi.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRecipeIngredient {
    String
            ingredient,
            count,
            unit;
}
