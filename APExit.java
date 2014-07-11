package com.linecomm.lib.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.linecomm.lib.R;

/**
 * 
 * <b>APExit</b><br><br>
 * 
 * 뒤로가기 키 동작 수행시 지정한 종료형태에 따라 종료하는 기능을 제공<br><br>
 * 
 * <b>(R.string 에 다음 리소스를 추가)</b>
 * <xmp>
<string name="exit_dialog_msg">프로그램을 종료합니다.</string>
<string name="exit_doubleback_msg">이전 버튼을 한번 더 누르면 종료합니다</string>
 * </xmp>
 * 
 * @author 차형국
 * @since 2013.
 * @version 1.0
 */
public class APExit 
{ 
/** 즉시종료모드 			 	*/	public static final int EXIT_MODE_IMMEDIATLY = 0;
/** 두번 뒤로가기 키 누름			*/	public static final int EXIT_MODE_DOUBLEBACK = 1;
/** 종료 Alert Dialog 출력 			*/	public static final int EXIT_MODE_DIALOG = 2;

/** 두번 누름 키 제한시간			*/	private static final int DOUBLE_BACK_EXIT_TIMELIMIT = 2000;

/**	요청 Activity				*/	private Activity activity;
/**	요청하는 종료모드			*/	private int exitMode;
/**	두번누름 제한시간			*/	private Timer timer;
/**	뒤로가기키를 누른 상태의 여부		*/	private boolean mPressFirstBackKey;
	
	/** 
	 * 생성자
	 * @param activity		{@link Activity}
	 * @param mode 		종료모드상수
	 */
	public APExit(Activity activity, int mode) {
		this.activity = activity;
		this.exitMode = mode;
	}

	/** 
	 * Activity 에서 종료호출
	 */
	public void exit()
	{
		switch(exitMode) {
			case EXIT_MODE_IMMEDIATLY : {
				if (activity != null) {
					exitImmediatly();
				}
				break;
			}
			
			case EXIT_MODE_DOUBLEBACK : {
				
				if (activity != null) {
					if (!mPressFirstBackKey) {
						showSecondBackKey();
					} else {
						exitImmediatly();
					}
				}
				break;
			}
			
			case EXIT_MODE_DIALOG : {
				if (activity != null) {
					showExitingDialog();
				}
			}
		}
		
	}
	
	/** 
	 * 즉시종료
	 */
	private void exitImmediatly() {
		activity.moveTaskToBack(true);
		activity.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/** 
	 * 두번째 BackKey 누름 안내 Toast 출력 및 타이머 동작
	 */
	private void showSecondBackKey() {
		Toast.makeText(activity, 
						activity.getString(R.string.exit_doubleback_msg), 
						Toast.LENGTH_LONG).show();
		
		mPressFirstBackKey = true;
		
		// 원복 타이머 동작
		TimerTask second = new TimerTask() {
			@Override
			public void run() {
				timer.cancel();
				timer = null;
				mPressFirstBackKey = false;
			}
		};
		
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		
		timer = new Timer();
		timer.schedule(second, DOUBLE_BACK_EXIT_TIMELIMIT);
	}
 
	/** 
	 * AlertDialog 출력을 통한 프로그램 종료
	 */
	private void showExitingDialog() {
		AlertDialog.Builder aDialog = new AlertDialog.Builder(activity);
		
		aDialog.setTitle(activity.getString(R.string.exit_dialog_msg));
		aDialog.setPositiveButton(activity.getString(android.R.string.ok), 
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitImmediatly();
					}
				});
		aDialog.setNegativeButton(activity.getString(android.R.string.cancel), 
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		aDialog.create();
		aDialog.show();
	}
}
