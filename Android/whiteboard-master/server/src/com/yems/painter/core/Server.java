package com.yems.painter.core;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.yems.painter.constant.Commons;
import com.yems.painter.factory.MyChannelPipelineFactory;

/**
 * Netty�����(����ģʽ)
 * 
 * @description:
 * @date: 2015-3-12 ����09:40:37
 * @author: yems
 */
public class Server {

	/**
	 * �̳߳ع���
	 */
	private ExecutorService mCachedThreadPool;

	private Server() {
		mCachedThreadPool = Executors.newCachedThreadPool();

		// Server����������
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(mCachedThreadPool,
						mCachedThreadPool));

		// ����һ������ͻ�����Ϣ�͸�����Ϣ�¼�����(Handler)
		bootstrap.setPipelineFactory(new MyChannelPipelineFactory());
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);

		// ����˰󶨵�ָ���˿�
		bootstrap.bind(new InetSocketAddress(Commons.PORT));
		System.out.println("Netty--����������������");
	}

	private static class Builder {
		private static Server instance = new Server();
	}

	public static Server getInstance() {
		return Builder.instance;
	}

	public static void main(String args[]) {
		Server.getInstance();
	}
}
