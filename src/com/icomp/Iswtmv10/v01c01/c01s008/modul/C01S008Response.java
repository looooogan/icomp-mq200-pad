package com.icomp.Iswtmv10.v01c01.c01s008.modul;

/**
 * Created by ${Nice} on 2017/7/20.
 */

public class C01S008Response {

    /**
     * success : true
     * message :
     * data : {"synthesisParametersCode":"T20001","rfidContainerID":"04bf019953c946a682ac15a7164cc899","createType":"5"}
     */

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
        /**
         * synthesisParametersCode : T20001
         * rfidContainerID : 04bf019953c946a682ac15a7164cc899
         * createType : 5
         */

        private String synthesisParametersCode;
        private String rfidContainerID;
        private String createType;
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSynthesisParametersCode() {
            return synthesisParametersCode;
        }

        public void setSynthesisParametersCode(String synthesisParametersCode) {
            this.synthesisParametersCode = synthesisParametersCode;
        }

        public String getRfidContainerID() {
            return rfidContainerID;
        }

        public void setRfidContainerID(String rfidContainerID) {
            this.rfidContainerID = rfidContainerID;
        }

        public String getCreateType() {
            return createType;
        }

        public void setCreateType(String createType) {
            this.createType = createType;
        }
    }
}
