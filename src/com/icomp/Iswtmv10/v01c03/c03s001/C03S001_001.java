package com.icomp.Iswtmv10.v01c03.c03s001;

import java.util.List;

/**
 * 查询合成刀具组成信息的参数类
 * Created by FanLL on 2017/7/7.
 */

public class C03S001_001 {

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

        private String synthesisParametersCode;
        private String createType;
        private List<ToolListBean> toolList;

        public String getSynthesisParametersCode() {
            return synthesisParametersCode;
        }

        public void setSynthesisParametersCode(String synthesisParametersCode) {
            this.synthesisParametersCode = synthesisParametersCode;
        }

        public String getCreateType() {
            return createType;
        }

        public void setCreateType(String createType) {
            this.createType = createType;
        }

        public List<ToolListBean> getToolList() {
            return toolList;
        }

        public void setToolList(List<ToolListBean> toolList) {
            this.toolList = toolList;
        }

        public static class ToolListBean {

            private String toolCode;
            private String toolConsumetype;
            private int toolCount;
            //R&D
            private String replaceCode;

            public String getReplaceCode() {
                return replaceCode;
            }

            public void setReplaceCode(String replaceCode) {
                this.replaceCode = replaceCode;
            }

            public String getToolCode() {
                return toolCode;
            }

            public void setToolCode(String toolCode) {
                this.toolCode = toolCode;
            }

            public String getToolConsumetype() {
                return toolConsumetype;
            }

            public void setToolConsumetype(String toolConsumetype) {
                this.toolConsumetype = toolConsumetype;
            }

            public int getToolCount() {
                return toolCount;
            }

            public void setToolCount(int toolCount) {
                this.toolCount = toolCount;
            }

        }

    }

}
