package com.xcc.model.vo;

import com.xcc.model.temp.MaterialBasedataTemp;
import com.xcc.model.temp.MaterialIngredientTemp;
import lombok.Data;

import java.util.List;

@Data
public class MaterialTempVo {
    private MaterialBasedataTemp materialData;
    private List<MaterialIngredientTemp> ingredientList;
}
