package com.icomp.Iswtmv10.v01c01.c01s019;

/**
 * 查询刀具信息判断是否可以出厂修磨的参数类
 * Created by FanLL on 2017/7/7.
 */

public class C01S019_001 {

    private boolean success;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private String rfidContainerID;
        private String toolCode;
        private String toolType;
        private String toolID;
        private String grindingQuantity;
        private String code;

        public String getRfidContainerID() {
            return rfidContainerID;
        }

        public void setRfidContainerID(String rfidContainerID) {
            this.rfidContainerID = rfidContainerID;
        }

        public String getToolCode() {
            return toolCode;
        }

        public void setToolCode(String toolCode) {
            this.toolCode = toolCode;
        }

        public String getToolType() {
            return toolType;
        }

        public void setToolType(String toolType) {
            this.toolType = toolType;
        }

        public String getToolID() {
            return toolID;
        }

        public void setToolID(String toolID) {
            this.toolID = toolID;
        }

        public String getGrindingQuantity() {
            return grindingQuantity;
        }

        public void setGrindingQuantity(String grindingQuantity) {
            this.grindingQuantity = grindingQuantity;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

    }

}
