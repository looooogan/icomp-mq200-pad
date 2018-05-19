package com.icomp.Iswtmv10.v01c01.c01s010.modul;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ${Nice} on 2017/7/17.
 */

public class HuanZhuangResponse implements Serializable{

    /**
     * success : true
     * message :
     * data : [{"synthesisParametersID":"","cutterType":"","synthesisCutterNumber":"0","toolCode":"C6060","tempToolCode":"","toolCount":1,"toolConsumetype":"其他"},{"synthesisParametersID":"","cutterType":"","synthesisCutterNumber":"1","toolCode":"E3039","tempToolCode":"","toolCount":1,"toolConsumetype":"可刃磨钻头"}]
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
         * synthesisParametersID :
         * cutterType :
         * synthesisCutterNumber : 0
         * toolCode : C6060
         * tempToolCode :
         * toolCount : 1
         * toolConsumetype : 其他
         */

        private String synthesisParametersID;
        private String cutterType;
        private String synthesisCutterNumber;
        private String toolCode;
        private String tempToolCode;
        private int toolCount;
        private String toolConsumetype;

        public String getToolType() {
            return toolType;
        }

        public void setToolType(String toolType) {
            this.toolType = toolType;
        }

        private String toolType;

        public String getSynthesisParametersID() {
            return synthesisParametersID;
        }

        public void setSynthesisParametersID(String synthesisParametersID) {
            this.synthesisParametersID = synthesisParametersID;
        }

        public String getCutterType() {
            return cutterType;
        }

        public void setCutterType(String cutterType) {
            this.cutterType = cutterType;
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

        public String getTempToolCode() {
            return tempToolCode;
        }

        public void setTempToolCode(String tempToolCode) {
            this.tempToolCode = tempToolCode;
        }

        public int getToolCount() {
            return toolCount;
        }

        public void setToolCount(int toolCount) {
            this.toolCount = toolCount;
        }

        public String getToolConsumetype() {
            return toolConsumetype;
        }

        public void setToolConsumetype(String toolConsumetype) {
            this.toolConsumetype = toolConsumetype;
        }
    }
}
