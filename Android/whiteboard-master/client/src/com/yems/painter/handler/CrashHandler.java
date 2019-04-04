package com.yems.painter.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * @description: UncaughtException������,��������Uncaught�쳣��ʱ��,�ɸ������ӹܳ���,����¼���ʹ��󱨸�.
 * @date: 2015-3-12 ����3:19:43
 * @author: yems
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	// ϵͳĬ�ϵ�UncaughtException������
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandlerʵ��
	private static CrashHandler INSTANCE = new CrashHandler();
	// �����Context����
	private Context mContext;
	// �����洢�豸��Ϣ���쳣��Ϣ
	private Map<String, String> infos = new HashMap<String, String>();
	// ���ڸ�ʽ������,��Ϊ��־�ļ�����һ����
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// �����쳣��Ϣ������·��
	private String CRASH_COLLECT_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/Painter/crash/";

	/**
	 * ��ֻ֤��һ��CrashHandlerʵ��
	 */
	private CrashHandler() {

	}

	/**
	 * @description: ��̬�ڲ��࣬����ʵ����CrashHandler
	 * @date: 2015-3-12 ����3:26:16
	 * @author: yems
	 */
	private static class Builder {
		private static CrashHandler instance = new CrashHandler();
	}

	/**
	 * @return
	 * @description: ��ȡCrashHandlerʵ�� ,����ģʽ
	 * @date: 2015-3-12 ����3:25:09
	 * @author�� yems
	 */
	public static CrashHandler getInstance() {
		return Builder.instance;
	}

	/**
	 * @param context
	 * @description: ��ʼ��
	 * @date: 2015-3-12 ����3:26:56
	 * @author�� yems
	 */
	public void init(Context context) {
		mContext = context;
		// ��ȡϵͳĬ�ϵ�UncaughtException������
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * ��UncaughtException����ʱ��ת��ú���������
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// ����û�û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// �˳�����
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1); // 1(���߷�0����ʾ�������˳�����)
		}
	}

	/**
	 * @param ex
	 * @return ��������˸��쳣��Ϣ;���򷵻�false
	 * @description: �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����
	 * @date: 2015-3-12 ����3:39:55
	 * @author�� yems
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// �ռ��豸������Ϣ
		collectDeviceInfo(mContext);
		// ������־�ļ�
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * @param ctx
	 * @description: �ռ��豸������Ϣ
	 * @date: 2015-3-12 ����3:45:48
	 * @author�� yems
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "�ռ�����Ϣ�Ƿ����쳣", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "�ռ��쳣��Ϣʱ�������쳣", e);
			}
		}
	}

	/**
	 * @param ex
	 * @return
	 * @description: ���������Ϣ���ļ���
	 * @date: 2015-3-12 ����3:46:53
	 * @author�� yems
	 */
	private String saveCrashInfo2File(Throwable ex) {
		// long timestamp = System.currentTimeMillis();
		String time = formatter.format(new Date());

		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("time=" + time + "\n");

		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			String fileName = "Painter_Crash.log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File dir = new File(CRASH_COLLECT_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(CRASH_COLLECT_PATH
						+ fileName, true);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "д�ļ��Ƿ����쳣", e);
		}
		return null;
	}
}
