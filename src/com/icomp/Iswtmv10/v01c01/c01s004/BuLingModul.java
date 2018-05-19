package com.icomp.Iswtmv10.v01c01.c01s004;

import java.io.Serializable;

/**
 * Created by ${Nice} on 2017/6/15.
 */

public class BuLingModul implements Serializable{
    private String name;
    private String time;
    private String id;
    private String replacementFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReplacementFlag() {
        return replacementFlag;
    }

    public void setReplacementFlag(String replacementFlag) {
        this.replacementFlag = replacementFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
