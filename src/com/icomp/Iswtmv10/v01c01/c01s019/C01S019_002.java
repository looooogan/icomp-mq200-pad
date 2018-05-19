package com.icomp.Iswtmv10.v01c01.c01s019;

import java.util.List;

/**
 * 查询厂外修复商家list的参数类
 * Created by FanLL on 2017/7/7.
 */

public class C01S019_002 {

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

        private String merchantsID;
        private String merchantsName;

        public String getMerchantsID() {
            return merchantsID;
        }

        public void setMerchantsID(String merchantsID) {
            this.merchantsID = merchantsID;
        }

        public String getMerchantsName() {
            return merchantsName;
        }

        public void setMerchantsName(String merchantsName) {
            this.merchantsName = merchantsName;
        }

    }

}
