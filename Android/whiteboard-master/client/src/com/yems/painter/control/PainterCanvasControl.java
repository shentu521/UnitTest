package com.yems.painter.control;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.yems.painter.common.Commons;
import com.yems.painter.entity.BrushPreset;
import com.yems.painter.entity.MetadataRepositories;
import com.yems.painter.entity.UndoState;
import com.yems.painter.handler.PainterHandler;
import com.yems.painter.serializable.SerializablePaint;
import com.yems.painter.serializable.SerializablePath;
import com.yems.painter.serializable.SerializablePoint;
import com.yems.painter.serializable.ShapeRepositories;
import com.yems.painter.utils.Utils;

/**
 * @description: ʵ�ֻ�ͼ���ܵ��Զ��廭����
 * @date: 2015-3-16 ����2:01:12
 * @author: yems
 */
public class PainterCanvasControl extends SurfaceView implements Callback {
	private String TAG = "PainterCanvas";

	/** ��������߳�*/
	private PainterThreadControl mThread;
	/** ���滭���ϵ�ͼ������*/
	private Bitmap mBitmap;
	/** ����Ԥ��ֵ�������*/
	private BrushPreset mPreset;
	/** ����ҵ������*/
	private PainterHandler mPainterHandler;
	/** ��־�Ƿ��ڻ��ʲ������ý���,true��:false��*/
	private boolean mIsSetup;
	/** ��־����ͼ�������Ƿ��ڱ仯״̬��true�ǣ�false��*/
	private boolean mIsChanged;
	/** ��������ͼ����Ŀͻ��˶���*/
	private ClientControl mClient;
	/** �����ϵĵ�ǰ�ջ�����ɵ�·�� */
	public SerializablePath mCurrentPath;
	/** �������紫��Ļ���ϵ�л�����*/
	private SerializablePaint mPaint;

	/** 
	 * @return SerializablePaint
	 * @description:  ��ȡ���ʶ��� 
	 * @date 2015-3-16 ����10:10:31    
	 * @author: yems 
	 */
	public SerializablePaint getPaint() {
		return mPaint;
	}

