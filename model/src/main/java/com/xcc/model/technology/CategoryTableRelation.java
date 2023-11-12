package com.xcc.model.technology;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 文件类型-数据表关系
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryTableRelation extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
/*
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;
*/

    /**
     * 文件类型id
     */
    private Integer categoryId;

    /**
     * 表格的id
     */
    private Integer tableId;

    /**
     * 创建时间
     */
/*    private LocalDateTime createTime;

    *//**
     * 更新时间
     *//*
    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
