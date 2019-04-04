package com.yems.painter.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: ͼ�γ������ָ���״̬��¼�����ࣨ����ģʽ��
 * @date: 2015-3-12 ����10:18:06
 * @author: yems
 */
public class UndoState {
	/** ������ʷͼ�Σ����ڳ�������*/
	private List<byte[]> undoCaches;

	private UndoState() {
		undoCaches = new ArrayList<byte[]>();
	}

	/**
	 * @return
	 * @description: ��ȡ��ʷͼ�����ݼ���
	 * @date: 2015-3-16 ����2:08:23
	 * @author: yems
	 */
	public List<byte[]> getUndoCaches() {
		return undoCaches;
	}

	private static class Builder {
		private static UndoState instance = new UndoState();
	}

	public static UndoState getInstance() {
		return Builder.instance;
	}
}
