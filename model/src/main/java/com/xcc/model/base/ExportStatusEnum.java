package com.xcc.model.base;

import lombok.Getter;

@Getter
public enum ExportStatusEnum {
    /**
     * 产品是否仅供出口（0：有内销；1：仅出口；2：不确定）
     */
    NOT_ONLY_EXPORT(0,"有内销"),
    ONLY_EXPORT(1,"仅出口"),
    NOT_SURE(2,"不确定")
    ;
    private Integer onlyExport;
    private String exportStatue;

    private ExportStatusEnum(Integer onlyExport, String exportStatue){
        this.onlyExport = onlyExport;
        this.exportStatue = exportStatue;
    }

    private Integer onlyExport(){
        return this.onlyExport;
    }

    private String exportStatue(){
        return this.exportStatue;
    }

    public static String getExportStatus(Integer onlyExport){
        ExportStatusEnum[] exportStatuses = values();
        for (ExportStatusEnum exportStatus : exportStatuses) {
            if(exportStatus.onlyExport().equals(onlyExport)){
                return exportStatus.exportStatue();
            }
        }
        return null;
    }
}