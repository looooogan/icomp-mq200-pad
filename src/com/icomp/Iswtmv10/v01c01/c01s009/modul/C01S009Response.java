package com.icomp.Iswtmv10.v01c01.c01s009.modul;

import java.util.List;

/**
 * Created by ${Nice} on 2017/7/20.
 */

public class C01S009Response {

    /**
     * success : true
     * message :
     * data : {"synthesisParametersCode":"t02016","rfidContainerID":"cbdb7a8d01f94d889e0b747793437528","createType":"3","toolList":[{"toolCode":"C6060","toolConsumetype":"其他","toolCount":1,"toolID":"9e0021a2-7467-49fa-8906-c15b41528903","toolConsumetypeID":"9","toolGrinding":"0"},{"toolCode":"E3039","toolConsumetype":"可刃磨钻头","toolCount":1,"toolID":"9e0021a2-7467-49fa-8906-c15b41529187","toolConsumetypeID":"0","toolGrinding":"0"}]}
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
         * synthesisParametersCode : t02016
         * rfidContainerID : cbdb7a8d01f94d889e0b747793437528
         * createType : 3
         * toolList : [{"toolCode":"C6060","toolConsumetype":"其他","toolCount":1,"toolID":"9e0021a2-7467-49fa-8906-c15b41528903","toolConsumetypeID":"9","toolGrinding":"0"},{"toolCode":"E3039","toolConsumetype":"可刃磨钻头","toolCount":1,"toolID":"9e0021a2-7467-49fa-8906-c15b41529187","toolConsumetypeID":"0","toolGrinding":"0"}]
         */

        private String synthesisParametersCode;
        private String rfidContainerID;
        private String createType;
        private List<ToolListBean> toolList;

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

        public List<ToolListBean> getToolList() {
            return toolList;
        }

        public void setToolList(List<ToolListBean> toolList) {
            this.toolList = toolList;
        }

        public static class ToolListBean {
            /**
             * toolCode : C6060
             * toolConsumetype : 其他
             * toolCount : 1
             * toolID : 9e0021a2-7467-49fa-8906-c15b41528903
             * toolConsumetypeID : 9
             * toolGrinding : 0
             */

            private String toolCode;
            private String toolConsumetype;
            private String toolCount;
            private String toolID;
            private String toolConsumetypeID;
            private String toolGrinding;
            private String grindingsum;

            public String getGrindingsum() {
                return grindingsum;
            }

            public void setGrindingsum(String grindingsum) {
                this.grindingsum = grindingsum;
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

            public String getToolCount() {
                return toolCount;
            }

            public void setToolCount(String toolCount) {
                this.toolCount = toolCount;
            }

            public String getToolID() {
                return toolID;
            }

            public void setToolID(String toolID) {
                this.toolID = toolID;
            }

            public String getToolConsumetypeID() {
                return toolConsumetypeID;
            }

            public void setToolConsumetypeID(String toolConsumetypeID) {
                this.toolConsumetypeID = toolConsumetypeID;
            }

            public String getToolGrinding() {
                return toolGrinding;
            }

            public void setToolGrinding(String toolGrinding) {
                this.toolGrinding = toolGrinding;
            }
        }
    }
}
