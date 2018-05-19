package com.icomp.Iswtmv10.v01c01.c01s008.modul;

import java.io.Serializable;

/**
 * Created by ${Nice} on 2017/7/20.
 */

public class C01S008Request  implements Serializable{
    private String synthesisParametersCodes;
    private String rfidContainerIDs;
    private String toolConsumetypes;

    public String getRfidString() {
        return rfidString;
    }

    public void setRfidString(String rfidString) {
        this.rfidString = rfidString;
    }

    private String rfidString;

    public String getSynthesisParametersCodes() {
        return synthesisParametersCodes;
    }

    public void setSynthesisParametersCodes(String synthesisParametersCodes) {
        this.synthesisParametersCodes = synthesisParametersCodes;
    }

    public String getRfidContainerIDs() {
        return rfidContainerIDs;
    }

    public void setRfidContainerIDs(String rfidContainerIDs) {
        this.rfidContainerIDs = rfidContainerIDs;
    }

    public String getToolConsumetypes() {
        return toolConsumetypes;
    }

    public void setToolConsumetypes(String toolConsumetypes) {
        this.toolConsumetypes = toolConsumetypes;
    }
}
