package com.yems.painter.listener;

/**
 * @description: ����״̬������
 * @date: 2015-11-4 ����1:00:19
 * @author: yems
 */
public interface ConnectStateListener
{
	/**
	 * 
	 * @description:�ͷ���˽�������
	 * @date: 2015-11-4 ����1:00:56
	 * @author: yems
	 */
	public void channelConnected();

	/**
	 * 
	 * @description:�ͷ���˶Ͽ�����
	 * @date: 2015-11-4 ����1:00:58
	 * @author: yems
	 */
	public void channelClosed();
}
