package com.yems.painter.serializable;

import java.io.Serializable;

/**
 * @description: �������л�
 * @date: 2015-3-13 ����4:57:23
 * @author: yems
 */
public class SerializablePaint implements Serializable {
	/** ������ɫ */
	private int painterColor;
	/** ���ʴ�С */
	private float painterSize;

	public SerializablePaint() {
	}

	public SerializablePaint(int color, int size) {
		this.painterColor = color;
		this.painterSize = size;
	}

	public int getColor() {
		return painterColor;
	}

	public void setColor(int color) {
		this.painterColor = color;
	}

	public float getSize() {
		return painterSize;
	}

	public void setSize(float size) {
		this.painterSize = size;
	}
}
