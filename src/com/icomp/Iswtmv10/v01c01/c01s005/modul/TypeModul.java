package com.icomp.Iswtmv10.v01c01.c01s005.modul;

import java.util.List;

/**
 * Created by ${Nice} on 2017/7/24.
 */

public class TypeModul {

    /**
     * success : true
     * message :
     * data : [{"scrapState":"0","scrapStateName":"断刀"},{"scrapState":"1","scrapStateName":"丢刀"},{"scrapState":"2","scrapStateName":"到寿"},{"scrapState":"3","scrapStateName":"出库报废"},{"scrapState":"9","scrapStateName":"其他"}]
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
         * scrapState : 0
         * scrapStateName : 断刀
         */

        private String scrapState;
        private String scrapStateName;

        public String getScrapState() {
            return scrapState;
        }

        public void setScrapState(String scrapState) {
            this.scrapState = scrapState;
        }

        public String getScrapStateName() {
            return scrapStateName;
        }

        public void setScrapStateName(String scrapStateName) {
            this.scrapStateName = scrapStateName;
        }
    }
}
