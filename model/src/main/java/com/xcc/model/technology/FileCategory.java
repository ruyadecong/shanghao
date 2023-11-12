package com.xcc.model.technology;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 文件类别汇总表及与数据库表的关系
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileCategory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 文件类别名称
     */
    private String categoryName;

    /**
     * 这类文件存放的文件夹
     */
    private String categoryPath;

    /**
     * 备注
     */
    private String remark;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
