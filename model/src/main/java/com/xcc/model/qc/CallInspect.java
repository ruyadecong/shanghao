package com.xcc.model.qc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xcc.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 原料报检信息表
 * </p>
 *
 * @author xcc
 * @since 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CallInspect extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

/*      @TableId(value = "id", type = IdType.AUTO)
    private Long id;*/

    /**
     * 采购表中的id
     */
    private Integer purchaseId;

    /**
     * 报检序列号
     */
    private String serialNumber;

    /**
     * 报检日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inspectDate;

    /**
     * 是否打印
     */
    private Integer isPrint;

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
