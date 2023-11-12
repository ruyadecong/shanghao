package com.xcc.model.base;

import lombok.Getter;

@Getter
public enum FormulaModelEnum {

    PRODUCT_FORMULA(0,"产品配方"),

    SEMI_FORMULA(1,"中间料配方")

    ;

    private Integer classMark;

    private String classModel;

    private Integer classMark(){return this.classMark;}

    private String classModel(){return this.classModel;}

    private FormulaModelEnum(Integer classMark,String classModel){
        this.classMark = classMark;
        this.classModel = classModel;
    }

    public static String getClassModel(Integer classMark){

        FormulaModelEnum[] formulaModelEnums = values();
        for (FormulaModelEnum formulaModelEnum : formulaModelEnums) {
            if(formulaModelEnum.classMark().equals(classMark)){
                return formulaModelEnum.classModel();
            }
        }
        return null;
    }
}
