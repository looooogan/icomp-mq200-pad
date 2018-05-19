package com.icomp.Iswtmv10.v01c01.c01s009.modul;

/**
 * Created by ${Nice} on 2017/9/11.
 */

public class TongDaoZuZhuang {
    private String caiLiao;
    private String num;
    private String heChengdaoNum;
    private String rfidContainerIDs;
    private boolean isChange;

    public String getRfidContainerIDs() {
        return rfidContainerIDs;
    }

    public TongDaoZuZhuang() {
    }

    public TongDaoZuZhuang(String caiLiao, String num, String heChengdaoNum, String rfidContainerIDs, boolean isChange) {
        this.caiLiao = caiLiao;
        this.num = num;
        this.heChengdaoNum = heChengdaoNum;
        this.rfidContainerIDs = rfidContainerIDs;
        this.isChange = isChange;
    }

    public void setRfidContainerIDs(String rfidContainerIDs) {
        this.rfidContainerIDs = rfidContainerIDs;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public String getHeChengdaoNum() {
        return heChengdaoNum;
    }

    public void setHeChengdaoNum(String heChengdaoNum) {
        this.heChengdaoNum = heChengdaoNum;
    }

    public String getCaiLiao() {
        return caiLiao;
    }

    public void setCaiLiao(String caiLiao) {
        this.caiLiao = caiLiao;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public TongDaoZuZhuang clone() throws CloneNotSupportedException {
        TongDaoZuZhuang zuZhuang = new TongDaoZuZhuang(this.caiLiao,this.num,this.heChengdaoNum,this.rfidContainerIDs,this.isChange);
        return zuZhuang;
    }

    @Override
    public String toString() {
        return "TongDaoZuZhuang{" +
                "caiLiao='" + caiLiao + '\'' +
                ", num='" + num + '\'' +
                ", heChengdaoNum='" + heChengdaoNum + '\'' +
                ", rfidContainerIDs='" + rfidContainerIDs + '\'' +
                ", isChange=" + isChange +
                '}';
    }
}
