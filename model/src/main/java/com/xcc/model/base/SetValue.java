package com.xcc.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 原料采购信息
 * </p>
 *
 * @author xcc
 * @since 2023-08-05
 */



//处理网页端用户选择的属性，比如每页记录数等，此功能暂未启用
@Data
@EqualsAndHashCode(callSuper = false)
public class SetValue extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 使用者
     */
    private String user;

    /**
     * 设置名称
     */
    private String setName;

    /**
     * 设置值
     */
    private String value;

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
