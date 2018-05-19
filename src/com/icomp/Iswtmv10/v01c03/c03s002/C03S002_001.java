package com.icomp.Iswtmv10.v01c03.c03s002;

import java.io.Serializable;

/**
 * 按材料号查询库存信息参数类
 * Created by FanLL on 2017/7/11.
 */

public class C03S002_001 {

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

    public static class DataBean implements Serializable{

        private String toolCode;
        private String toolID;
        private String toolConsumeType;
        private String libraryCodeID;
        private String rfidString;

        public String getToolCode() {
            return toolCode;
        }

        public void setToolCode(String toolCode) {
            this.toolCode = toolCode;
        }

        public String getToolID() {
            return toolID;
        }

        public void setToolID(String toolID) {
            this.toolID = toolID;
        }

        public String getToolConsumeType() {
            return toolConsumeType;
        }

        public void setToolConsumeType(String toolConsumeType) {
            this.toolConsumeType = toolConsumeType;
        }

        public String getLibraryCodeID() {
            return libraryCodeID;
        }

        public void setLibraryCodeID(String libraryCodeID) {
            this.libraryCodeID = libraryCodeID;
        }

        public String getRfidString() {
            return rfidString;
        }

        public void setRfidString(String rfidString) {
            this.rfidString = rfidString;
        }

    }

}
