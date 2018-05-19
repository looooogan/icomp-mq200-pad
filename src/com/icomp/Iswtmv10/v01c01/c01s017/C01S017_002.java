package com.icomp.Iswtmv10.v01c01.c01s017;

/**
 * 提交回厂刀具信息的参数类
 * Created by FanLL on 2017/7/6.
 */

public class C01S017_002 {

    //刀具ID
    private String toolID;
    //材料号
    private String materialNum;
    //RFID载体ID
    private String rfidContainerID;
    //激光码（单品编码）
    private String laserCode;
    //刀具类型
    private String toolType;
    //实际回厂数量
    private String backCount;
    //出门单号
    private String outDoorNom;
    //用户ID
    private String customerID;

    public String getToolID() {
        return toolID;
    }

    public void setToolID(String toolID) {
        this.toolID = toolID;
    }

    public String getMaterialNum() {
        return materialNum;
    }

    public void setMaterialNum(String materialNum) {
        this.materialNum = materialNum;
    }

    public String getRfidContainerID() {
        return rfidContainerID;
    }

    public void setRfidContainerID(String rfidContainerID) {
        this.rfidContainerID = rfidContainerID;
    }

    public String getLaserCode() {
        return laserCode;
    }

    public void setLaserCode(String laserCode) {
        this.laserCode = laserCode;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public String getBackCount() {
        return backCount;
    }

    public void setBackCount(String backCount) {
        this.backCount = backCount;
    }

    public String getOutDoorNom() {
        return outDoorNom;
    }

    public void setOutDoorNom(String outDoorNom) {
        this.outDoorNom = outDoorNom;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

}
