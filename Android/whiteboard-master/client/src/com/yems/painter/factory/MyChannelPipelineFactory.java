package com.yems.painter.factory;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.yems.painter.control.PainterCanvasControl;
import com.yems.painter.handler.MyClientHandler;
import com.yems.painter.listener.ConnectStateListener;

/**
 * @description: �Զ���ܵ��߹����࣬���ڹ���ͨ����ĸ���handler
 * @date: 2015-3-16 ����1:57:49
 * @author: yems
 */
public class MyChannelPipelineFactory implements ChannelPipelineFactory
{
	/** ������������ҵ��Ŀͻ��˶��� */
	private MyClientHandler myClientHandler;

	public MyChannelPipelineFactory()
	{
		myClientHandler = new MyClientHandler();
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();
		// ���������
		pipeline.addLast("objectencoder", new ObjectEncoder());
		// ���������
		pipeline.addLast("objectdecoder", new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
		// ��������ҵ����
		pipeline.addLast("clientHandler", myClientHandler);
		return pipeline;
	}

	/**
	 * @param painterCanvas
	 * @description: ���û�������
	 * @date: 2015-3-16 ����2:00:22
	 * @author: yems
	 */
	public void setPainterCanvas(PainterCanvasControl painterCanvas)
	{
		myClientHandler.setPainterCanvas(painterCanvas);
	}

	public void setConnectListener(ConnectStateListener onnectStateListener)
	{
		myClientHandler.setConnectListener(onnectStateListener);
	}
}
