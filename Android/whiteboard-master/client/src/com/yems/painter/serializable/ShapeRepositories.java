package com.yems.painter.serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: ShapeRepositories
 * @Description: ���ڴ洢�����ϵ�ͼ���б�(����ģʽ)
 * @author lwtx-yems
 * @date 2015-2-28 ����04:21:06
 * 
 */
/**
 * @description: 
 * @date: 2015-3-17 ����8:56:53
 * @author: yems
 */
public class ShapeRepositories{

	/**
	 * �洢�����ϵ�ͼ���б�
	 */
	private List<SerializablePath> paths;
	private List<SerializablePath> undoCaches;
	
	public List<SerializablePath> getUndoCaches() {
		return undoCaches;
	}

	private ShapeRepositories(){
		paths = new ArrayList<SerializablePath>();
		undoCaches = new ArrayList<SerializablePath>();
	}

	public List<SerializablePath> getPath() {
		return paths;
	}

	public void addPath(SerializablePath path) {
		paths.add(path);
	}

	private static class Builder {
		public static ShapeRepositories instance = new ShapeRepositories();
	}

	public static ShapeRepositories getInstance() {
		return Builder.instance;
	}
}
