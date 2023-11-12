package com.xcc.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

//已经报检的数据的展示

@Data
public class CallInspectData {
    //报检序列号
    private String serialNumber;
    //报检的内容是否打印，根据serialNumber查询得到，如果都是0，则设置为否；如果都是1，则设置为是；如果不都是0或1，则设置为异常
    private String isPrint;
    //根据serialNumber查到的记录条数
    private Integer inspectNum;
}
