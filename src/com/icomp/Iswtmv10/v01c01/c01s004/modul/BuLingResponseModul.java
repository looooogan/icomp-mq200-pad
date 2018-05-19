package com.icomp.Iswtmv10.v01c01.c01s004.modul;

import java.util.List;

/**
 * Created by ${Nice} on 2017/7/5.
 */

public class BuLingResponseModul {

    /**
     * success : true
     * message :
     * data : [{"replacementID":"10000000000027","toolCode":"E3026","appliedNumber":0,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":10,"toolID":"9e0021a2-7467-49fa-8906-c15b41529174","libraryCodeID":"09-01-02","toolType":"0","knifelnventoryNumber":"32","rfidCode":"E2004134721302631170A33F","delFlag":"","updateUser":""},{"replacementID":"10000000000029","toolCode":"E3031","appliedNumber":0,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":10,"toolID":"9e0021a2-7467-49fa-8906-c15b41529179","libraryCodeID":"09-01-02","toolType":"0","knifelnventoryNumber":"43","rfidCode":"E2004134721300850570DD77","delFlag":"","updateUser":""},{"replacementID":"10000000000030","toolCode":"E3032","appliedNumber":0,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":10,"toolID":"9e0021a2-7467-49fa-8906-c15b41529180","libraryCodeID":"09-02-03","toolType":"0","knifelnventoryNumber":"23","rfidCode":"E2004134721601310230F692","delFlag":"","updateUser":""},{"replacementID":"10000000000031","toolCode":"E3033","appliedNumber":0,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":10,"toolID":"9e0021a2-7467-49fa-8906-c15b41529181","libraryCodeID":"09-02-02","toolType":"0","knifelnventoryNumber":"40","rfidCode":"E2004134721300991160A429","delFlag":"","updateUser":""},{"replacementID":"10000000000032","toolCode":"E3039","appliedNumber":0,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":10,"toolID":"9e0021a2-7467-49fa-8906-c15b41529187","libraryCodeID":"08-02-02","toolType":"0","knifelnventoryNumber":"40","rfidCode":"E2003098911900560570DB7D","delFlag":"","updateUser":""},{"replacementID":"10000000000025","toolCode":"E3044","appliedNumber":0,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":10,"toolID":"9e0021a2-7467-49fa-8906-c15b41529192","libraryCodeID":"07-01-03","toolType":"0","knifelnventoryNumber":"92","rfidCode":"E20041347215002520603E60","delFlag":"","updateUser":""},{"replacementID":"10000000000028","toolCode":"G6006","appliedNumber":0,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":30,"toolID":"9e0021a2-7467-49fa-8906-c15b41529288","libraryCodeID":"09-01-01","toolType":"2","knifelnventoryNumber":"30","rfidCode":"E20041347213022412509A35","delFlag":"","updateUser":""},{"replacementID":"10000000000033","toolCode":"G6010","appliedNumber":5,"applyUser":"","applyTime":"","replacementReason":"","receiveUser":"","receiveTime":"","processingStatus":"","departmentID":"","replacementFlag":"","applyID":"","appliedTotalNumber":30,"toolID":"9e0021a2-7467-49fa-8906-c15b41529292","libraryCodeID":"08-02-03","toolType":"2","knifelnventoryNumber":"50","rfidCode":"E20041347217014026700C3A","delFlag":"","updateUser":""}]
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
         * replacementID : 10000000000027
         * toolCode : E3026
         * appliedNumber : 0
         * applyUser :
         * applyTime :
         * replacementReason :
         * receiveUser :
         * receiveTime :
         * processingStatus :
         * departmentID :
         * replacementFlag :
         * applyID :
         * appliedTotalNumber : 10
         * toolID : 9e0021a2-7467-49fa-8906-c15b41529174
         * libraryCodeID : 09-01-02
         * toolType : 0
         * knifelnventoryNumber : 32
         * rfidCode : E2004134721302631170A33F
         * delFlag :
         * updateUser :
         */

        private String replacementID;
        private String toolCode;
        private int appliedNumber;
        private String applyUser;
        private String applyTime;
        private String replacementReason;
        private String receiveUser;
        private String receiveTime;
        private String processingStatus;
        private String departmentID;
        private String replacementFlag;
        private String applyID;
        private int appliedTotalNumber;
        private String toolID;
        private String libraryCodeID;
        private String toolType;
        private String knifelnventoryNumber;
        private String rfidCode;
        private String delFlag;
        private String updateUser;

        public String getReplacementID() {
            return replacementID;
        }

        public void setReplacementID(String replacementID) {
            this.replacementID = replacementID;
        }

        public String getToolCode() {
            return toolCode;
        }

        public void setToolCode(String toolCode) {
            this.toolCode = toolCode;
        }

        public int getAppliedNumber() {
            return appliedNumber;
        }

        public void setAppliedNumber(int appliedNumber) {
            this.appliedNumber = appliedNumber;
        }

        public String getApplyUser() {
            return applyUser;
        }

        public void setApplyUser(String applyUser) {
            this.applyUser = applyUser;
        }

        public String getApplyTime() {
            return applyTime;
        }

        public void setApplyTime(String applyTime) {
            this.applyTime = applyTime;
        }

        public String getReplacementReason() {
            return replacementReason;
        }

        public void setReplacementReason(String replacementReason) {
            this.replacementReason = replacementReason;
        }

        public String getReceiveUser() {
            return receiveUser;
        }

        public void setReceiveUser(String receiveUser) {
            this.receiveUser = receiveUser;
        }

        public String getReceiveTime() {
            return receiveTime;
        }

        public void setReceiveTime(String receiveTime) {
            this.receiveTime = receiveTime;
        }

        public String getProcessingStatus() {
            return processingStatus;
        }

        public void setProcessingStatus(String processingStatus) {
            this.processingStatus = processingStatus;
        }

        public String getDepartmentID() {
            return departmentID;
        }

        public void setDepartmentID(String departmentID) {
            this.departmentID = departmentID;
        }

        public String getReplacementFlag() {
            return replacementFlag;
        }

        public void setReplacementFlag(String replacementFlag) {
            this.replacementFlag = replacementFlag;
        }

        public String getApplyID() {
            return applyID;
        }

        public void setApplyID(String applyID) {
            this.applyID = applyID;
        }

        public int getAppliedTotalNumber() {
            return appliedTotalNumber;
        }

        public void setAppliedTotalNumber(int appliedTotalNumber) {
            this.appliedTotalNumber = appliedTotalNumber;
        }

        public String getToolID() {
            return toolID;
        }

        public void setToolID(String toolID) {
            this.toolID = toolID;
        }

        public String getLibraryCodeID() {
            return libraryCodeID;
        }

        public void setLibraryCodeID(String libraryCodeID) {
            this.libraryCodeID = libraryCodeID;
        }

        public String getToolType() {
            return toolType;
        }

        public void setToolType(String toolType) {
            this.toolType = toolType;
        }

        public String getKnifelnventoryNumber() {
            return knifelnventoryNumber;
        }

        public void setKnifelnventoryNumber(String knifelnventoryNumber) {
            this.knifelnventoryNumber = knifelnventoryNumber;
        }

        public String getRfidCode() {
            return rfidCode;
        }

        public void setRfidCode(String rfidCode) {
            this.rfidCode = rfidCode;
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
    }
}
