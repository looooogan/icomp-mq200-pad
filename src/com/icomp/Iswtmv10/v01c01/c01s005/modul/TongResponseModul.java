package com.icomp.Iswtmv10.v01c01.c01s005.modul;

import java.util.List;

/**
 * Created by ${Nice} on 2017/7/13.
 */

public class TongResponseModul {

    /**
     * success : true
     * message :
     * data : [{"synthesisParametersCode":"T20001","synthesisCutterNumber":"","toolCode":"A5003","loadState":"","toolCount":18,"grindingsum":"","delFlag":"","updateUser":"","createUser":"","version":"","rFID":"1988c39c25c345e3a800ee7abfc1d97f"},{"synthesisParametersCode":"T20001","synthesisCutterNumber":"","toolCode":"C6057","loadState":"","toolCount":1,"grindingsum":"","delFlag":"","updateUser":"","createUser":"","version":"","rFID":"1988c39c25c345e3a800ee7abfc1d97f"}]
     */

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

    public static class DataBean {
        /**
         * synthesisParametersCode : T20001
         * synthesisCutterNumber :
         * toolCode : A5003
         * loadState :
         * toolCount : 18
         * grindingsum :
         * delFlag :
         * updateUser :
         * createUser :
         * version :
         * rFID : 1988c39c25c345e3a800ee7abfc1d97f
         */

        private String synthesisParametersCode;
        private String synthesisCutterNumber;
        private String toolCode;
        private String loadState;
        private int toolCount;
        private String grindingsum;
        private String delFlag;
        private String updateUser;
        private String createUser;
        private String version;
        private String rFID;

        public String getSynthesisParametersCode() {
            return synthesisParametersCode;
        }

        public void setSynthesisParametersCode(String synthesisParametersCode) {
            this.synthesisParametersCode = synthesisParametersCode;
        }

        public String getSynthesisCutterNumber() {
            return synthesisCutterNumber;
        }

        public void setSynthesisCutterNumber(String synthesisCutterNumber) {
            this.synthesisCutterNumber = synthesisCutterNumber;
        }

        public String getToolCode() {
            return toolCode;
        }

        public void setToolCode(String toolCode) {
            this.toolCode = toolCode;
        }

        public String getLoadState() {
            return loadState;
        }

        public void setLoadState(String loadState) {
            this.loadState = loadState;
        }

        public int getToolCount() {
            return toolCount;
        }

        public void setToolCount(int toolCount) {
            this.toolCount = toolCount;
        }

        public String getGrindingsum() {
            return grindingsum;
        }

        public void setGrindingsum(String grindingsum) {
            this.grindingsum = grindingsum;
        }

        public String getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(String delFlag) {
            this.delFlag = delFlag;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getRFID() {
            return rFID;
        }

        public void setRFID(String rFID) {
            this.rFID = rFID;
        }
    }
}
