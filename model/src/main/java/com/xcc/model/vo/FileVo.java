package com.xcc.model.vo;

import lombok.Data;

@Data
public class FileVo {
    private String fileName;
    private String fileCategory;
    private String originalName;
    private String tableName;           //用于从前端接收提取数据的表名，这个可以决定返回的文件类型列表以及可查看的文件类型
    private String uploadUser;
    private String orderField;


}
