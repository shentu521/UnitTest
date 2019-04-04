package com.yems.painter.handler;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.yems.painter.Painter;
import com.yems.painter.R;
import com.yems.painter.activity.PainterPreferences;
import com.yems.painter.common.Commons;
import com.yems.painter.control.PainterCanvasControl;
import com.yems.painter.entity.PainterSettings;
import com.yems.painter.task.SaveTask;
import com.yems.painter.utils.Utils;

/**
 * @ClassName: PainterHandler
 * @Description: ����ҵ�������
 * @author lwtx-yems
 * @date 2015-3-10 ����01:51:10
 * 
 */
public class PainterHandler {
	private Context mContext;
	/** �����������*/
	private PainterCanvasControl mCanvas;
	/** */
	private PainterSettings mSettings;
	/** */
	private Painter mPainter;

	public PainterHandler(Context context, PainterCanvasControl canvas,
			PainterSettings settings) {
		this.mCanvas = canvas;
		this.mContext = context;
		this.mSettings = settings;
	}

	/** 
	 * @return void
	 * @description: ���û��ʶ���
	 * @date 2015-3-16 ����10:41:33    
	 * @author: yems 
	 */
	public void setPainter(Painter painter) {
		this.mPainter = painter;
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param action ����ͼƬ��ͬʱ�������Ĳ������ͣ��磺�򿪡��˳��ȣ�
	 * @param canvas
	 * @param settings
	 */
	public void savePicture(int action, PainterCanvasControl canvas,
			PainterSettings settings) {
		if (!Utils.getInstance().isStorageAvailable(mContext)) {
			return;
		}
		this.mCanvas = canvas;
		this.mSettings = settings;

		final int taskAction = action;

		new SaveTask(mContext, canvas, settings) {
			protected void onPostExecute(String pictureName) {
				Commons.mIsNewFile = false;

				if (taskAction == Commons.ACTION_SAVE_AND_SHARE) {
					startShareActivity(pictureName);
				}

				if (taskAction == Commons.ACTION_SAVE_AND_OPEN) {
					startOpenActivity();
				}

				super.onPostExecute(pictureName);

				if (taskAction == Commons.ACTION_SAVE_AND_EXIT) {
					((Painter) mContext).finish();
					System.exit(0);
				}
				if (taskAction == Commons.ACTION_SAVE_AND_ROTATE) {
					rotateScreen(mPainter, mSettings);
				}
			}
		}.execute();
	}

	/**
	 * �����������
	 * 
	 * @param pictureName
	 *            Ҫ�����ͼƬ����
	 */
	public void startShareActivity(String pictureName) {
		Uri uri = Uri.fromFile(new File(pictureName));
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType(Commons.PICTURE_MIME);
		i.putExtra(Intent.EXTRA_STREAM, uri);
		mContext.startActivity(Intent.createChooser(i,
				mContext.getString(R.string.share_image_title)));
	}

	/**
	 * �������ļ��Ľ���
	 */
	public void startOpenActivity() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setDataAndType(Uri.fromFile(new File(Utils.getInstance()
				.getSaveDir(mContext))), Commons.PICTURE_MIME);
		((Painter) mContext).startActivityForResult(
				Intent.createChooser(intent,
						mContext.getString(R.string.open_prompt_title)),
				Commons.REQUEST_OPEN);
	}

	/**
	 * ��ת��Ļ�ķ���
	 * 
	 * @param activity
	 * @param settings
	 */
	public void rotateScreen(Activity activity, PainterSettings settings) {
		if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			settings.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			Utils.getInstance().saveSettings(activity, settings);
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			settings.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			Utils.getInstance().saveSettings(activity, settings);
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	/**
	 * @param context
	 * @param settings
	 * @return
	 * @description: �����������
	 * @date: 2015-3-16 ����11:35:24
	 * @author�� yems
	 */
	public Dialog createDialogShare(Context context,
			final PainterSettings settings) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setMessage(R.string.share_prompt);
		alert.setCancelable(false);
		alert.setTitle(R.string.share_prompt_title);

		alert.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						savePicture(Commons.ACTION_SAVE_AND_SHARE, mCanvas,
								mSettings);
					}
				});
		alert.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						startShareActivity(settings.getLastPicture());
					}
				});

		return alert.create();
	}

	/**
	 * @param context
	 * @return
	 * @description: �������ͼ�εĽ���
	 * @date: 2015-3-16 ����11:32:54
	 * @author�� yems
	 */
	public Dialog createDialogClear(Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setMessage(R.string.clear_bitmap_prompt);
		alert.setCancelable(false);
		alert.setTitle(R.string.clear_bitmap_prompt_title);

		alert.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						mPainter.clear();
					}
				});
		alert.setNegativeButton(R.string.no, null);
		return alert.create();
	}

	/** 
	 * @return void
	 * @description: �򿪲������ý��� 
	 * @date 2015-3-16 ����10:39:28    
	 * @author: yems 
	 */
	public void showPreferences() {
		Intent intent = new Intent();
		intent.setClass(mContext, PainterPreferences.class);
		intent.putExtra("orientation", mPainter.getRequestedOrientation());
		mContext.startActivity(intent);
	}

	/** 
	 * @return void
	 * @description: ����Ӧ�� 
	 * @date 2015-3-16 ����10:39:38    
	 * @author: yems 
	 */
	public void restart() {
		Intent intent = mPainter.getIntent();
		mPainter.overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		mPainter.finish();
		mPainter.overridePendingTransition(0, 0);
		mContext.startActivity(intent);
	}
	
	/** 
	 * @return Bitmap
	 * @description: ��ȡ���µ�ͼ�� 
	 * @date 2015-3-16 ����10:39:48    
	 * @author: yems 
	 */
	public Bitmap getLastPicture() {
		Bitmap savedBitmap = null;

		if (!Commons.mOpenLastFile && mSettings.isForceOpenFile()) {
			mSettings.setLastPicture(null);
			Commons.mIsNewFile = true;
			return savedBitmap;
		}
		mSettings.setForceOpenFile(false);

		if (mSettings.getLastPicture() != null) {
			if (new File(mSettings.getLastPicture()).exists()) {
				savedBitmap = BitmapFactory.decodeFile(mSettings.getLastPicture());
				Commons.mIsNewFile = false;
			} else {
				mSettings.setLastPicture(null);
			}
		}
		return savedBitmap;
	}
}