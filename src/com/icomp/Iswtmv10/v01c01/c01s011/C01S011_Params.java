package com.icomp.Iswtmv10.v01c01.c01s011;


import com.icomp.wsdl.v01c01.c01s011.endpoint.EquipmentEntity;
import com.icomp.wsdl.v01c01.c01s011.endpoint.Equipments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备安上-参数
 * 
 * @ClassName: C01S011_Params 
 * @author WHY
 * @date 2016-3-2 上午10:39:10
 */
public class C01S011_Params implements Serializable {

	private static final long serialVersionUID = 8380970436676271795L;
	/**
	 * 设备id
	 */
	String equipmentID;
	/**
	 * 设备名称
	 */
	String equipmentName;
	/**
	 * 轴id
	 */
	String axleID;
	/**
	 * 轴编码
	 */
	String axleCode;
	/**
	 * 合成刀具id
	 */
	String synthesisParametersID;
	/**
	 * 合成刀编码
	 */
	String synthesisParametersCode;
	/**
	 * 授权人ID
	 */
	String gruantUserID;
	
	//请选择或扫描要安上的设备
	List<EquipmentEntity> equipmentEntity;
	List<EquipmentEntity> equList = new ArrayList<EquipmentEntity>();

	//请选择对应的轴号
	ArrayList<Equipments> lineList = new ArrayList<Equipments>();

	//合成刀具RFID
	String synthesisParametersRfid;

	public String getGruantUserID() {
		return gruantUserID;
	}

	public void setGruantUserID(String gruantUserID) {
		this.gruantUserID = gruantUserID;
	}
}
