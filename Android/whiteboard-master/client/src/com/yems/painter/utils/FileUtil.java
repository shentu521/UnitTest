package com.yems.painter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @description: �ļ�����������
 * @date: 2015-3-16 ����9:43:13
 * @author: yems
 */
public class FileUtil {

	/**
	 * @param from
	 * @param to
	 * @return String
	 * @description: �����ļ���ָ����Ŀ¼��
	 * @date 2015-3-16 ����9:58:49
	 * @author: yems
	 */
	public static String copyFile(String from, String to) {
		try {
			File destFile = new File(to);

			if (destFile.exists()) {
				int suffix = 1;
				String fileName = getFileName(destFile.getName());
				String fileExt = getFileExtension(destFile.getName());
				String filePath = destFile.getParentFile().getAbsolutePath() + '/';

				String newFileName = filePath + fileName + '_' + suffix + '.'
						+ fileExt;
				while (new File(newFileName).exists()) {
					newFileName = filePath + fileName + '_' + suffix + '.'
							+ fileExt;
					suffix++;
				}
				to = newFileName;
			}

			FileInputStream original = new FileInputStream(from);
			FileOutputStream destination = new FileOutputStream(to);

			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = original.read(buffer)) > 0) {
				destination.write(buffer, 0, bytesRead);
			}

			original.close();
			destination.flush();
			destination.close();
		} catch (Exception e) {
			return null;
		}
		return to;
	}

	/**
	 * @param filename
	 * @return String
	 * @description: ��ȡͼ���ļ�����չ��
	 * @date 2015-3-16 ����9:59:30
	 * @author: yems
	 */
	public static String getFileExtension(String filename) {
		int dotposition = filename.lastIndexOf('.');
		return filename.substring(dotposition + 1, filename.length());
	}

	/** Get the name of the image file */
	
	/** 
	 * @param filename
	 * @return String
	 * @description: ��ȡ�ļ���  
	 * @date 2015-3-16 ����10:00:45    
	 * @author: yems 
	 */
	public static String getFileName(String filename) {
		int dotposition = filename.lastIndexOf('.');
		return filename.substring(0, dotposition);
	}
}