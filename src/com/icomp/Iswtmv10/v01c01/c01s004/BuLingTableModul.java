package com.icomp.Iswtmv10.v01c01.c01s004;

import java.io.Serializable;

/**
 * Created by ${Nice} on 2017/6/15.
 */

public class BuLingTableModul implements Serializable{
    private String caiLiao;
    private String huoWei;
    private String kuCun;
    private String KeLing;
    private String chuKu;

    public String getCaiLiao() {
        return caiLiao;
    }

    public void setCaiLiao(String caiLiao) {
        this.caiLiao = caiLiao;
    }

    public String getHuoWei() {
        return huoWei;
    }

    public void setHuoWei(String huoWei) {
        this.huoWei = huoWei;
    }

    public String getKuCun() {
        return kuCun;
    }

    public void setKuCun(String kuCun) {
        this.kuCun = kuCun;
    }

    public String getKeLing() {
        return KeLing;
    }

    public void setKeLing(String keLing) {
        KeLing = keLing;
    }

    public String getChuKu() {
        return chuKu;
    }

    public void setChuKu(String chuKu) {
        this.chuKu = chuKu;
    }
}
