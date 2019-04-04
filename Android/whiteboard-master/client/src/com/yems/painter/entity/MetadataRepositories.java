package com.yems.painter.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jboss.netty.channel.Channel;

import com.yems.painter.serializable.SerializablePath;

/**
 * @ClassName: MetadataRepositories
 * @Description: ��ſͻ��˵������������ݣ�����ģʽ��
 * @author lwtx-yems
 * @date Mar 6, 2015 10:58:39 AM
 * 
 */
public class MetadataRepositories {
	
	/** ���ڷ������˱���ͼ���б�*/
	private  List<SerializablePath> bufferShapes;

	private MetadataRepositories() {
		bufferShapes = new ArrayList<SerializablePath>();
	}

	private static class Builder {
		private static MetadataRepositories instance = new MetadataRepositories();
	}
	
	public static MetadataRepositories getInstance(){
		return Builder.instance;
	}

	public  List<SerializablePath> getBufferShapes() {
		return bufferShapes;
	}

	public  void addShape(SerializablePath newShape) {
		bufferShapes.add(newShape);
	}
}
