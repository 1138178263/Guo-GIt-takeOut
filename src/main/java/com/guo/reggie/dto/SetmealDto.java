package com.guo.reggie.dto;

import com.guo.reggie.pojo.Setmeal;
import com.guo.reggie.pojo.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes = new ArrayList<>();

    private String categoryName;

    private String image;
}
