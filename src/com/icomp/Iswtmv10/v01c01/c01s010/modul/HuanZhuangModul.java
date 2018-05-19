package com.icomp.Iswtmv10.v01c01.c01s010.modul;

/**
 * Created by ${Nice} on 2017/6/20.
 */

public class HuanZhuangModul {
    private String caiLiaoHao;
    private String type;
    private int num;
    private int huanzhuangNum = 0;
    private int diudaoNum = 0;

    public String getCaiLiaoHao() {
        return caiLiaoHao;
    }

    public void setCaiLiaoHao(String caiLiaoHao) {
        this.caiLiaoHao = caiLiaoHao;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getHuanzhuangNum() {
        return huanzhuangNum;
    }

    public void setHuanzhuangNum(int huanzhuangNum) {
        this.huanzhuangNum = huanzhuangNum;
    }

    public int getDiudaoNum() {
        return diudaoNum;
    }

    public void setDiudaoNum(int diudaoNum) {
        this.diudaoNum = diudaoNum;
    }
}
