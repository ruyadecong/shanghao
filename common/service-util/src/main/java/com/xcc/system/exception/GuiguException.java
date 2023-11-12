package com.xcc.system.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//自定义异常类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuiguException extends RuntimeException{

    private Integer code;       //异常状态码
    private String msg;         //异常信息

}
