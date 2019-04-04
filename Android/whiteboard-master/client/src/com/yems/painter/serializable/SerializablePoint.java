package com.yems.painter.serializable;

import java.io.Serializable;

/**
 * @description: ���·���������
 * @date: 2015-3-16 ����11:18:09
 * @author: yems
 */
public class SerializablePoint implements Serializable {
	/** x������ֵ*/
	private float x;
	/** y������ֵ*/
	private float y;

	public SerializablePoint(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public void setX(float paramFloat) {
		this.x = paramFloat;
	}

	public void setY(float paramFloat) {
		this.y = paramFloat;
	}

	public String toString() {
		return "SerializablePoint [x=" + this.x + ", y=" + this.y + "]";
	}
}