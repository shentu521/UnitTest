/**
 * 
 */
package com.yems.painter.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

/**
 * @description: ������ƹ�����
 * @date: 2015��3��11�� ����5:20:13
 * @author: Li Yihua
 */
public class ReflectUtil {

	/** TreeMap */
	private static TreeMap<String, Method> mapMethod = new TreeMap<String, Method>();

	/**
	 * @param cls
	 * @param clsObj
	 * @param name
	 * @param classes
	 * @param objects
	 * @return Object
	 * @description: ���䷽������
	 * @date: 2015��3��11�� ����5:22:33
	 * @author�� Li Yihua
	 */
	public static Object invoke(Class<?> cls, Object clsObj, String name, Class<?>[] classes, Object[] objects) {
		Method m = null;
		try {
			String key = cls + "." + name;
			if (mapMethod.containsKey(key)) {
				m = mapMethod.get(key);
			} else {
				try {
					m = cls.getDeclaredMethod(name, classes);
				} catch (NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				mapMethod.put(key, m);
			}
			if (m != null) {
				return m.invoke(clsObj, objects);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @param clsObj
	 * @param name
	 * @param classes
	 * @param objects
	 * @return
	 * @description: ����
	 * @date: 2015��3��11�� ����5:22:18
	 * @author�� Li Yihua
	 */
	public static Object invoke(Object clsObj, String name, Class<?>[] classes, Object[] objects) {
		if (clsObj != null) {
			return invoke(clsObj.getClass(), clsObj, name, classes, objects);
		}
		return null;
	}
	
	/**
	 * @param obj ���� 
	 * @param methodName ������
	 * @param classes ��
	 * @param objs ���� 
	 * @return Object ���ض���
	 * @description: ������ö���ķ���
	 * @date: 2015��3��11�� ����5:20:58
	 * @author�� Li Yihua
	 */
	public static Object invokeMethod(Object obj, String methodName, Class<?>[] classes, Object[] objs){
		try {
			Method method = obj.getClass().getDeclaredMethod(methodName, classes);
			method.setAccessible(true);
			return method.invoke(obj, objs);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
