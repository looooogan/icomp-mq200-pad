package com.icomp.Iswtmv10.v01c01.c01s023;

import java.util.List;

/**
 * 查询激光码信息的response参数类
 * Created by user on 2017/7/24.
 */

public class C01S023_002 {

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

        private int toolDurable;

        public int getToolDurable() {
            return toolDurable;
        }

        public void setToolDurable(int toolDurable) {
            this.toolDurable = toolDurable;
        }

    }

}
