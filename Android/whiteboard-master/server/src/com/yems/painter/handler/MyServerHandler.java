package com.yems.painter.handler;

import java.net.InetSocketAddress;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.yems.painter.constant.Commons;
import com.yems.painter.core.MetadataRepositories;
import com.yems.painter.serializable.SerializablePath;

/**
 * @ClassName: MyHandler
 * @Description: �Զ���ͨ�������������ڴ���ͨ������Ϣ���շ���
 * @author lwtx-yems
 * @date Mar 6, 2015 10:40:51 AM
 * 
 */
public class MyServerHandler extends SimpleChannelHandler {

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel channel = e.getChannel();
		Commons.currentClientIP = ((InetSocketAddress) e.getChannel()
				.getRemoteAddress()).getAddress().getHostAddress();
		// MetadataRepositories.getInstance().getChannels().put(
		// Commons.currentClientIP, channel);
		System.out.println("�ͻ�: " + Commons.currentClientIP + " ���룡");
		sendAllShapesToClient(channel);
	}

	/**
	 * �����¿ͻ��˽���ʱ����ÿͻ��˷�������ͼ�ζ���
	 * 
	 * @param channel
	 */
	private void sendAllShapesToClient(Channel channel) {
		if (MetadataRepositories.getInstance().getBufferShapes().size() != 0) {
			System.out.println("��������ͼ�����ݵ��ͻ���");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			channel.write(MetadataRepositories.getInstance().getBufferShapes());
		}
	}

	/**
	 * ������ͼ�����ݵ��ǵ�ǰ�ͻ���
	 * 
	 * @param newPath
	 */
	private void sendNewShapeToClient(SerializablePath newPath) {
		System.out.println("��������ͼ�����ݵ��ͻ���");
		Map<String, Channel> channels = MetadataRepositories.getInstance()
				.getChannels();

		for (String myUUID : channels.keySet()) {
			// ��ʼ��Ǳ��ͻ���д������
			if (!Commons.myUUID.equals(myUUID)) {
				System.out.println("����������ͻ� " + myUUID + "д������-----");
				Channel channel = channels.get(myUUID);

				System.out.println("connected--" + channel.isConnected()
						+ ",writable-" + channel.isWritable() + ",open--"
						+ channel.isOpen() + ",readable--"
						+ channel.isReadable());
				channel.write(newPath);
			}
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Channel channel = e.getChannel();
		SerializablePath newPath = (SerializablePath) e.getMessage();
		Commons.myUUID = newPath.getMyUUID();

		recordMyUUID(Commons.myUUID, channel);
		System.out.println("������յ���Ϣ,�豸��ʾ��--" + Commons.myUUID);
		if ("send_uuid".equals(newPath.getOPType()))
			return;

		if ("clear".equals(newPath.getOPType())) {
			if (MetadataRepositories.getInstance().getBufferShapes().size() != 0) {
				System.out.println("������������ͼ������");
				MetadataRepositories.getInstance().getBufferShapes().clear();
			}
			return;
		}

		MetadataRepositories.getInstance().getBufferShapes().add(newPath);
		// // ��Ǳ��͑���д����ͼ������
		sendNewShapeToClient(newPath);
	}

	/**
	 * ��¼��ǰ�豸Ψһ��ʾ����Channel�໥��Ӧ
	 */
	private void recordMyUUID(String myUUID, Channel channel) {

		if (MetadataRepositories.getInstance().getChannels()
				.containsKey(myUUID)
				&& MetadataRepositories.getInstance().getChannels().get(myUUID)
						.isOpen())
			return;

		MetadataRepositories.getInstance().getChannels().put(myUUID, channel);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		// String hostAddress = ((InetSocketAddress) e.getChannel()
		// .getRemoteAddress()).getAddress().getHostAddress();
		// MetadataRepositories.getInstance().getChannels().remove(hostAddress);

		// System.out.println("�ͻ�: " + hostAddress + " �˳���");
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		System.out.println("ͨ���ر�");
		// e.getChannel().disconnect();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		System.out.println("�����쳣--" + e.getCause().toString());
		e.getChannel().close();
	}
}
