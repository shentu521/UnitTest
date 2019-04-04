package com.yems.painter.entity;

import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Color;

import com.yems.painter.common.Commons;

/**
 * @description: ���ʲ���Ԥ��ֵ������
 * @date: 2015-3-13 ����5:01:37
 * @author: yems
 */
public class BrushPreset {
	/** ��ǰ������ʽ*/
	public float currentSize = 2; 
	/** ��ǰ������ɫ*/
	public int currentColor = Color.BLACK; 
	/** ��ǰ����ģ������*/
	public Blur currentBlurType = null;  
	/** ��ǰ����ģ���뾶*/
	public int currentBlurRadius = 0;  
	/** ��ǰ��������*/
	public int currentBrushType = Commons.CUSTOM; 

	/**
	 * @param type �������ͣ�Ǧ�ʡ��Զ��廭�ʣ�
	 * @param color ������ɫ
	 */
	public BrushPreset(int type, int color) {
		switch (type) {
		case Commons.PEN:
			set(Commons.COMMON_SIZE, color);
			break;
		case Commons.CUSTOM:
			setColor(color);
			break;
		}
		setType(type);
	}

	public BrushPreset(float size) {
		set(size);
	}

	public BrushPreset(float size, int color) {
		set(size, color);
	}

	public void set(float size) {
		setSize(size);
	}

	public void set(float size, int color) {
		setSize(size);
		setColor(color);
	}

	public void setColor(int color) {
		this.currentColor = color;
		Commons.currentColor = color;
	}

	public void setSize(float size) {
		currentSize = (size > 0) ? size : 1;
		Commons.currentSize = currentSize;
	}

	public void setType(int type) {
		this.currentBrushType = type;
	}
}