package com.yems.painter.handler;

import java.net.InetSocketAddress;
import java.util.List;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import android.util.Log;

import com.yems.painter.common.Commons;
import com.yems.painter.control.PainterCanvasControl;
import com.yems.painter.entity.MetadataRepositories;
import com.yems.painter.listener.ConnectStateListener;
import com.yems.painter.serializable.SerializablePath;
import com.yems.painter.serializable.ShapeRepositories;
import com.yems.painter.utils.Utils;

/**
 * @ClassName: ClientHandler
 * @Description: �Զ���ͻ��˴����������ڴ���ͨ������Ϣ���շ��ȣ�
 * @author yems
 * @date 2015-3-6 ����01:37:47
 * 
 */
public class MyClientHandler extends SimpleChannelHandler
{
	private String TAG = "ClientHandler";

	/** �Զ���Ļ������� */
	private PainterCanvasControl mPainterCanvas;
	/** Զ�̿ͻ��ˣ��ǵ�ǰ�ͻ��ˣ��������ĵ�ǰͼ������ */
	private SerializablePath currentRemotePath;
	private ConnectStateListener mConnectStateListener;

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
	{
		Log.i(TAG, "ͨ�����ӳɹ�");
		Commons.currentChannel = e.getChannel();
		if (mConnectStateListener != null)
		{
			mConnectStateListener.channelConnected();
		}
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		try
		{
			if (mPainterCanvas != null)
			{
				Object message = e.getMessage();
				String hostAddress = ((InetSocketAddress) e.getRemoteAddress()).getAddress().getHostAddress();
				System.out.println("�ͻ��˶��յ�" + hostAddress + "��������Ϣ");

				// ���շ���˷��͹���������ͼ�������б�
				if (message instanceof List)
				{
					List<SerializablePath> paths = (List<SerializablePath>) message;
					MetadataRepositories.getInstance().getBufferShapes().addAll(paths);
					mPainterCanvas.repaint(true);
					ShapeRepositories.getInstance().getUndoCaches().addAll(paths);

				} else if (message instanceof SerializablePath)
				{
					Commons.pickRemotePathFirst = false;
					// ���շ���˷��͹�����һ��������µ�ͼ������
					currentRemotePath = (SerializablePath) message;
					MetadataRepositories.getInstance().getBufferShapes().add(currentRemotePath);

					// ����Զ�̿ͻ��˷�������ʷͼ�����ݣ����ȱ��汾�ؿͻ��˵���ʷͼ������
					Utils.pickUndoCaches();
					Commons.lastRemotePath = currentRemotePath;
					mPainterCanvas.repaint(false);
				}
			}

		} catch (Exception e2)
		{
			Log.i(TAG, e2.toString());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		e.getChannel().close();
		Log.i(TAG, "�����쳣--" + e.getCause().toString());
	}

	/**
	 * @param painterCanvas
	 * @description: ���û�������
	 * @date: 2015-3-16 ����2:03:47
	 * @author: yems
	 */
	public void setPainterCanvas(PainterCanvasControl painterCanvas)
	{
		this.mPainterCanvas = painterCanvas;
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		Log.i(TAG, "����˶Ͽ�����");
		if (mConnectStateListener != null)
		{
			mConnectStateListener.channelClosed();
		}
	}

	public void setConnectListener(ConnectStateListener connectStateListener)
	{
		mConnectStateListener = connectStateListener;
	}
}
