package com.xcc.model.qc;

import com.xcc.model.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Equipment extends BaseEntity {


    private String innerCode;

    private String plateCode;

    private String equipmentName;

    private String equipmentModel;

    private String equipmentUse;

    private String equipmentPlace;

    private String equipmentAdjust;

    private String equipmentOperator;

    private String equipmentLevel;

    private String equipmentStatus;

    private String equipmentManufacturer;

    private Date productDate;

    private Date purchaseDate;

    private String equipmentDescription;

    private String equipmentRemark;



}
