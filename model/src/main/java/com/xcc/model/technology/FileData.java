package com.xcc.model.technology;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 文件列表
 * </p>
 *
 * @author xcc
 * @since 2023-09-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileData extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 关联记录的id（如果没有指定关联记录，应该在后端指定特殊数值作为识别）
     */
    private Integer correlationId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原文件名
     */
    private String originalName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类目在file_category表中的id
     */
    private Integer categoryId;

    /**
     * 文件类目（比如附件14、营业执照）
     */
    private String fileCategory;

    /**
     * 文件类型（比如JPEG、PPT）
     */
    private String fileType;

    /**
     * 文件上传人
     */
    private String uploadUser;

    /**
     * 标记1
     */
    private String markOne;

    /**
     * 标记2
     */
    private String markTwo;

    /**
     * 标记3
     */
    private String markThree;

    /**
     * 标记4
     */
    private String markFour;

    /**
     * 标记5
     */
    private String markFive;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（1正常 0停用）
     */
    private Boolean status;

    /**
     * 非表字段，用于初始化表格行的可编辑状态
     */
    @TableField(exist = false)
    private boolean edit=false;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
