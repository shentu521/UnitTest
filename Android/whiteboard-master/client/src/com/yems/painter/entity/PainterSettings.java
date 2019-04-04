package com.yems.painter.entity;


import android.content.pm.ActivityInfo;

/**
 * @description: ����Ĭ������
 * @date: 2015-3-16 ����11:18:37
 * @author: yems
 */
public class PainterSettings {
	/** ���滭��Ԥ�����*/
	private BrushPreset mPreset = null;
	/** �����ϵ����һ��ͼ������*/
	private String mLastPicture = null;
	/** ��־�Ƿ�ǿ�ƴ�ͼ���ļ�,true �ǣ�ֻ�򿪣������棩,false �񣨴򿪵�ͬʱ����ͼƬ��*/
	private boolean mForceOpenFile = false;
	/** ��ǰ�豸����Ļ����*/
	private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	
	public BrushPreset getPreset() {
		return mPreset;
	}
	public void setPreset(BrushPreset preset) {
		mPreset = preset;
	}
	public String getLastPicture() {
		return mLastPicture;
	}
	public void setLastPicture(String lastPicture) {
		mLastPicture = lastPicture;
	}
	public boolean isForceOpenFile() {
		return mForceOpenFile;
	}
	public void setForceOpenFile(boolean forceOpenFile) {
		mForceOpenFile = forceOpenFile;
	}
	public int getOrientation() {
		return mOrientation;
	}
	public void setOrientation(int orientation) {
		mOrientation = orientation;
	}
}