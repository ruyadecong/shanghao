package com.xcc.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.omg.CORBA.INTERNAL;

import java.util.Date;
import java.util.List;

@Data
public class CallInspectVo {
    private List<Integer> idList;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String remark;
}
