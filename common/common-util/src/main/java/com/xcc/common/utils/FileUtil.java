package com.xcc.common.utils;



import com.xcc.model.technology.FileData;


public class FileUtil {

    //根据文件数据返回fileName + fileCategory
    public static String getFileNameCategory(FileData fileData){

        String filePath = fileData.getFilePath();
        String fileCategory;
        String fileName = fileData.getFileName();
        if(fileData.getFileCategory() == null || fileData.getFileCategory() == "") {
            fileCategory = "无";
        }else{
            fileCategory = fileData.getFileCategory();
        }
        return fileName + " - " + fileCategory;
    }

    //根据文件数据返回文件后缀名（不含 . ）
    public static String getFileSuffix(FileData fileData){
        String fileSuffix = "后缀名缺失";
        //通过最后一个 . 获取文件后缀名
        int dotIndex = fileData.getFilePath().lastIndexOf('.');
        if(dotIndex >= 0){
            fileSuffix = fileData.getFilePath().substring(dotIndex + 1);
        }
        return fileSuffix;
    }


    //将word文件转换为pdf

}
