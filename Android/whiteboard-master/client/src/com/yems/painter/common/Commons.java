package com.yems.painter.common;

import org.jboss.netty.channel.Channel;

import com.yems.painter.control.PainterCanvasControl;
import com.yems.painter.serializable.SerializablePath;

/**
 * @description: ����������
 * @date: 2015-3-13 ����2:34:00
 * @author: yems
 */
public class Commons
{
	/** ������IP��ַ */
	public static String SERVER_IP_ADDRESS = "";
	/** �˿ں� */
	public static int SERVER_PORT = 8080;
	/** �̳߳ش�С��Ĭ��Ϊ10�� */
	public static final int THREAD_COUNT = 10;
	/** Ĭ�ϻ��ʴ�С */
	public static final float DEFAULT_BRUSH_SIZE = 2;
	/** ��ǰ�豸����Ļ�ܶ�ֵ */
	public static float CURRENT_DENSITY;
	/** ��ǰ�豸����Ļ��� */
	public static float CURRENT_SCREEN_WIDTH;
	/** ��ǰ�豸����Ļ�߶� */
	public static float CURRENT_SCREEN_HEIGHT;

	/** ��������:Ǧ�� */
	public static final int PENCIL = 1;
	/** ��������:ë�� */
	public static final int BRUSH = 2;
	/** ��������:��˱� */
	public static final int MARKER = 3;
	/** ��������:�ֱ� */
	public static final int PEN = 4;
	/** ��������:�Զ��廭�� */
	public static final int CUSTOM = 5;

	/** Ĭ�ϵĹ������ʴ�С */
	public static final float COMMON_SIZE = 2;
	/** ë�ʵ�Ĭ�ϴ�С */
	public static final float BRUSH_SIZE = 15;
	/** ��˱ʵ�Ĭ�ϴ�С */
	public static final float MARKER_SIZE = 20;
	/** Ǧ�ʵ�Ĭ��ģ���뾶 */
	public static final int PENCIL_BLUR_RADIUS = 10;
	/** ë�ʵ�Ĭ��ģ���뾶 */
	public static final int BRUSH_BLUR_RADIUS = 18;

	/** ��ǰ�豸������ɫ */
	public static int currentColor;
	/** ��ǰ���ʴ�С */
	public static float currentSize;
	/** ����ǰ�ͻ��˺ͷ��������ͨ�� */
	public static Channel currentChannel = null;
	/** ��ͼ����ǰ׺ */
	public static final String PICTURE_PREFIX = "picture_";
	/** ��ͼ���ƺ�׺ */
	public static final String PICTURE_EXT = ".png";
	/** �洢���õĲ��� */
	public static final String SETTINGS_STORAGE = "settings";
	/** ��ʾ�Ƿ�Ϊ�´�����ͼ�� */
	public static boolean mIsNewFile = true;
	/** MIME���ͱ�ʾͼƬ */
	public static final String PICTURE_MIME = "image/png";
	/** ���ļ��������ʶ */
	public static final int REQUEST_OPEN = 1;
	/** ��ʶ�豸�Ƿ�֧��Ӳ������ ��true ֧�֣� false ��֧�� */
	public static boolean mIsHardwareAccelerated = false;
	/** ֻ���ƴ�����Ӧ�õ�Ŀ¼�򿪵�ͼƬ */
	public static final int BACKUP_OPENED_ONLY_FROM_OTHER = 10;
	/** ���ǣ����ܸ�ͼƬ���Ա�Ӧ�û�������Ӧ�ã��Ѵ򿪵�ͼƬ���Ƶ���ǰӦ��ָ����Ŀ¼�� */
	public static final int BACKUP_OPENED_ALWAYS = 20;
	/** ֱ�Ӵ�ͼƬ���Ӳ����Ƹ�ͼƬ */
	public static final int BACKUP_OPENED_NEVER = 100;
	/** �˳�Ӧ��ǰ������ͼƬ */
	public static final int BEFORE_EXIT_SUBMIT = 10;
	/** ͼƬ�Ѿ��������ֱ���˳�Ӧ�� */
	public static final int BEFORE_EXIT_SAVED = 20;
	/** �˳�Ӧ��ǰ�������κα��涯�� */
	public static final int BEFORE_EXIT_NO_ACTION = 100;
	/** �������䵱�������ʳߴ�Ŀ�ݼ� */
	public static final int SHORTCUTS_VOLUME_BRUSH_SIZE = 10;
	/** �������䵱�����������Ŀ�ݼ� */
	public static final int SHORTCUTS_VOLUME_UNDO_REDO = 20;
	/** ����ͼƬ���˳�Ӧ�� */
	public static final int ACTION_SAVE_AND_EXIT = 1;
	/** ����ͼƬ�󷵻ػ������ */
	public static final int ACTION_SAVE_AND_RETURN = 2;
	/** ����ͼƬ��򿪷���ͼƬ�Ľ��� */
	public static final int ACTION_SAVE_AND_SHARE = 3;
	/** ��ת��Ļǰ���ȱ���ͼƬ */
	public static final int ACTION_SAVE_AND_ROTATE = 4;
	/** ����ͼƬ�󣬴򿪸�ͼƬ */
	public static final int ACTION_SAVE_AND_OPEN = 5;
	/** ��ǰ�豸��Ψһ��ʾ�� */
	public static String myUUID = "";
	/** ���ʵ�״̬��ʶ */
	public static final int SLEEP = 0;
	/** ���ʴ���׼��״̬ */
	public static final int READY = 1;
	/** ���ʴ������ò���״̬ */
	public static final int SETUP = 2;
	/** ��־����APPʱ���Ƿ�������ͼ���ļ���true �ǣ�false �� */
	public static boolean mOpenLastFile = true;
	/** ��ǰ��Ļ���� */
	public static int requestedOrientation;
	/** Զ�̿ͻ��ˣ��ǵ�ǰ�ͻ��ˣ�����������ʷͼ������ */
	public static SerializablePath lastRemotePath;
	/** ���أ���ǰ���ͻ��˵���ʷͼ������ */
	public static SerializablePath lastLocalPath;
	/** ��ʶ�Ƿ��ȱ���Զ�̿ͻ��˵���ʷͼ������ */
	public static boolean pickRemotePathFirst;
	/** ���洹ֱ���붯������ʼ�� */
	public final static float SLIDE_FROM = 1.0f;
	/** ���洹ֱ���붯���Ľ����� */
	public final static float SLIDE_TO = 0.0f;
	/** ���滬�붯���ĳ���ʱ�� */
	public final static int SLIDE_DURATION = 300;
}
