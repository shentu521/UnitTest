package com.yems.painter.control;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import android.util.Log;

import com.yems.painter.common.Commons;
import com.yems.painter.entity.MetadataRepositories;
import com.yems.painter.factory.MyChannelPipelineFactory;
import com.yems.painter.factory.MyThreadFactory;
import com.yems.painter.listener.ConnectStateListener;
import com.yems.painter.serializable.SerializablePath;

/**
 * @description: ������Ӱװ�-�ͻ���(����ģʽ),�� ����C/Sģʽ�Ľ�����ͼ��
 * @date: 2015-3-12 ����9:03:17
 * @author: yems
 */
public class ClientControl implements Serializable
{
	private String TAG = "ClientNetty";
	/** �Զ���ܵ����� */
	private MyChannelPipelineFactory myChannelPipelineFactory;
	private static ClientControl instance;
	private ConnectStateListener mConnectStateListener;

	public static ClientControl getInstance()
	{
		if (instance == null)
		{
			instance = new ClientControl();
		}
		return instance;
	}

	/**
	 * 
	 * @description: �ͷſͻ��˶�����Դ
	 * @date: 2015-3-20 ����12:37:23
	 * @author: yems
	 */
	public void releaseInstance()
	{
		if (instance != null)
		{
			instance = null;
		}
	}

	private ClientControl()
	{
		myChannelPipelineFactory = new MyChannelPipelineFactory();
	}

	/**
	 * @return void
	 * @description: ���ӵ������
	 * @date 2015-3-16 ����10:08:27
	 * @author: yems
	 */
	public void connect()
	{
		MyThreadFactory.getInstance().getExecutorService().submit(new Runnable()
		{

			@Override
			public void run()
			{
				// Client����������
				ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
				// ����һ������������Ϣ�͸�����Ϣ�¼�����(Handler)
				bootstrap.setPipelineFactory(myChannelPipelineFactory);
				// ���ӵ�ָ��IP��ַ�ķ����
				bootstrap.setOption("key", "demokey");
				Channel channel = bootstrap.connect(new InetSocketAddress(Commons.SERVER_IP_ADDRESS, Commons.SERVER_PORT)).awaitUninterruptibly().getChannel();

				if (channel.isOpen())
				{
					Log.i(TAG, "ͨ���ɹ���");
					sendMyUUID2Server();
				} else
				{
					Log.i(TAG, "ͨ����ʧ��");
				}
			}
		});

	}

	/**
	 * @return void
	 * @description: ���͵�ǰ�豸Ψһ��ʾ���������
	 * @date 2015-3-16 ����10:08:44
	 * @author: yems
	 */
	private void sendMyUUID2Server()
	{
		SerializablePath path = new SerializablePath();
		path.setMyUUID(Commons.myUUID);
		path.setOPType("send_uuid");
		write(path);
	}

	/**
	 * @return void
	 * @description: �ϴ�ͼ�����ݵ������
	 * @date 2015-3-16 ����10:08:59
	 * @author: yems
	 */
	public void commitShapToServer()
	{
		List<SerializablePath> shapes = MetadataRepositories.getInstance().getBufferShapes();
		System.out.println("�ͻ��ˣ��������д������");
		if (shapes.size() != 0)
		{
			SerializablePath serializablePath = shapes.get(shapes.size() - 1);
			write(serializablePath);
			System.out.println("�ͻ��ˣ�д��������ϣ�");
		}

	}

	/**
	 * @return void
	 * @description: �����������е�ͼ������
	 * @date 2015-3-16 ����10:09:10
	 * @author: yems
	 */
	public void clearAllShapes()
	{
		SerializablePath path = new SerializablePath();
		path.setOPType("clear");
		path.setMyUUID(Commons.myUUID);
		write(path);
	}

	/**
	 * @return void
	 * @description: �����˷�������
	 * @date 2015-3-16 ����10:09:20
	 * @author: yems
	 */
	private void write(Object object)
	{
		if (Commons.currentChannel != null && Commons.currentChannel.isOpen())
		{
			Log.i(TAG, "��ʼ�����˷�������");
			Commons.currentChannel.write(object);
		}
	}

	/**
	 * @return void
	 * @description: ���û�������
	 * @date 2015-3-16 ����10:09:30
	 * @author: yems
	 */
	public void setPainterCanvas(PainterCanvasControl painterCanvas)
	{
		myChannelPipelineFactory.setPainterCanvas(painterCanvas);
	}

	public void setConnectListener(ConnectStateListener connectStateListener)
	{
		myChannelPipelineFactory.setConnectListener(connectStateListener);
	}
}
