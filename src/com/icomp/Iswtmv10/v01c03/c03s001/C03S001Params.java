package com.icomp.Iswtmv10.v01c03.c03s001;

import com.icomp.wsdl.v01c03.c03s001.endpoint.SynthesisEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 合成刀具初始化参数类
 * Created by FanLL on 2017/6/27.
 */

public class C03S001Params implements Serializable {

    //合成刀具编码
    public String synthesisParametersCode;

    //筒刀编码
    public String tubeKnifeCode;

    //合成刀具组成列表
    public List<SynthesisEntity> list;

    //组成类型
    public String createType;

    public List<SynthesisEntity> getList() {
        return list;
    }

    public void setList(List<SynthesisEntity> list) {
        this.list = list;
    }

}
