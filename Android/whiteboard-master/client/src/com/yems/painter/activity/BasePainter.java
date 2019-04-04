package com.yems.painter.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.yems.painter.R;
import com.yems.painter.common.Commons;
import com.yems.painter.control.ClientControl;
import com.yems.painter.control.ColorPickerControl;
import com.yems.painter.control.PainterCanvasControl;
import com.yems.painter.entity.BrushPreset;
import com.yems.painter.entity.PainterSettings;
import com.yems.painter.handler.PainterHandler;
import com.yems.painter.task.SaveTask;
import com.yems.painter.task.SetWallpaperTask;
import com.yems.painter.utils.Utils;

/**
 * �������
 * 
 * @description:
 * @date: 2015-3-11 ����4:48:08
 * @author: yems
 */
public class BasePainter extends Activity implements OnClickListener
{
	/** �װ�Ļ��� */
	protected PainterCanvasControl mCanvas;
	/** �Զ�������Ĳ��� */
	protected RelativeLayout mSettingsLayout;
	/** �������������� */
	protected LinearLayout mPropertiesBar;
	/** ������ͻ��ˣ����C/Sģʽ�ķ���˶��ԣ� */
	protected ClientControl client;
	/** ����ҵ������ */
	protected PainterHandler mPainterHandler;
	/** ���ΰ��·��ؼ��ļ��ʱ�� */
	protected long exitTime = 0;
	/** ���ʳߴ��С */
	protected SeekBar mBrushSize;
	/** ���滭�� */
	protected PainterSettings mSettings;
	/** ������ݼ������Ըı仭����ɫ�����߸ı仭�ʳߴ磩 */
	protected int mVolumeButtonsShortcuts;
	/** ʰɫ����ť */
	protected ImageButton changeBrushColor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// ���뻭�����ʱ,��Ļ���ֳ���,������ʱ����ʾ�ý���
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setting_main, menu);
		return true;
	}

	@Override
	public void openOptionsMenu()
	{
		super.openOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		if (itemId == R.id.menu_brush && !mCanvas.isSetup())
		{
			// ���뻭����ʽ���ý���
			enterBrushSetup();
		} else if (itemId == R.id.menu_save)
		{
			// ���浱ǰ�����ϵ�ͼ������
			savePicture(Commons.ACTION_SAVE_AND_RETURN);
		} else if (itemId == R.id.menu_clear)
		{
			if (mCanvas.isChanged())
			{
				// ��ʾ����ͼ����ʾ��
				showDialog(R.id.dialog_clear);
			} else
			{
				// �������ͼ������
				clear();
			}
		} else if (itemId == R.id.menu_share)
		{
			// ����������
			share();
		} else if (itemId == R.id.menu_open)
		{
			// ��ͼ���ļ�
			open();
		} else if (itemId == R.id.menu_undo)
		{
			// ִ�г�������
			mCanvas.undo();
		} else if (itemId == R.id.menu_preferences)
		{
			// ����������ý���
			mPainterHandler.showPreferences();
		} else if (itemId == R.id.menu_set_wallpaper)
		{
			// ���ñ�ֽ
			new SetWallpaperTask(this, mCanvas, mSettings).execute();
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		if (id == R.id.dialog_clear)
		{
			// �����������ͼ�����ݵĶԻ���
			return mPainterHandler.createDialogClear(this);
		} else if (id == R.id.dialog_exit)
		{
			// �����˳�����Ի���
			return createDialogExit();
		} else if (id == R.id.dialog_share)
		{
			// ��������ͼ�����ݶԻ���
			return mPainterHandler.createDialogShare(this, mSettings);
		} else if (id == R.id.dialog_open)
		{
			// ������ͼ�����ݶԻ���
			return createDialogOpen();
		}
		return super.onCreateDialog(id);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			if (mCanvas.isSetup())
			{
				// ���·��ؼ��������ǰ���ڻ������ý��棬���˳�������������
				exitBrushSetup();
				return true;
			} else if (mCanvas.isChanged() || (!Commons.mIsNewFile && !new File(mSettings.getLastPicture()).exists()))
			{

				mSettings.setPreset(mCanvas.getCurrentPreset());
				Utils.getInstance().saveSettings(this, mSettings);

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

				int beforeExit = Integer.parseInt(preferences.getString(getString(R.string.preferences_before_exit), String.valueOf(Commons.BEFORE_EXIT_SUBMIT)));

				if (mCanvas.isChanged() && beforeExit == Commons.BEFORE_EXIT_SUBMIT)
				{
					// �˳�ǰ����ʾ����ͼ�ζԻ���
					showDialog(R.id.dialog_exit);
				} else if (beforeExit == Commons.BEFORE_EXIT_SAVED)
				{
					// ����ʾ��ʾ��ֱ���˳�
					savePicture(Commons.ACTION_SAVE_AND_EXIT);
				} else
				{
					return super.onKeyDown(keyCode, event);
				}
				return true;
			}

			// �������η��ؼ��˳�Ӧ��
			if (event.getAction() == KeyEvent.ACTION_DOWN)
			{
				if ((System.currentTimeMillis() - exitTime) > 2000)
				{
					Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else
				{
					finishPainter();
				}
				return true;
			}
			break;

		// ������������ݼ�
		case KeyEvent.KEYCODE_VOLUME_UP:
			switch (mVolumeButtonsShortcuts)
			{
			// ��ݼ�����Ϊ���ӻ��ʳߴ�
			case Commons.SHORTCUTS_VOLUME_BRUSH_SIZE:
				mCanvas.setPresetSize(mCanvas.getCurrentPreset().currentSize + 1);
				if (mCanvas.isSetup())
				{
					updateControls();
				}
				break;

			// ��ݼ�����Ϊִ�г�������
			case Commons.SHORTCUTS_VOLUME_UNDO_REDO:
				if (!mCanvas.isSetup())
				{
					mCanvas.undo();
				}
				break;
			}
			return true;

			// ������������ݼ�
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			switch (mVolumeButtonsShortcuts)
			{
			// ��ݼ�����Ϊ��С���ʳߴ�
			case Commons.SHORTCUTS_VOLUME_BRUSH_SIZE:
				mCanvas.setPresetSize(mCanvas.getCurrentPreset().currentSize - 1);
				if (mCanvas.isSetup())
				{
					updateControls();
				}
				break;
			// ��ݼ�����Ϊִ�г�������
			case Commons.SHORTCUTS_VOLUME_UNDO_REDO:
				if (!mCanvas.isSetup())
				{
					mCanvas.undo();
				}
				break;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * @description: ���뻭�����ý���
	 * @date: 2015-3-16 ����12:05:07
	 * @author�� yems
	 */
	protected void enterBrushSetup()
	{
		mSettingsLayout.setVisibility(View.VISIBLE);

		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{
				mCanvas.setVisibility(View.INVISIBLE);
				setPanelVerticalSlide(mPropertiesBar, Commons.SLIDE_FROM, Commons.SLIDE_TO, Commons.SLIDE_DURATION, true);
				mCanvas.setup(true);
			}
		}, 10);
	}

	/**
	 * 
	 * @description: ��������������ͼ��
	 * @date: 2015-3-16 ����12:59:56
	 * @author�� yems
	 */
	public void clear()
	{
		mCanvas.getThread().clearBitmap();
		mCanvas.changed(false);
		clearSettings();
		Commons.mIsNewFile = true;
		updateControls();

		client.clearAllShapes();
	}

	/**
	 * 
	 * @description: ����Ľ���
	 * @date: 2015-3-16 ����1:08:39
	 * @author�� yems
	 */
	protected void share()
	{
		if (!Utils.getInstance().isStorageAvailable(this))
		{
			return;
		}
		// ������´�����ͼ�Σ����ȱ����ٷ���
		if (mCanvas.isChanged() || Commons.mIsNewFile)
		{
			if (Commons.mIsNewFile)
			{
				savePicture(Commons.ACTION_SAVE_AND_SHARE);
			} else
			{
				showDialog(R.id.dialog_share);
			}
		} else
		{
			mPainterHandler.startShareActivity(mSettings.getLastPicture());
		}
	}

	/**
	 * 
	 * @description: ѡ��ת��Ļ
	 * @date: 2015-3-16 ����1:12:24
	 * @author�� yems
	 */
	protected void rotate()
	{
		mSettings.setForceOpenFile(true);

		if (!Commons.mIsNewFile || mCanvas.isChanged())
		{
			savePicture(Commons.ACTION_SAVE_AND_ROTATE);
		} else
		{
			Utils.getInstance().rotateScreen(this, mSettings);
		}
	}

	/**
	 * 
	 * @description: �������õ���ʷ���������ʵ���ɫ����С��
	 * @date: 2015-3-16 ����1:12:42
	 * @author�� yems
	 */
	protected void loadSettings()
	{
		mSettings = new PainterSettings();
		SharedPreferences settings = getSharedPreferences(Commons.SETTINGS_STORAGE, Context.MODE_PRIVATE);

		int orientation = settings.getInt(getString(R.string.settings_orientation), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mSettings.setOrientation(orientation);

		if (getRequestedOrientation() != mSettings.getOrientation())
		{
			setRequestedOrientation(mSettings.getOrientation());
		}

		mSettings.setLastPicture(settings.getString(getString(R.string.settings_last_picture), null));

		int type = settings.getInt(getString(R.string.settings_brush_type), Commons.PEN);

		// �Զ��廭����ʽ���ı仭�ʵ���ɫ�ͳߴ��С��
		if (type == Commons.CUSTOM)
		{
			float size = settings.getFloat(getString(R.string.settings_brush_size), Commons.DEFAULT_BRUSH_SIZE);
			int color = settings.getInt(getString(R.string.settings_brush_color), Color.BLACK);

			mSettings.setPreset(new BrushPreset(size, color));
			mSettings.getPreset().setType(type);
			mCanvas.setSerializablePaintStyle(color, size);

		} else
		{
			mSettings.setPreset(new BrushPreset(settings.getInt(getString(R.string.settings_brush_color), Color.BLACK)));

			mCanvas.setSerializablePaintStyle(mSettings.getPreset().currentColor, mSettings.getPreset().currentSize);
		}
		mCanvas.setPreset(mSettings.getPreset());
		mSettings.setForceOpenFile(settings.getBoolean(getString(R.string.settings_force_open_file), false));
	}

	/**
	 * 
	 * @description: �˳��������ý���
	 * @date: 2015-3-16 ����11:37:25
	 * @author�� yems
	 */
	protected void exitBrushSetup()
	{
		mSettingsLayout.setBackgroundColor(Color.WHITE);

		if (Commons.mIsHardwareAccelerated)
		{
			setPanelVerticalSlide(mPropertiesBar, 0.0f, 1.0f, Commons.SLIDE_DURATION, true);
			mCanvas.setup(false);
		} else
		{
			Handler handler = new Handler();
			handler.postDelayed(new Runnable()
			{
				public void run()
				{
					mCanvas.setVisibility(View.INVISIBLE);
					setPanelVerticalSlide(mPropertiesBar, 0.0f, 1.0f, Commons.SLIDE_DURATION, true);
					mCanvas.setup(false);
				}
			}, 10);
		}
	}

	/**
	 * @return
	 * @description: �����˳�����ĶԻ���
	 * @date: 2015-3-16 ����11:33:12
	 * @author�� yems
	 */
	protected Dialog createDialogExit()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(R.string.exit_app_prompt);
		alert.setCancelable(false);
		alert.setTitle(R.string.exit_app_prompt_title);

		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int which)
			{
				savePicture(Commons.ACTION_SAVE_AND_EXIT);
			}
		});
		alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int which)
			{
				finishPainter();
			}
		});
		return alert.create();
	}

	/**
	 * @return
	 * @description: ������ͼ���ļ��Ի���
	 * @date: 2015-3-16 ����11:35:59
	 * @author�� yems
	 */
	protected Dialog createDialogOpen()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(R.string.open_prompt);
		alert.setCancelable(false);
		alert.setTitle(R.string.open_prompt_title);

		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int which)
			{
				savePicture(Commons.ACTION_SAVE_AND_OPEN);
			}
		});
		alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int which)
			{
				mPainterHandler.startOpenActivity();
			}
		});

		return alert.create();
	}

	/**
	 * @param action
	 * @description: ���浱ǰ�����ϵ�ͼ������
	 * @date: 2015-3-16 ����11:37:41
	 * @author�� yems
	 */
	protected void savePicture(int action)
	{
		if (!Utils.getInstance().isStorageAvailable(this))
		{
			return;
		}
		final int taskAction = action;

		new SaveTask(this, mCanvas, mSettings)
		{
			protected void onPostExecute(String pictureName)
			{
				Commons.mIsNewFile = false;

				if (taskAction == Commons.ACTION_SAVE_AND_SHARE)
				{
					mPainterHandler.startShareActivity(pictureName);
				}

				if (taskAction == Commons.ACTION_SAVE_AND_OPEN)
				{
					mPainterHandler.startOpenActivity();
				}

				super.onPostExecute(pictureName);

				if (taskAction == Commons.ACTION_SAVE_AND_EXIT)
				{
					finishPainter();
				}
				if (taskAction == Commons.ACTION_SAVE_AND_ROTATE)
				{
					Utils.getInstance().rotateScreen(BasePainter.this, mSettings);
				}
			}
		}.execute();
	}

	/**
	 * 
	 * @description:���»��ʳߴ�ؼ��ĵ�ǰ״ֵ̬
	 * @date: 2015-3-16 ����1:40:57
	 * @author�� yems
	 */
	protected void updateControls()
	{
		mBrushSize.setProgress((int) mCanvas.getCurrentPreset().currentSize);
	}

	/**
	 * ������崹ֱ����(�˳����ò����Ľ���ʱ������豸֧��Ӳ�����٣�����ô˷���)
	 * 
	 * @param layout
	 * @param from
	 * @param to
	 * @param duration
	 */
	protected void setPanelVerticalSlide(LinearLayout layout, float from, float to, int duration)
	{
		setPanelVerticalSlide(layout, from, to, duration, false);
	}

	/**
	 * @param layout
	 *            Ҫ���Ŷ����Ĳ���
	 * @param from
	 *            ������ʼ��
	 * @param to
	 *            ����������
	 * @param duration
	 *            ����ʱ��
	 * @param last
	 * @description: ��ָ��������Ӵ�ֱ���붯��
	 * @date: 2015-3-16 ����12:55:19
	 * @author�� yems
	 */
	private void setPanelVerticalSlide(LinearLayout layout, float from, float to, int duration, boolean last)
	{
		Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, from, Animation.RELATIVE_TO_SELF, to);

		animation.setDuration(duration);
		animation.setFillAfter(true);
		animation.setInterpolator(this, android.R.anim.decelerate_interpolator);

		final float listenerFrom = Math.abs(from);
		final float listenerTo = Math.abs(to);
		final boolean listenerLast = last;
		final View listenerLayout = layout;

		if (listenerFrom > listenerTo)
		{
			listenerLayout.setVisibility(View.VISIBLE);
		}

		animation.setAnimationListener(new Animation.AnimationListener()
		{

			public void onAnimationStart(Animation animation)
			{
			}

			public void onAnimationRepeat(Animation animation)
			{
			}

			public void onAnimationEnd(Animation animation)
			{
				if (listenerFrom < listenerTo)
				{
					listenerLayout.setVisibility(View.INVISIBLE);
					if (listenerLast)
					{
						mCanvas.setVisibility(View.VISIBLE);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable()
						{
							public void run()
							{
								mSettingsLayout.setVisibility(View.GONE);
							}
						}, 10);
					}
				} else
				{
					if (listenerLast)
					{
						mCanvas.setVisibility(View.VISIBLE);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable()
						{
							public void run()
							{
								mSettingsLayout.setBackgroundColor(Color.TRANSPARENT);
							}
						}, 10);
					}
				}
			}
		});
		layout.setAnimation(animation);
	}

	/**
	 * 
	 * @description: ���Ѿ�������SD�ϵ��ļ�
	 * @date: 2015-3-16 ����1:39:57
	 * @author�� yems
	 */
	private void open()
	{
		if (!Utils.getInstance().isStorageAvailable(this))
		{
			return;
		}
		mSettings.setForceOpenFile(true);
		if (mCanvas.isChanged())
		{
			showDialog(R.id.dialog_open);
		} else
		{
			mPainterHandler.startOpenActivity();
		}
	}

	/**
	 * 
	 * @description: ������ò���
	 * @date: 2015-3-16 ����1:40:14
	 * @author�� yems
	 */
	private void clearSettings()
	{
		mSettings.setLastPicture(null);
		deleteFile(Commons.SETTINGS_STORAGE);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();

		// �����ʰɫ��
		if (id == R.id.bt_change_brush_color)
		{
			new ColorPickerControl(this, new ColorPickerControl.OnColorChangedListener()
			{
				public void colorChanged(int color)
				{
					mCanvas.setPresetColor(color);
				}
			}, mCanvas.getCurrentPreset().currentColor).show();
			return;
		}
		updateControls();
	}

	/**
	 * 
	 * @description: �˳���ǰ����Ӧ��
	 * @date: 2015-3-16 ����11:40:47
	 * @author�� yems
	 */
	private void finishPainter()
	{
		finish();
		// 0 ��ʾ������ֹ���е�ǰӦ�õ�JVM��ȷ�����������̹߳ر�
		// System.exit(0);
	}
}
