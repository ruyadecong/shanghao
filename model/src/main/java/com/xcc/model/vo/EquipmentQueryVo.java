package com.xcc.model.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class EquipmentQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String equipmentName;

    private String equipmentPlace;
}


