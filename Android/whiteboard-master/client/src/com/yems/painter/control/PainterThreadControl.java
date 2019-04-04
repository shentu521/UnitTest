package com.yems.painter.control;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yems.painter.common.Commons;
import com.yems.painter.entity.BrushPreset;
import com.yems.painter.entity.UndoState;
import com.yems.painter.serializable.SerializablePaint;
import com.yems.painter.serializable.SerializablePath;
import com.yems.painter.serializable.ShapeRepositories;

/**
 * @description: ���𻭰���Ƶ��߳�
 * @date: 2015-3-11 ����4:47:19
 * @author: yems
 */
public class PainterThreadControl extends Thread {
	private String TAG = "PainterThread";

	/** ����surfaceView�������ʾ */
	private SurfaceHolder mHolder;
	/** �Զ��廭��ʵ�� */
	PainterCanvasControl mPainterCanvas;
	/** ����ʵ������ */
	public Paint mBrush;
	/** ���ʳߴ� */
	private float mBrushSize;
	/** ��¼��󻭱�X��λ�õ㣬����������� */
	private int mLastBrushPointX;
	/** ��¼��󻭱�Y��λ�õ㣬����������� */
	private int mLastBrushPointY;
	/** �����ı���ɫ */
	private int mCanvasBgColor;
	/** ����ͼ�εĻ��� */
	private Canvas mCanvas;
	/** ���ڻ��滭���ϻ��Ƶ�ͼ�� */
	private Bitmap mBitmap;
	/** ��ʶ��ǰ�����߳��Ƿ��ڼ���״̬��ture�ǣ�false�� */
	private boolean mIsActive;
	/** ���ʵ�����״̬�����ߡ�׼�������ã� */
	private int mStatus;
	/** ����ͼ�β����Ķ��� */
	private UndoState mState;

	/**
	 * @param surfaceHolder
	 * @param painterCanvas
	 */
	public PainterThreadControl(SurfaceHolder surfaceHolder,
			PainterCanvasControl painterCanvas) {
		// base data
		mHolder = surfaceHolder;
		this.mPainterCanvas = painterCanvas;

		// defaults brush settings
		mBrushSize = 2;
		mBrush = new Paint();
		mBrush.setAntiAlias(true);
		mBrush.setColor(Color.rgb(0, 0, 0));
		mBrush.setStrokeWidth(mBrushSize);
		mBrush.setStrokeCap(Cap.ROUND);

		Commons.currentColor = Color.rgb(0, 0, 0);
		Commons.currentSize = 2;

		// default canvas settings
		mCanvasBgColor = Color.WHITE;

		// set negative coordinates for reset last point
		mLastBrushPointX = -1;
		mLastBrushPointY = -1;
	}

