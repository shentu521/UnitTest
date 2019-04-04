package com.yems.painter.serializable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @description: ���������д�������л�·������
 * @date: 2015-3-16 ����11:08:23
 * @author: yems
 */
public class SerializablePath implements Serializable {
	/** ��ɸ�·���ĵ�*/
	public ArrayList<SerializablePoint> points = new ArrayList();
	/** ���ؿͻ��˲������ͣ�send-����ͼ�����ݡ�clear-���ͼ�����ݡ�send_uuid-�����豸Ψһ��ʾ����*/
	private String OPType = "send"; 
	/** ��ǰ�豸����Ļ���*/
	private float screenWidth; 
	/** ��ǰ�豸����Ļ�߶�*/
	private float screenHeight; 
	/** ��ǰ�豸��Ψһ��ʾ��*/
	private String myUUID; 
	/** ��ǰͼ�����õĻ�����ʽ*/
	private SerializablePaint serializablePaint; 
	/** */
	public String shapeType = "CURVE";
	
	public String getMyUUID() {
		return myUUID;
	}

	public void setMyUUID(String myUUID) {
		this.myUUID = myUUID;
	}

	public float getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(float screenWidth) {
		this.screenWidth = screenWidth;
	}

	public float getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(float screenHeight) {
		this.screenHeight = screenHeight;
	}

	public SerializablePaint getSerializablePaint() {
		return serializablePaint;
	}

	public void setSerializablePaint(SerializablePaint serializablePaint) {
		this.serializablePaint = serializablePaint;
	}

	public String getOPType() {
		return OPType;
	}

	public void setOPType(String oPType) {
		OPType = oPType;
	}
}