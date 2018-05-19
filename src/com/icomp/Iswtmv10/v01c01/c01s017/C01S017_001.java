package com.icomp.Iswtmv10.v01c01.c01s017;

import java.io.Serializable;
import java.util.List;

/**
 * 取得回厂刀具信息的参数类
 * Created by FanLL on 2017/7/6.
 */

public class C01S017_001 implements Serializable {

    private boolean success;
    private String message;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {

        private String outsideFactoryID;
        private String orderNum;
        private String merchantsID;
        private String toolID;
        private String materialNum;
        private String numberGrinding;
        private String grindingType;
        private String laserCode;
        private String toolType;
        private String manufactureDate;
        private String approver;
        private String repairState;
        private String note;
        private String toolName;
        private String receiveNumber;
        private String updateUser;
        private String delFlag;
        private String expandSpace4;
        private String createUser;
        private String rfidContainerID;
        private String laserIdentificationCode;
        private String outDoorNom;
        private String surplusNumber;
        private String trueNum;

        public String getTrueNum() {
            return trueNum;
        }

        public void setTrueNum(String trueNum) {
            this.trueNum = trueNum;
        }

        public String getOutsideFactoryID() {
            return outsideFactoryID;
        }

        public void setOutsideFactoryID(String outsideFactoryID) {
            this.outsideFactoryID = outsideFactoryID;
        }

        public String getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(String orderNum) {
            this.orderNum = orderNum;
        }

        public String getMerchantsID() {
            return merchantsID;
        }

        public void setMerchantsID(String merchantsID) {
            this.merchantsID = merchantsID;
        }

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

        public String getNumberGrinding() {
            return numberGrinding;
        }

        public void setNumberGrinding(String numberGrinding) {
            this.numberGrinding = numberGrinding;
        }

        public String getGrindingType() {
            return grindingType;
        }

        public void setGrindingType(String grindingType) {
            this.grindingType = grindingType;
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

        public String getManufactureDate() {
            return manufactureDate;
        }

        public void setManufactureDate(String manufactureDate) {
            this.manufactureDate = manufactureDate;
        }

        public String getApprover() {
            return approver;
        }

        public void setApprover(String approver) {
            this.approver = approver;
        }

        public String getRepairState() {
            return repairState;
        }

        public void setRepairState(String repairState) {
            this.repairState = repairState;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getToolName() {
            return toolName;
        }

        public void setToolName(String toolName) {
            this.toolName = toolName;
        }

        public String getReceiveNumber() {
            return receiveNumber;
        }

        public void setReceiveNumber(String receiveNumber) {
            this.receiveNumber = receiveNumber;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(String delFlag) {
            this.delFlag = delFlag;
        }

        public String getExpandSpace4() {
            return expandSpace4;
        }

        public void setExpandSpace4(String expandSpace4) {
            this.expandSpace4 = expandSpace4;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getRfidContainerID() {
            return rfidContainerID;
        }

        public void setRfidContainerID(String rfidContainerID) {
            this.rfidContainerID = rfidContainerID;
        }

        public String getLaserIdentificationCode() {
            return laserIdentificationCode;
        }

        public void setLaserIdentificationCode(String laserIdentificationCode) {
            this.laserIdentificationCode = laserIdentificationCode;
        }

        public String getOutDoorNom() {
            return outDoorNom;
        }

        public void setOutDoorNom(String outDoorNom) {
            this.outDoorNom = outDoorNom;
        }

        public String getSurplusNumber() {
            return surplusNumber;
        }

        public void setSurplusNumber(String surplusNumber) {
            this.surplusNumber = surplusNumber;
        }

    }

}
