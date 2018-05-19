package com.icomp.Iswtmv10.v01c01.c01s011;

import java.util.List;

/**
 * Created by Administrator on 2016/5/23 0023.
 */
public class C01s011Bean {
    private String equipmentName;
    private String equipmentID;
    private String rfidCode;
    private List<c01s011_002_02Entity> axleList;

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public List<c01s011_002_02Entity> getAxleList() {
        return axleList;
    }

    public void setAxleList(List<c01s011_002_02Entity> axleList) {
        this.axleList = axleList;
    }
}
