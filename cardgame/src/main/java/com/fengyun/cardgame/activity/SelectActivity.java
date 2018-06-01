package com.fengyun.cardgame.activity;

import com.fengyun.cardgame.app.MainApplication;
import com.fengyun.cardgame.util.DialogUtils;
import com.fengyun.cardgame.R;
import com.fengyun.cardgame.util.NetworkUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 游戏选择界面
 * @author Administrator
 *
 */
public class SelectActivity extends BaseActivity implements OnClickListener{

	//获得MainApplication对象
	private MainApplication app=MainApplication.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载布局
		setContentView(R.layout.activity_select);
		//绑定相应按钮单击事件
		findViewById(R.id.choose_game_exit).setOnClickListener(this);
		findViewById(R.id.choose_game_btn_multi_play).setOnClickListener(this);
		findViewById(R.id.choose_game_btn_single_play).setOnClickListener(this);
	}

	/**
	 * 按钮的单击处理方法
	 */
	@Override
	public void onClick(View v) {
		//播放音乐特效
		app.play("SpecOk.ogg");
		switch (v.getId()) {
			case R.id.choose_game_exit:
				//关闭当前界面，返回开始界面
				this.finish();
				break;
			case R.id.choose_game_btn_multi_play:
//				多人局域网对战 判断wifi连通
				if(NetworkUtils.isWifiConnected()){
					//连通的话，进入多人游戏界面
					startActivity(new Intent(this, Multi_Game_Join_Activity.class));
				}else{
					//没有连通的话，设置wifi对话框
					DialogUtils.wifiSetDialog(this);
				}
				break;
			case R.id.choose_game_btn_single_play:
				//进入单机游戏
				startActivity(new Intent(this, SingleGameActivity.class));
				break;
		}
	}
	
	
	
	
	
}
