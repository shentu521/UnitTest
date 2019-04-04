package com.yems.painter.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yems.painter.common.Commons;

/**
 * @description: �Զ����̳߳ع�������(����ģʽ)
 * @date: 2015-3-16 ����10:33:26
 * @author: yems
 */
public class MyThreadFactory {

	/** �̳߳ع���ʵ��*/
	private ExecutorService executorService = Executors
			.newFixedThreadPool(Commons.THREAD_COUNT);

	public ExecutorService getExecutorService() {
		return executorService;
	}

	private MyThreadFactory() {

	}

	private static class Builder {
		private static MyThreadFactory instance = new MyThreadFactory();
	}

	public static MyThreadFactory getInstance() {
		return Builder.instance;
	}
}
