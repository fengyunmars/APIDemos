package com.fengyun.cardgame.activity;

import com.fengyun.cardgame.app.MainApplication;
import com.fengyun.cardgame.bean.ScreenType;
import com.fengyun.cardgame.util.DialogUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;

/**
 * 单击游戏界面
 * @author Administrator
 *
 */
public class SingleGameActivity extends BaseActivity {

	//获得MainApplication对象
	private MainApplication app=MainApplication.getInstance();
	//游戏界面视图
	private SingleGameView gameview;
	//分辨率大小
	private ScreenType screenType;
	
	//创建Handler对象，用于子线程与UI线程通信
	private Handler handler=new Handler(){
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Display display=getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics=new DisplayMetrics();
		display.getMetrics(metrics);
		if(metrics.heightPixels<480){
			screenType=ScreenType.low;
		}else if(metrics.heightPixels>=480&&metrics.heightPixels<720){
			screenType=ScreenType.middle;
		}else if(metrics.heightPixels>=720){
			screenType=ScreenType.large;
		}
		
		//创建游戏界面视图
		gameview=new SingleGameView(this,handler,screenType);
		//加载界面
		setContentView(gameview);
		//播放音乐特效
		app.playbgMusic("MusicEx_Normal2.ogg");
		
		setSurfaceViewDisplay();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		setSurfaceViewDisplay();
		super.onStart();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		System.out.println("---------------------------------onConfigurationChanged");
		setSurfaceViewDisplay();
	}
	
	public void setSurfaceViewDisplay(){
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();  
	    getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);  
	    int W = mDisplayMetrics.widthPixels;  
	    int H = mDisplayMetrics.heightPixels;  
  
	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {  
		   gameview.getLayoutParams().width = W;  
		   gameview.getLayoutParams().height = H;  
  
	    }else{  
	    	gameview.getLayoutParams().width = W;  
		    gameview.getLayoutParams().height = H;  
	    }  
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//当按返回按钮的时候，弹出退出对话框。
		//System.out.println(KeyEvent.KEYCODE_BACK+","+keyCode+","+event.getKeyCode());
		if(KeyEvent.KEYCODE_BACK==keyCode){
			DialogUtil.exitGameDialog(this);
		}		
		return true;
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//			  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		}
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onResume();
	}
}
