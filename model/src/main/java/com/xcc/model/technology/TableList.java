package com.xcc.model.technology;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数据库中表的汇总
 * </p>
 *
 * @author xcc
 * @since 2023-09-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableList extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 表在数据库中的名称
     */
    private String tableName;

    /**
     * 表的中文名称
     */
    private String tableMark;

    /**
     * 备注
     */
    private String remark;

    /**
     * 非表字段，用于从前端接收数据
     */
    @TableField(exist = false)
    private Integer categoryId;

    /**
     * 删除标记（0:可用 1:已删除）
     */
    /*private Integer isDeleted;*/


}
