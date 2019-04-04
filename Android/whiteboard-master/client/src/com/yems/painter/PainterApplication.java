package com.yems.painter;

import android.app.Activity;
import android.app.Application;

import com.yems.painter.handler.CrashHandler;

/**
 * @description: ������ڳ�ʼ��
 * @date: 2015-3-12 ����3:23:22
 * @author: yems
 */
public class PainterApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		initCrashSetting();
	}

	/**
	 * 
	 * @description: ��ʼ���쳣��־��¼
	 * @date: 2015-3-12 ����4:00:59
	 * @author�� yems
	 */
	private void initCrashSetting() {
		CrashHandler.getInstance().init(getApplicationContext());
	}
}