	@Override
	public void run() {
		waitForBitmap();

		while (isRun()) {
			Canvas canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				synchronized (mHolder) {
					switch (mStatus) {
					case Commons.READY: {
						if (canvas != null) {
							canvas.drawBitmap(mBitmap, 0, 0, null);
						}
						break;
					}
					case Commons.SETUP: {
						if (canvas != null) {
							canvas.drawColor(mCanvasBgColor);
							canvas.drawLine(50,
									(mBitmap.getHeight() / 100) * 35,
									mBitmap.getWidth() - 50,
									(mBitmap.getHeight() / 100) * 35, mBrush);
						}
						break;
					}
					}
				}
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
				if (isFreeze()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @return
	 * @description: ��ȡ��ǰ����
	 * @date: 2015-3-16 ����2:11:47
	 * @author: yems
	 */
	public Paint getmBrush() {
		return mBrush;
	}

	/**
	 * @param bitmap
	 *            Ҫ��ԭ�������ϵ�ͼƬ
	 * @param matrix
	 *            Я����bitmap��ת�ĽǶ�ֵ
	 * @description: ��ԭָ����bitmapͼƬ��������
	 * @date: 2015-3-16 ����2:12:02
	 * @author: yems
	 */
	public void restoreBitmap(Bitmap bitmap, Matrix matrix) {
		mCanvas.drawBitmap(bitmap, matrix, new Paint(Paint.FILTER_BITMAP_FLAG));
	}

	/**
	 * @param preset
	 * @description: �����ʵ���ɫ���ߴ�ı�ʱ���øú���
	 * @date: 2015-3-16 ����2:15:14
	 * @author: yems
	 */
	public void setPreset(BrushPreset preset) {
		mBrush.setColor(preset.currentColor);
		mBrushSize = preset.currentSize;
		mBrush.setStrokeWidth(mBrushSize);
	}

	/**
	 * 
	 * @description: ��ʼ����ǰ�����λ�õ�ı��ֵ
	 * @date: 2015-3-16 ����2:15:58
	 * @author: yems
	 */
	public void clearBrushPoint() {
		mLastBrushPointX = -1;
		mLastBrushPointY = -1;
	}

	/**
	 * @param x
	 *            x������ֵ
	 * @param y
	 *            y������ֵ
	 * @description: ����ͼ��
	 * @date: 2015-3-12 ����11:17:26
	 * @author�� yems
	 */
	public void draw(int x, int y) {
		Log.i("paths", "mCanvas---" + mCanvas + ",mBrush--" + mBrush);
		if (mLastBrushPointX > 0) {
			if (mLastBrushPointX - x == 0 && mLastBrushPointY - y == 0) {
				return;
			}
			if (mCanvas == null) {
				// ��һ�ν���APP������ϵͳSurface��������ʱ�ԣ���ʱmCanvas��δʵ������ɣ�����˷��͹��������ݲ������ϻ���
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mCanvas.drawLine(x, y, mLastBrushPointX, mLastBrushPointY,
						mBrush);
			} else {
				mCanvas.drawLine(x, y, mLastBrushPointX, mLastBrushPointY,
						mBrush);
			}

		} else {
			if (mCanvas == null) {
				// ��һ�ν���APP������ϵͳSurface��������ʱ�ԣ���ʱmCanvas��δʵ������ɣ�����˷��͹��������ݲ������ϻ���
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mCanvas.drawCircle(x, y, mBrush.getStrokeWidth() * .5f, mBrush);
			} else {
				mCanvas.drawCircle(x, y, mBrush.getStrokeWidth() * .5f, mBrush);
			}
		}
		mLastBrushPointX = x;
		mLastBrushPointY = y;
	}

	/**
	 * @param bitmap
	 *            Ҫ���صı���ͼ
	 * @param clear
	 *            true ��ʾ����������ڼ��ر���ͼ��false��ʾֱ�Ӽ��ر���ͼ
	 * @description: ���õ�ǰ����ı���
	 * @date: 2015-3-16 ����2:19:11
	 * @author: yems
	 */
	public void setBitmap(Bitmap bitmap, boolean clear) {
		mBitmap = bitmap;
		if (clear) {
			mBitmap.eraseColor(mCanvasBgColor);
		}
		mCanvas = new Canvas(mBitmap);
	}

	/**
	 * 
	 * @description: �����������ͼ������
	 * @date: 2015-3-16 ����2:19:25
	 * @author: yems
	 */
	public void clearBitmap() {
		mBitmap.eraseColor(mCanvasBgColor);
		if (ShapeRepositories.getInstance().getUndoCaches().size() != 0) {
			ShapeRepositories.getInstance().getUndoCaches().clear();
		}
		Commons.lastLocalPath = null;
		Commons.lastRemotePath = null;
	}

	/**
	 * @return
	 * @description: ��ȡ��ǰ���Ƶ�ͼ��
	 * @date: 2015-3-16 ����2:19:48
	 * @author: yems
	 */
	public Bitmap getBitmap() {
		return mBitmap;
	}

	/**
	 * 
	 * @description: ��ʾ���������߳�
	 * @date: 2015-3-16 ����2:23:24
	 * @author: yems
	 */
	public void on() {
		mIsActive = true;
	}

	/**
	 * 
	 * @description: ��ʾ�رջ����߳�
	 * @date: 2015-3-16 ����2:23:42
	 * @author: yems
	 */
	public void off() {
		mIsActive = false;
	}

	/**
	 * 
	 * @description: ��ʾ���ߣ���ͣ�������߳�
	 * @date: 2015-3-16 ����2:24:12
	 * @author: yems
	 */
	public void freeze() {
		mStatus = Commons.SLEEP;
	}

	/**
	 * 
	 * @description: ��ʾ�����߳��Ѿ�׼���ã����Ի�����
	 * @date: 2015-3-16 ����2:24:29
	 * @author: yems
	 */
	public void activate() {
		mStatus = Commons.READY;
	}

	/**
	 * 
	 * @description: ���崦�����û��ʲ�����״̬
	 * @date: 2015-3-16 ����2:25:26
	 * @author: yems
	 */
	public void setup() {
		mStatus = Commons.SETUP;
	}

	/**
	 * @return
	 * @description: �жϻ����Ƿ��ڶ��ᣨ�����߳����ߣ���״̬
	 * @date: 2015-3-16 ����2:26:08
	 * @author: yems
	 */
	public boolean isFreeze() {
		return (mStatus == Commons.SLEEP);
	}

	/**
	 * @return
	 * @description: �жϻ����Ƿ������û��ʲ�����״̬
	 * @date: 2015-3-16 ����2:27:31
	 * @author: yems
	 */
	public boolean isSetup() {
		return (mStatus == Commons.SETUP);
	}

	/**
	 * @return
	 * @description: �жϻ����Ƿ���׼���û��Ƶ�״̬
	 * @date: 2015-3-16 ����2:27:34
	 * @author: yems
	 */
	public boolean isReady() {
		return (mStatus == Commons.READY);
	}

	/**
	 * @return
	 * @description: �жϻ����߳��Ƿ���������״̬
	 * @date: 2015-3-16 ����2:27:36
	 * @author: yems
	 */
	public boolean isRun() {
		return mIsActive;
	}

	/**
	 * 
	 * @description: �����������ָ���һ�ε�ͼ�����ݣ�
	 * @date: 2015-3-12 ����12:53:39
	 * @author�� yems
	 */
	public void undo() {
		int undoSize = ShapeRepositories.getInstance().getUndoCaches().size();
		if (undoSize != 0) {
			undoRepaint();
			// �ָ�һ����ʷ���ݣ���Ӧ�����ﱣ������ݾ�Ҫ�Ƴ����������´�ִ�г�������ʱ���ֳ��ָ�ͼ��
			ShapeRepositories.getInstance().getUndoCaches()
					.remove(undoSize - 1);

		} else {
			mBitmap.eraseColor(mCanvasBgColor);
			mPainterCanvas.changed(false);
			Commons.lastLocalPath = null;
			Commons.lastRemotePath = null;
		}
	}

	/**
	 * 
	 * @description: ��������ʱ���ػ���ͼ��
	 * @date: 2015-3-12 ����4:44:39
	 * @author�� yems
	 */
	private void undoRepaint() {
		mBitmap.eraseColor(mCanvasBgColor);
		repaintAllShapes(ShapeRepositories.getInstance().getUndoCaches());
	}

	/**
	 * @return
	 * @description: ��ȡ�����ĵ�ǰ����ɫ
	 * @date: 2015-3-16 ����2:32:51
	 * @author: yems
	 */
	public int getBackgroundColor() {
		return mCanvasBgColor;
	}

	/**
	 * @param state
	 * @description: ���ó���ͼ�β����Ķ���
	 * @date: 2015-3-16 ����2:33:20
	 * @author: yems
	 */
	public void setState(UndoState state) {
		this.mState = state;
	}

	/**
	 * 
	 * @description: �ȴ���������ͼʵ���Ĵ���
	 * @date: 2015-3-16 ����2:35:01
	 * @author: yems
	 */
	private void waitForBitmap() {
		while (mBitmap == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @description: ����ͻ����ǵ�һ�����ӵ�����ˣ������ȫ��ͼ�Σ�����ֻ�������һ��
	 * @date: 2015-3-12 ����4:10:24
	 * @author�� yems
	 */
	public void repaintNewShape(List<SerializablePath> paths) {
		int size = paths.size();
		clearBrushPoint();

		// ��ȡ���������һ��ͼ�����ݣ�����˴�����������ͼ�����ݣ�
		SerializablePath newSerializablePath = paths.get(size - 1);
		float srcScreenWidth = newSerializablePath.getScreenWidth();
		float srcScreenHeight = newSerializablePath.getScreenHeight();
		SerializablePaint serializablePaint = newSerializablePath
				.getSerializablePaint();

		updatePaintParams(serializablePaint);

		// ����Ļ������ͬ���豸�ϻ�ͼ������ת������ֵ
		if (checkScreenDisplay(srcScreenWidth, srcScreenHeight)) {
			int pointCount = newSerializablePath.points.size(); // ÿһ��·������ɵĵ���
			for (int j = 0; j < pointCount; j++) {
				int x = (int) newSerializablePath.points.get(j).getX();
				int y = (int) newSerializablePath.points.get(j).getY();
				draw(x, y);
			}
		} else {
			// ��ͬ���ص��豸�ϻ�ͼ����Ҫת������ֵ
			int pointCount = newSerializablePath.points.size(); // ÿһ��·������ɵĵ���
			for (int j = 0; j < pointCount; j++) {
				int x = (int) newSerializablePath.points.get(j).getX();
				int y = (int) newSerializablePath.points.get(j).getY();
				draw((int) (x * (Commons.CURRENT_SCREEN_WIDTH / srcScreenWidth)),
						(int) (y * (Commons.CURRENT_SCREEN_HEIGHT / srcScreenHeight)));
			}
		}
		clearBrushPoint();
		resetColorAndSize();
	}

	/**
	 * �ػ������е�ͼ�����ݣ�����һ���µĿͻ��˺�������ͨ��ʱ�������ԭ�е�ͼ��������Ҫһ�����ػ��Ƶ����¿ͻ��ˣ�
	 */
	public void repaintAllShapes(List<SerializablePath> paths) {
		// List<SerializablePath> paths = MetadataRepositories.getInstance()
		// .getBufferShapes();
		int size = paths.size();
		Log.i("paths", "�ػ������е�ͼ�����ݣ�ͼ����ĿΪ---" + size);

		for (int i = 0; i < paths.size(); i++) {
			clearBrushPoint();
			// ������ȡ������ÿһ��ͼ�����ݣ�����˴�����������ͼ�����ݣ�
			SerializablePath serializablePath = paths.get(i);
			float srcScreenWidth = serializablePath.getScreenWidth();
			float srcScreenHeight = serializablePath.getScreenHeight();
			SerializablePaint serializablePaint = serializablePath
					.getSerializablePaint();
			updatePaintParams(serializablePaint);

			// ����Ļ�ֱ�����ͬ���豸�ϻ�ͼ������ת������ֵ
			if (checkScreenDisplay(srcScreenWidth, srcScreenHeight)) {
				int pointCount = serializablePath.points.size(); // ÿһ��·������ɵĵ���
				for (int j = 0; j < pointCount; j++) {
					int x = (int) serializablePath.points.get(j).getX();
					int y = (int) serializablePath.points.get(j).getY();
					draw(x, y);
				}
			} else {
				// ��ͬ���ص��豸�ϻ�ͼ����Ҫת������ֵ
				int pointCount = serializablePath.points.size(); // ÿһ��·������ɵĵ���
				for (int j = 0; j < pointCount; j++) {
					int x = (int) serializablePath.points.get(j).getX();
					int y = (int) serializablePath.points.get(j).getY();
					draw((int) (x * (Commons.CURRENT_SCREEN_WIDTH / srcScreenWidth)),
							(int) (y * (Commons.CURRENT_SCREEN_HEIGHT / srcScreenHeight)));
				}
			}
			clearBrushPoint();
			resetColorAndSize();
		}
	}

	/**
	 * ����豸�����Ļ�ֱ����Ƿ���ͬ
	 * 
	 * @param srcScreenWidth
	 * @param srcScreenHeight
	 * @return
	 */
	private boolean checkScreenDisplay(float srcScreenWidth,
			float srcScreenHeight) {
		return srcScreenWidth == Commons.CURRENT_SCREEN_WIDTH
				&& srcScreenHeight == Commons.CURRENT_SCREEN_HEIGHT;
	}

	private void resetColorAndSize() {
		mBrush.setStrokeWidth(Commons.currentSize);
		mBrush.setColor(Commons.currentColor);
	}

	/**
	 * @param serializablePaint
	 * @description:  ���»��ʵĲ�������ɫ���ߴ磩
	 * @date: 2015-3-16 ����2:38:05
	 * @author: yems
	 */
	private void updatePaintParams(SerializablePaint serializablePaint) {
		// ��¼��ǰ�豸���ʵĲ���ֵ���ڶ�̨�豸��
		Commons.currentColor = mBrush.getColor();
		Commons.currentSize = mBrush.getStrokeWidth();

		mBrush.setStrokeWidth(serializablePaint.getSize());
		mBrush.setColor(serializablePaint.getColor());
	}
}
