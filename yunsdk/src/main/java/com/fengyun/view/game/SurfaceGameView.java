package com.fengyun.view.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * 单机游戏视图
 * 继承SurfaceView，实现SurfaceHolder.Callback和Runnable
 * 
 * 实现 Runnable  作用：用于绘制界面线程
 *
 */
public abstract class SurfaceGameView extends SurfaceView implements SurfaceHolder.Callback{

	private static final String TAG = SurfaceGameView.class.getSimpleName();
	//视图控制器
	private SurfaceHolder surfaceHolder = null;
	//绘图线程
	public Thread drawThread = null;
	public Thread pulseThread = null;
	public long mPulse = 1;
	public int mPulseInterval = 800;
    public boolean repaint;
    public static Canvas canvas;

    protected Object lock = new Object();

    /**
	 * 构造方法
	 * @param context 上下文
	 */
	public SurfaceGameView(Context context) {
		super(context);
		//当前视图获得焦点
		setFocusable(true);
		//获得视图控制器，赋值
		surfaceHolder = this.getHolder();
		//给视图控制器添加监听
		surfaceHolder.addCallback(this);
		setWillNotDraw(false);
	}

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
//        if(hasWindowFocus){
//            drawThread.notify();
//            pulseThread.notify();
//        }else {
//            try {
//                drawThread.wait();
//                pulseThread.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    protected void onFocusLost() {
        super.onFocusLost();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

    }

    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//SurfaceView发生更改触发
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 开始绘图线程
		drawThread=new Thread(new Runnable() {
            @Override
            public void run() {
                drawGame();
                while (true) {
                    if(repaint)
                    {
                        //绘制界面
                        drawGame();
                        repaint=true;
                    }
                    //修改50毫秒
                    Sleep(50);
                }
            }
        });
		drawThread.start();

		// 开始绘图线程
		pulseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
				    synchronized (lock) {
                        mPulse++;
                        updatePulse();
                    }
					//修改50毫秒
					Sleep(mPulseInterval);
				}
			}
		});
		pulseThread.start();
	}

    public abstract void updatePulse();

    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("dingxiaoquan", TAG + " surfaceDestroyed !");
	}
	

	/**
	 *绘图函数
	 */
	public void drawGame(){
		synchronized (surfaceHolder) {
			try {
				//锁定整个视图
				canvas = surfaceHolder.lockCanvas();
				onGameDraw(canvas);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null){
					//绘制完毕，进行关闭，提交刷新
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

    protected abstract void onGameDraw(Canvas canvas);

    //线程休眠方法
	public void Sleep(long i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


}
