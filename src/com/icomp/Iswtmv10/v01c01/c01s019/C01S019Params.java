package com.icomp.Iswtmv10.v01c01.c01s019;

import java.io.Serializable;

/**
 * 厂外修磨参数类
 * Created by FanLL on 2017/7/2.
 */

public class C01S019Params implements Serializable {

    //材料号
    public String materialNumber;

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    //修磨数量
    public String grindingQuantity;

    public String getGrindingQuantity() {
        return grindingQuantity;
    }

    public void setGrindingQuantity(String grindingQuantity) {
        this.grindingQuantity = grindingQuantity;
    }

}
