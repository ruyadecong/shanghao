package com.xcc.model.base;

import lombok.Getter;

@Getter
public enum IsConfirmEnum {
    APPLY_FOR_MODIFICATION(-2,"申请修改，待批准"),

    AGREE_FOR_MODIFIED(-1,"同意修改，待修改"),

    MODIFICATION_COMPLETED(0,"修改/新增完成，待审核"),

    AVAILABLE(1,"审核完成，可使用"),

    HISTORY_DATA(10,"历史数据"),

    NEWLY_ADD(-10,"新增数据，待录入"),

    REJECT_APPLY(-100,"修改数据的申请被驳回"),

    REJECT_CONFIRM(-200,"修改完后的数据的审核被驳回")

    ;

    private Integer isConfirm;

    private String applyStatus;

    private Integer isConfirm(){
        return this.isConfirm;
    }

    private String applyStatus(){
        return this.applyStatus;
    }

    IsConfirmEnum(Integer isConfirm, String applyStatus) {
        this.isConfirm = isConfirm;
        this.applyStatus = applyStatus;
    }

    public static String getApplyStatus(Integer isConfirm){
        IsConfirmEnum[] isConfirmEnums = values();
        for (IsConfirmEnum isConfirmEnum : isConfirmEnums) {
            if(isConfirmEnum.isConfirm().equals(isConfirm)){
                return isConfirmEnum.applyStatus();
            }
        }
        return null;
    }

}