	public PainterCanvasControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mPreset = new BrushPreset(Commons.PEN, Color.BLACK);
		mPaint = new SerializablePaint();
		setFocusable(true);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (!hasWindowFocus) {
			getThread().freeze();
		} else {
			if (!isSetup()) {
				getThread().activate();
			} else {
				getThread().setup();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (mBitmap == null) {
			mBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_4444);
			getThread().setBitmap(mBitmap, true);

			Bitmap bitmap = mPainterHandler.getLastPicture();

			if (bitmap != null) {
				float bitmapWidth = bitmap.getWidth();
				float bitmapHeight = bitmap.getHeight();
				float scale = 1.0f;

				Matrix matrix = new Matrix();
				if (width != bitmapWidth || height != bitmapHeight) {
					if (width == bitmapHeight || height == bitmapWidth) {
						if (width > height) {
							matrix.postRotate(-90, width / 2, height / 2);
						} else if (bitmapWidth != bitmapHeight) {
							matrix.postRotate(90, width / 2, height / 2);
						} else {
							if (Commons.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
								matrix.postRotate(-90, width / 2, height / 2);
							}
						}
					} else {
						if (Commons.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
							if (bitmapWidth > bitmapHeight
									&& bitmapWidth > width) {
								scale = (float) width / bitmapWidth;
							} else if (bitmapHeight > bitmapWidth
									&& bitmapHeight > height) {
								scale = (float) height / bitmapHeight;
							}
						} else {
							if (bitmapHeight > bitmapWidth
									&& bitmapHeight > height) {
								scale = (float) height / bitmapHeight;
							} else if (bitmapWidth > bitmapHeight
									&& bitmapWidth > width) {
								scale = (float) width / bitmapWidth;
							}
						}
					}

					if (scale == 1.0f) {
						matrix.preTranslate((width - bitmapWidth) / 2,
								(height - bitmapHeight) / 2);
					} else {
						matrix.postScale(scale, scale, bitmapWidth / 2,
								bitmapHeight / 2);
						matrix.postTranslate((width - bitmapWidth) / 2,
								(height - bitmapHeight) / 2);
					}
				}
				getThread().restoreBitmap(bitmap, matrix);
			}
		} else {
			getThread().setBitmap(mBitmap, false);
		}
		getThread().setPreset(mPreset);
		if (!isSetup()) {
			getThread().activate();
		} else {
			getThread().setup();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		getThread().on();
		getThread().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		getThread().off();
		while (retry) {
			try {
				getThread().join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		mThread = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!getThread().isReady()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsChanged = true;
			Commons.pickRemotePathFirst = true;
			getThread().clearBrushPoint();
			setPathValue();

			// ��ǰʵ��ֻ�л����ߡ�curve��,��������չ����ͼ��
			mCurrentPath.points.add(new SerializablePoint(event.getX(), event
					.getY()));
			break;

		case MotionEvent.ACTION_MOVE:
			getThread().draw((int) event.getX(), (int) event.getY());
			mCurrentPath.points.add(new SerializablePoint(event.getX(), event
					.getY()));
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			getThread().clearBrushPoint();
			applyShape();

			if (mClient != null) {
				mClient.commitShapToServer();
			}
			break;
		}
		return true;
	}

	/** 
	 * @return void
	 * @description:  ��ÿһ��·�����ó�ʼֵ
	 * @date 2015-3-16 ����10:11:57    
	 * @author: yems 
	 */
	private void setPathValue() {
		mCurrentPath = new SerializablePath();
		mCurrentPath.setOPType("send");
		mCurrentPath.setScreenHeight(Commons.CURRENT_SCREEN_HEIGHT);
		mCurrentPath.setScreenWidth(Commons.CURRENT_SCREEN_WIDTH);
		mCurrentPath.setMyUUID(Commons.myUUID);
		mCurrentPath
				.setSerializablePaint((SerializablePaint) SerializationUtils
						.clone(mPaint));

		Utils.pickUndoCaches();
	}

	public PainterThreadControl getThread() {
		if (mThread == null) {
			mThread = new PainterThreadControl(getHolder(), this);
			mThread.setState(UndoState.getInstance());
		}
		return mThread;
	}

	private void applyShape() {
		try {
			MetadataRepositories.getInstance().addShape(
					(SerializablePath) SerializationUtils.clone(mCurrentPath));
			Commons.lastLocalPath = mCurrentPath;
			mCurrentPath = null;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/** 
	 * @return void
	 * @description: ���浱ǰ�����ϵ�ͼ��
	 * @date 2015-3-16 ����10:12:28    
	 * @author: yems 
	 */
	public void saveBitmap(String pictureName) throws FileNotFoundException {
		synchronized (getHolder()) {
			Utils.getInstance()
					.saveBitmap(pictureName, getThread().getBitmap());
		}
		changed(false);
	}

	/** 
	 * @return BrushPreset
	 * @description: ��ȡ��ǰ���� 
	 * @date 2015-3-16 ����10:13:48    
	 * @author: yems 
	 */
	public BrushPreset getCurrentPreset() {
		return mPreset;
	}

	/** 
	 * @return void
	 * @description: ���û�����ɫ 
	 * @date 2015-3-16 ����10:13:58    
	 * @author: yems 
	 */
	public void setPresetColor(int color) {
		mPaint.setColor(color);
		mPreset.setColor(color);
		Log.i(TAG, "�����˻�����ɫֵ--" + color);
		getThread().setPreset(mPreset);
	}

	/** 
	 * @return void
	 * @description: ���û��ʳߴ� 
	 * @date 2015-3-16 ����10:14:08    
	 * @author: yems 
	 */
	public void setPresetSize(float size) {
		mPreset.setSize(size);
		mPaint.setSize(size);
		Log.i(TAG, "�����˻��ʴ�С--" + size);
		getThread().setPreset(mPreset);
	}

	/** 
	 * @return void
	 * @description: ���û��� 
	 * @date 2015-3-16 ����10:14:17    
	 * @author: yems 
	 */
	public void setPreset(BrushPreset preset) {
		mPreset = preset;
		getThread().setPreset(mPreset);
	}

	/** 
	 * @return boolean
	 * @description: �Ƿ������û��ʲ������� 
	 * @date 2015-3-16 ����10:14:36    
	 * @author: yems 
	 */
	public boolean isSetup() {
		return mIsSetup;
	}

	/** 
	 * @return void
	 * @description: �л��������ͻ��ʲ������ý��� 
	 * @date 2015-3-16 ����10:15:40    
	 * @author: yems 
	 */
	public void setup(boolean setup) {
		mIsSetup = setup;

		if (mIsSetup) {
			getThread().setup();
		} else {
			getThread().activate();
		}
	}

	/** 
	 * @return boolean
	 * @description: �Ƿ񻭰���ͼ�������б仯 
	 * @date 2015-3-16 ����10:16:16    
	 * @author: yems 
	 */
	public boolean isChanged() {
		return mIsChanged;
	}

	/**
	 * @param changed
	 *            true��ʾ��ǰ����ͼ�θ�����
	 * @description: ���õ�ǰ�����ϵ�ͼ���Ƿ��и���
	 * @date: 2015-3-16 ����2:31:28
	 * @author: yems
	 */
	public void changed(boolean changed) {
		mIsChanged = changed;
	}

	/** 
	 * @return void
	 * @description: ִ�г���ͼ�β��� 
	 * @date 2015-3-16 ����10:17:08    
	 * @author: yems 
	 */
	public void undo() {
		getThread().undo();
	}

	/** 
	 * @return void
	 * @description: ���»���ͼ�� 
	 * @date 2015-3-16 ����10:17:25    
	 * @author: yems 
	 */
	public void repaint(boolean isRepaintAll) {
		List<SerializablePath> bufferShapes = MetadataRepositories
				.getInstance().getBufferShapes();
		if (isRepaintAll) {
			getThread().repaintAllShapes(bufferShapes);
		} else {
			getThread().repaintNewShape(bufferShapes);
		}
	}

	/** 
	 * @return void
	 * @description: �������л����ʵĲ��� 
	 * @date 2015-3-16 ����10:18:38    
	 * @author: yems 
	 */
	public void setSerializablePaintStyle(int color, float size) {
		mPaint.setSize(size);
		mPaint.setColor(color);
	}

	/** 
	 * @return void
	 * @description: ��ʼ���ͻ��˶���� ����ҵ������� 
	 * @date 2015-3-16 ����10:45:06    
	 * @author: yems 
	 */
	public void init(ClientControl client, PainterHandler painterHandler) {
		mPainterHandler = painterHandler;
		mClient = client;
	}
}
