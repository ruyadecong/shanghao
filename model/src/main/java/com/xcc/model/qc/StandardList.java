package com.xcc.model.qc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 标准列表
 * </p>
 *
 * @author xcc
 * @since 2023-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StandardList extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 标准类型
     */
    private String standardClass;

    /**
     * 标准名称
     */
    private String standardName;

    /**
     * 标准编码
     */
    private String standardNumber;

    /**
     * 标准年份
     */
    private String standardYear;

    /**
     * 标准文本的id
     */
    private Long fileId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 状态（1正常 0停用）
     */
    private Boolean status;

    /**
     * 非表属性
     * 由标准类型和标准名称拼接而成
     */
    @TableField(exist = false)
    private String name;

/*    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    *//**
     * 删除标记（0:可用 1:已删除）
     *//*
    private Integer isDeleted;*/


}
