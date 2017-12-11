package com.fengyun.cardgame.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.fengyun.cardgame.app.MainApplication;
import com.fengyun.cardgame.bean.BotPlayer;
import com.fengyun.cardgame.bean.Card;
import com.fengyun.cardgame.bean.GameStep;
import com.fengyun.cardgame.bean.HandCardLandlord;
import com.fengyun.cardgame.bean.Player;
import com.fengyun.cardgame.bean.RealPlayer;
import com.fengyun.cardgame.bean.ScreenType;
import com.fengyun.util.ImageUtils;
import com.fengyun.util.AppUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * 单机游戏视图
 * 继承SurfaceView，实现SurfaceHolder.Callback和Runnable
 * 
 *实现Runnable  作用：用于绘制界面线程
 *
 */
public class SingleGameView extends SurfaceView implements SurfaceHolder.Callback,Runnable  {

	//获得MainApplication实例
	private MainApplication app = MainApplication.getInstance();
	
	//获得Asset资源管理器
	private AssetManager assetManager;
	
	//分辨率大小
	private ScreenType screenType;
	
	//视图控制器
	private SurfaceHolder surfaceHolder=null;
	
	//画布
	private Canvas canvas=null;
	
	
	
	// 屏幕宽度和高度
	private	int screen_height=0;
	private	int screen_width=0;
	
	//游戏线程
	private Thread gameThread=null;
	
	private Thread animationThread=null;
	
	//绘图线程
	private Thread drawThread=null;
	
	////轮流
	private int turn=-1;
	
	public int turnState = 2;
	
	public int hintIndex = 0;
	
	public HandCardLandlord currentHandCardLandLord;
	
	public float sx,sy,fx,fy;
	
	public List<Card> selectedCards = new ArrayList<Card>();
	
	public int preSelected = -1;
	
	//是否重绘
	public Boolean repaint=false;
	
	//是否开始游戏
	private Boolean start=false;
	
	//上下文
	private Context appContext=null;
	
	//游戏状态
	public GameStep gameStep=GameStep.init;
	
	// 牌对象
	private Card card[] = new Card[54];
	
	//地主牌
	private List<Card> dizhuList=new ArrayList<Card>();
	
	//玩家信息 ：左边 电脑 ，中间 自己 ， 右边 电脑
	public BotPlayer player1=new BotPlayer(1,false,(byte)0, 0);
	public RealPlayer player2=new RealPlayer(2,true, (byte)1, 1);//设置自己
	public BotPlayer player3=new BotPlayer(3,false,(byte)1,2);
	
	//下注倍数
	public	int dizhubei=1;
	
	//轮到谁抢地主
	public int grabindex=0;
	
	private Bitmap[] ClubBitmaps = new Bitmap[16];
	private Bitmap[] ClubSmallBitmaps = new Bitmap[16];
	private Bitmap[] DiamondBitmaps = new Bitmap[16];
	private Bitmap[] DiamondSmallBitmaps = new Bitmap[16];
	private Bitmap[] HeartBitmaps = new Bitmap[16];
	private Bitmap[] HeartSmallBitmaps = new Bitmap[16];
	private Bitmap[] SpadeBitmaps = new Bitmap[16];
	private Bitmap[] SpadeSmallBitmaps = new Bitmap[16];
	
	private Bitmap BigJoker = null;
	private Bitmap BigJokerSmall = null;
	private Bitmap SmallJoker = null;
	private Bitmap SmallJokerSmall = null;
	
	private Bitmap cardSelectedBitmap;//玩家2的 牌正背景
	
	private Bitmap[] cardNumberBitmaps = new Bitmap[10];
	
	//头像图标
	public Bitmap player1Head = null;
	public Bitmap player2Head = null;
	public Bitmap player3Head = null;
	
	public Bitmap[] farmerBitmaps = new Bitmap[8];
	public Bitmap[] landlordBitmaps = new Bitmap[8];
	
	//背景图像
	private Bitmap background;
	//初始化是的玩家头像图标
	private Bitmap initHeadBitmap=null;
	
	private Bitmap playCardBitmap = null;
	//退出图标
	private Bitmap exitBitmap=null;
	
	//设置图标
	private Bitmap setupBitmap=null;
	
	//牌背景图标
	private Bitmap cardBgBitmap=null;
	
	//牌背景图标
	private Bitmap cardbeforeBitmap=null;
	
	//准备按钮文字
	private Bitmap prepareButtontextBitmap=null;
	
	//当前准备按钮背景
	public Bitmap prepareButtonbgBitmap=null;
	
	//准备没有按下按钮背景
	public Bitmap prepareButtonupbgBitmap=null;
	
	//准备按下按钮背景
	public Bitmap prepareButtondownbgBitmap=null;
	
	//准备好图标
	public Bitmap prepareButtonokBitmap=null;
	
	//数字图标
	public List<Bitmap> numberBitmaps=new ArrayList<Bitmap>();
	
		
	//倍字图像
	public Bitmap beiBitmap=null;

	//文字\按钮背景图像
	private Bitmap[] gramTextBitmap = new Bitmap[19];//图标
	
	//游戏结束的谁输谁赢祝贺
	private Bitmap[] overGameBitmaps=new Bitmap[4];
	
	private Bitmap overGamecurrBitmap=null;
	
	/**
	 * 构造方法
	 * @param context 上下文
	 * @param handler handler对象
	 */
	public SingleGameView(Context context,Handler handler,ScreenType screenType) { 
		super(context);
		assetManager=context.getAssets();
		//当前视图获得焦点
		setFocusable(true);
		//赋值
		this.screenType=screenType;
		this.appContext=context;
		
		//获得视图控制器，赋值
		surfaceHolder = this.getHolder();
		
		//给视图控制器添加监听
		surfaceHolder.addCallback(this);
		
	} 
	
	/**
	 * 初始化牌  创建54张扑克牌
	 */
	public void  initCard(){
		
		//从3到K
		for(int i = 3; i <= 15; i++){
			for(int j = 0; j<= 3; j++){
				switch (j) {
				case 0:
					card[(i-3)*4+j] = new Card("c"+(i-3)*4+j, i, 
							                   ClubBitmaps[i], ClubSmallBitmaps[i]);
					break;
				case 1:
					card[(i-3)*4+j] = new Card("c"+(i-3)*4+j, i, 
							                   DiamondBitmaps[i], DiamondSmallBitmaps[i]);
					break;
				case 2:
					card[(i-3)*4+j] = new Card("c"+(i-3)*4+j, i, 
							                   HeartBitmaps[i], DiamondSmallBitmaps[i]);
					break;
				case 3:
					card[(i-3)*4+j] = new Card("c"+(i-3)*4+j, i, 
							                   SpadeBitmaps[i], SpadeSmallBitmaps[i]);
					break;
				}
			}
		}
		
		//最后小王，大王
		card[52]=new Card("c"+54,16, SmallJoker, SmallJokerSmall);
		card[53]=new Card("c"+53,17, BigJoker, BigJokerSmall);
	}

	public void initLowBitMap(){

		
		try {
			//游戏界面背景
			background= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/state/lord_play_bg.png")),
					(float)(3/4));
			
			initHeadBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/logo_unknown.png")),
					(float)(1.0/3));
			
			exitBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/game_icon_exit.png")),
					(float)(1.0/3));
			setupBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/game_icon_setting.png")),
					(float)(1.0/3));
			cardBgBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/poke_back_header.png")),
					(float)(1.0/3));
			prepareButtontextBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),
					(float)(1.0/3));
			prepareButtonupbgBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/big_green_btn.png")),
					(float)(1.0/3));
			prepareButtonbgBitmap=prepareButtonupbgBitmap;
			prepareButtondownbgBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/big_green_btn_down.png")),
					(float)(1.0/3));
			prepareButtonokBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/ready.png")),
					(float)(1.0/3));
			//数字图片
			for(int i=0;i<10;i++){
				numberBitmaps.add(ImageUtils.zoomBitmap
						(BitmapFactory.decodeStream(assetManager.open("images/beishu_"+i+".png")),
					    (float)(1.0/3)));
			}
			
			//倍字图像
			beiBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/game_icon_bei.png")),
					(float)(1.0/3));
			
			for(int n=0;n<10;n++){
				cardNumberBitmaps[n]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/number/otherplayer_cards_num_"+n+".png")),
					(float)(1.0/3));
			}
			
			for(int n=3;n<=15;n++){
				ClubBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_club_" + n + ".png"));
				ClubSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_club_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				DiamondBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_diamond_" + n + ".png"));
				DiamondSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_diamond_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				HeartBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_heart_" + n + ".png"));
				HeartSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_heart_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				SpadeBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_spade_" + n + ".png"));
				SpadeSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_spade_" + n + "_small.png"));
			}
	
			BigJoker = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_big.png"));
			BigJokerSmall = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_big_small.png"));
			SmallJoker = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_small.png"));
			SmallJokerSmall = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_small_small.png"));
			
			cardSelectedBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
							assetManager.open("images/state/lord_card_selected.png")),
						0.64f);
			
			//头像图标
			for(int i = 1; i <= 4; i++){
				farmerBitmaps[i-1] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
							"images/head/lord_classic_playerinfo_icon_farmer_normal_"+i+ ".png")),
					(float)(1.0/3));
			}
			for(int i = 1; i <= 4; i++){
				farmerBitmaps[i+3] = ImageUtils.zoomBitmap(
						BitmapFactory.decodeStream(assetManager.open(
								"images/head/lord_classic_playerinfo_icon_farmer_think_"+i+ ".png")),
						(float)(1.0/3));
			}
			for(int i = 1; i <= 4; i++){
				landlordBitmaps[i-1] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
							"images/head/lord_classic_playerinfo_icon_lord_normal_"+i+ ".png")),
					(float)(1.0/3));
			}
			for(int i = 1; i <= 4; i++){
				landlordBitmaps[i+3] = ImageUtils.zoomBitmap(
						BitmapFactory.decodeStream(assetManager.open(
								"images/head/lord_classic_playerinfo_icon_lord_think_"+i+ ".png")),
						(float)(1.0/3));
			}
			
//			playCardBitmap=ImageUtil.zoomBitmap(
//					BitmapFactory.decodeStream(assetManager.open("images/poke_back_small.png")),
//					(float)(1.0/3));
			
			//抢地主
			gramTextBitmap[0]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
							assetManager.open("images/string_bu.png")),(float)(1.0/3));
			gramTextBitmap[1]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_chu.png")),
					(float)(1.0/3));
			gramTextBitmap[2]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_di.png")),
					(float)(1.0/3));
			gramTextBitmap[3]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_jiao.png")),
					(float)(1.0/3));
			gramTextBitmap[4]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_qiang.png")),
					(float)(1.0/3));
			gramTextBitmap[5]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_zhu.png")),
					(float)(1.0/3));
			gramTextBitmap[6]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_bj.png")),
					(float)(1.0/3));
			gramTextBitmap[7]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_bq.png")),
					(float)(1.0/3));
			gramTextBitmap[8]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_cue.png")),
					(float)(1.0/3));
			gramTextBitmap[9]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_jdz.png")),
					(float)(1.0/3));
			gramTextBitmap[10]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_pass.png")),
					(float)(1.0/3));
			gramTextBitmap[11]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_qdz.png")),
					(float)(1.0/3));
			gramTextBitmap[12]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),
					(float)(1.0/3));
			gramTextBitmap[13]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_repick.png")),
					(float)(1.0/3));
			gramTextBitmap[14]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_send_card.png")),
					(float)(1.0/3));
			gramTextBitmap[15]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/blue_btn.png")),
					(float)(1.0/3));
			gramTextBitmap[16]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/green_btn.png")),
					(float)(1.0/3));
			gramTextBitmap[17]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/red_btn.png")),
					(float)(1.0/3));
			gramTextBitmap[18]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/other_btn_disable.png")),
					(float)(1.0/3));
			
			//牌正面背景
			cardbeforeBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/poke_gb_header.png")),
					(float)(1.0/3));
			
			overGameBitmaps[0]=BitmapFactory.decodeStream(
					assetManager.open("images/text_dizhu_lose.png"));
			overGameBitmaps[1]=BitmapFactory.decodeStream(
					assetManager.open("images/text_dizhu_win.png"));
			overGameBitmaps[2]=BitmapFactory.decodeStream(
					assetManager.open("images/text_nongmin_lose.png"));
			overGameBitmaps[3]=BitmapFactory.decodeStream(
					assetManager.open("images/text_nongmin_win.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initMiddleBitMap(){

		
		try {
//			background=ImageUtil.zoomBitmap(
//					BitmapFactory.decodeStream(assetManager.open("images/state/lord_play_bg.png")),
//					(float)(3/4));
			background=
					BitmapFactory.decodeStream(assetManager.open("images/state/lord_play_bg.png"));
			
			initHeadBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/logo_unknown.png")),
					(float)(2.0/3));
			exitBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/game_icon_exit.png")),
					(float)(2.0/3));
			setupBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/game_icon_setting.png")),
					(float)(2.0/3));
			cardBgBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/poke_back_header.png")),
					(float)(2.0/3));
			prepareButtontextBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),
					(float)(2.0/3));
			prepareButtonupbgBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/big_green_btn.png")),
					(float)(2.0/3));
			prepareButtonbgBitmap=prepareButtonupbgBitmap;
			prepareButtondownbgBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/big_green_btn_down.png")),
					(float)(2.0/3));
			prepareButtonokBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/ready.png")),
					(float)(2.0/3));
			//数字图片
			for(int i=0;i<10;i++){
				numberBitmaps.add(ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/beishu_"+i+".png")),
					(float)(2.0/3)));
			}
			//倍字图像
			beiBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/game_icon_bei.png")),
					(float)(2.0/3));
			
			for(int n=0;n<10;n++){
				cardNumberBitmaps[n]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/number/otherplayer_cards_num_"+n+".png")),
					(float)(3.001/3));
			}
			
			for(int n=3;n<=15;n++){
				ClubBitmaps[n]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_club_" + n + ".png")),
					0.64f);
				ClubSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_club_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				DiamondBitmaps[n]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_diamond_" + n + ".png")),
					0.64f);
				DiamondSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_diamond_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				HeartBitmaps[n]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_heart_" + n + ".png")),
					0.64f);
				HeartSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_heart_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				SpadeBitmaps[n]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_spade_" + n + ".png")),
					0.64f);
				SpadeSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_spade_" + n + "_small.png"));
			}
	
			BigJoker = ImageUtils.zoomBitmap(BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_big.png")), 0.64f);
			BigJokerSmall = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_big_small.png"));
			SmallJoker = ImageUtils.zoomBitmap(BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_small.png")), 0.64f);
			SmallJokerSmall = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_small_small.png"));
			cardSelectedBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
							assetManager.open("images/state/lord_card_selected.png")),
						0.64f);
			
			
			//头像图标
			for(int i = 1; i <= 4; i++){
				farmerBitmaps[i-1] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
							"images/head/lord_classic_playerinfo_icon_farmer_normal_"+i+ ".png")),
					0.65f);
			}
			for(int i = 1; i <= 4; i++){
				farmerBitmaps[i+3] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
								"images/head/lord_classic_playerinfo_icon_farmer_think_"+i+ ".png")),
					0.65f);
			}
			for(int i = 1; i <= 4; i++){
				landlordBitmaps[i-1] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
							"images/head/lord_classic_playerinfo_icon_lord_normal_"+i+ ".png")),
					0.65f);
			}
			for(int i = 1; i <= 4; i++){
				landlordBitmaps[i+3] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
							"images/head/lord_classic_playerinfo_icon_lord_think_"+i+ ".png")),
					0.65f);
			}
			
			playCardBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/poke_back_small.png")),
					(float)(2.0/3));
			//抢地主
			gramTextBitmap[0]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_bu.png")),
					(float)(2.0/3));
			gramTextBitmap[1]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_chu.png")),
					(float)(2.0/3));
			gramTextBitmap[2]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_di.png")),
					(float)(2.0/3));
			gramTextBitmap[3]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_jiao.png")),
					(float)(2.0/3));
			gramTextBitmap[4]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_qiang.png")),
					(float)(2.0/3));
			gramTextBitmap[5]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/string_zhu.png")),
					(float)(2.0/3));
			gramTextBitmap[6]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_bj.png")),
					(float)(2.0/3));
			gramTextBitmap[7]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_bq.png")),
					(float)(2.0/3));
			gramTextBitmap[8]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_cue.png")),
					(float)(2.0/3));
			gramTextBitmap[9]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_jdz.png")),
					(float)(2.0/3));
			gramTextBitmap[10]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_pass.png")),
					(float)(2.0/3));
			gramTextBitmap[11]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_qdz.png")),
					(float)(2.0/3));
			gramTextBitmap[12]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),
					(float)(2.0/3));
			gramTextBitmap[13]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_repick.png")),
					(float)(2.0/3));
			gramTextBitmap[14]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/text_send_card.png")),
					(float)(2.0/3));
			gramTextBitmap[15]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/blue_btn.png")),
					(float)(2.0/3));
			gramTextBitmap[16]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/green_btn.png")),
					(float)(2.0/3));
			gramTextBitmap[17]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/red_btn.png")),
					(float)(2.0/3));
			gramTextBitmap[18]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/other_btn_disable.png")),
					(float)(2.0/3));
			
			//牌正面背景
			cardbeforeBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/poke_gb_header.png")),
					(float)(2.0/3));
			overGameBitmaps[0]=BitmapFactory.decodeStream(
					assetManager.open("images/text_dizhu_lose.png"));
			overGameBitmaps[1]=BitmapFactory.decodeStream(
					assetManager.open("images/text_dizhu_win.png"));
			overGameBitmaps[2]=BitmapFactory.decodeStream(
					assetManager.open("images/text_nongmin_lose.png"));
			overGameBitmaps[3]=BitmapFactory.decodeStream(
					assetManager.open("images/text_nongmin_win.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initLargeBitMap(){

		getContext().getResources().getDisplayMetrics();
		try {
			background= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/state/lord_play_bg.png")),
					AppUtils.getDisplayDimensions(getContext())[0], AppUtils.getDisplayDimensions(getContext())[1]);
			
			initHeadBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/logo_unknown.png"));
			exitBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/game_icon_exit.png"));
			setupBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/game_icon_setting.png"));
			cardBgBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/poke_back_header.png"));
			prepareButtontextBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/text_ready.png"));
			prepareButtonupbgBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/big_green_btn.png"));
			prepareButtonbgBitmap=prepareButtonupbgBitmap;
			prepareButtondownbgBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/big_green_btn_down.png"));
			prepareButtonokBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/ready.png"));
			//数字图片
			for(int i=0;i<10;i++){
				numberBitmaps.add(
						BitmapFactory.decodeStream(
								assetManager.open("images/beishu_"+i+".png")));
			}
			
			//倍字图像
			beiBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/game_icon_bei.png"));
			
			for(int n=0;n<10;n++){
				cardNumberBitmaps[n]= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open("images/number/otherplayer_cards_num_"+n+".png")),
					(float)(3.0/3));
			}
			
			for(int n=3;n<=15;n++){
				ClubBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_club_" + n + ".png"));
				ClubSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_club_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				DiamondBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_diamond_" + n + ".png"));
				DiamondSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_diamond_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				HeartBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_heart_" + n + ".png"));
				HeartSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_heart_" + n + "_small.png"));
			}
			for(int n=3;n<=15;n++){
				SpadeBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_spade_" + n + ".png"));
				SpadeSmallBitmaps[n]=BitmapFactory.decodeStream(
						assetManager.open("images/card/lord_card_spade_" + n + "_small.png"));
			}
	
			BigJoker = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_big.png"));
			BigJokerSmall = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_big_small.png"));
			SmallJoker = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_small.png"));
			SmallJokerSmall = BitmapFactory.decodeStream(
					assetManager.open("images/card/lord_card_joker_small_small.png"));
			
//			playCardBitmap=BitmapFactory.decodeStream(
//					assetManager.open("images/poke_back_small.png"));
			cardSelectedBitmap= ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(
							assetManager.open("images/state/lord_card_selected.png")),
						0.64f);
			
			
			//头像图标
			for(int i = 1; i <= 4; i++){
				farmerBitmaps[i-1] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
							"images/head/lord_classic_playerinfo_icon_farmer_normal_"+i+ ".png")),
					(float)(1.0/3));
			}
			for(int i = 1; i <= 4; i++){
				farmerBitmaps[i+3] = ImageUtils.zoomBitmap(
						BitmapFactory.decodeStream(assetManager.open(
								"images/head/lord_classic_playerinfo_icon_farmer_think_"+i+ ".png")),
						(float)(1.0/3));
			}
			for(int i = 1; i <= 4; i++){
				landlordBitmaps[i-1] = ImageUtils.zoomBitmap(
					BitmapFactory.decodeStream(assetManager.open(
							"images/head/lord_classic_playerinfo_icon_lord_normal_"+i+ ".png")),
					(float)(1.0/3));
			}
			for(int i = 1; i <= 4; i++){
				landlordBitmaps[i+3] = ImageUtils.zoomBitmap(
						BitmapFactory.decodeStream(assetManager.open(
								"images/head/lord_classic_playerinfo_icon_lord_think_"+i+ ".png")),
						(float)(1.0/3));
			}
			
			//抢地主
			gramTextBitmap[0]=BitmapFactory.decodeStream(
					assetManager.open("images/string_bu.png"));
			gramTextBitmap[1]=BitmapFactory.decodeStream(
					assetManager.open("images/string_chu.png"));
			gramTextBitmap[2]=BitmapFactory.decodeStream(
					assetManager.open("images/string_di.png"));
			gramTextBitmap[3]=BitmapFactory.decodeStream(
					assetManager.open("images/string_jiao.png"));
			gramTextBitmap[4]=BitmapFactory.decodeStream(
					assetManager.open("images/string_qiang.png"));
			gramTextBitmap[5]=BitmapFactory.decodeStream(
					assetManager.open("images/string_zhu.png"));
			gramTextBitmap[6]=BitmapFactory.decodeStream(
					assetManager.open("images/text_bj.png"));
			gramTextBitmap[7]=BitmapFactory.decodeStream(
					assetManager.open("images/text_bq.png"));
			gramTextBitmap[8]=BitmapFactory.decodeStream(
					assetManager.open("images/text_cue.png"));
			gramTextBitmap[9]=BitmapFactory.decodeStream(
					assetManager.open("images/text_jdz.png"));
			gramTextBitmap[10]=BitmapFactory.decodeStream(
					assetManager.open("images/text_pass.png"));
			gramTextBitmap[11]=BitmapFactory.decodeStream(
					assetManager.open("images/text_qdz.png"));
			gramTextBitmap[12]=BitmapFactory.decodeStream(
					assetManager.open("images/text_ready.png"));
			gramTextBitmap[13]=BitmapFactory.decodeStream(
					assetManager.open("images/text_repick.png"));
			gramTextBitmap[14]=BitmapFactory.decodeStream(
					assetManager.open("images/text_send_card.png"));
			gramTextBitmap[15]=BitmapFactory.decodeStream(
					assetManager.open("images/blue_btn.png"));
			gramTextBitmap[16]=BitmapFactory.decodeStream(
					assetManager.open("images/green_btn.png"));
			gramTextBitmap[17]=BitmapFactory.decodeStream(
					assetManager.open("images/red_btn.png"));
			gramTextBitmap[18]=BitmapFactory.decodeStream(
					assetManager.open("images/other_btn_disable.png"));
	
			//牌正面背景
			cardbeforeBitmap=BitmapFactory.decodeStream(
					assetManager.open("images/poke_gb_header.png"));
			overGameBitmaps[0]=BitmapFactory.decodeStream(
					assetManager.open("images/text_dizhu_lose.png"));
			overGameBitmaps[1]=BitmapFactory.decodeStream(
					assetManager.open("images/text_dizhu_win.png"));
			overGameBitmaps[2]=BitmapFactory.decodeStream(
					assetManager.open("images/text_nongmin_lose.png"));
			overGameBitmaps[3]=BitmapFactory.decodeStream(
					assetManager.open("images/text_nongmin_win.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 初始化 加载资源图片
	public void initBitMap() {
		
		switch (screenType) {
		case large:
				initLargeBitMap();
			break;
		case middle:
			initMiddleBitMap();
			break;
		case low:
		default:
			initLowBitMap();
			break;
		
		}
	}
	
	
	public void initData(){
		//清空基本數據
		player1.init();
		player2.init();
		player3.init();
		dizhuList.clear();
		
		initCard();
		washCards();
		
		//倍数
		dizhubei=1;
		grabindex=1;
		
		//游戏状态设置准备状态
		gameStep=GameStep.ready;
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//SurfaceView发生更改触发
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		//SurfaceView创建成功触发
		//获得屏幕高度、宽度
		screen_height = this.getHeight();
		screen_width = this.getWidth();
		System.out.println("屏幕分辨率："+screen_width+"*"+screen_height);
		// 初始化基本信息 加载资源
		initBitMap();
		
		player1Head = farmerBitmaps[0];
		player2Head = farmerBitmaps[0];
		player3Head = farmerBitmaps[0];
		
		//游戏开始
		start=true;
		
		// 开始游戏线程，负责游戏业务流程处理
		//在游戏线程中控制绘图线程通过 rapaint标示
		animationThread = new Thread(new Runnable() {
			int i = 0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					player1Head = farmerBitmaps[i];
					player2Head = landlordBitmaps[i];
					player3Head = farmerBitmaps[i];
					i++;
					if(i > 7){
						i = 0;
					}
					onGameDraw();
					Sleep(500);
				}
			}
		});
		animationThread.start();
		gameThread=new Thread(new Runnable() {
			@Override
			public void run() {
				while(start){
					
					if(gameStep==GameStep.init){
						initData();
					}
					if(gameStep==GameStep.deal){
							//开始发牌
							app.play("Special_Dispatch.mp3");
							handCards();
					}
					//抢地主 开始
					if(gameStep==GameStep.grab){
						//设置第一个叫地主的
						grabDiZhu();
					}
					//斗地主进行中
					if(gameStep==GameStep.landlords){
						HandCardLandlord handCardLandlord = null;
							switch (turn) {
								case 0:
									Sleep(5000-app.getSpeed());
									handCardLandlord = player1.play(currentHandCardLandLord,turnState);
									if(handCardLandlord == null){
										turnState++;
									}else{
										currentHandCardLandLord = handCardLandlord;
										playSound(player1.getGender());
										turnState = 0;
									}
									nextTurn();
									break;
								case 1:
									player2.play(currentHandCardLandLord,turnState);
									break;
								case 2:
									Sleep(5000-app.getSpeed());
									handCardLandlord = player3.play(currentHandCardLandLord,turnState);
									if(handCardLandlord == null){
										turnState++;
									}else{
										currentHandCardLandLord = handCardLandlord;
										playSound(player3.getGender());
										turnState = 0;
									}
									nextTurn();
									break;
								default:
									break;
							}
							//判断输赢
							win();
					}
				}
			}
		});
		gameThread.start();
		
		// 开始绘图线程
		drawThread=new Thread(this);
		drawThread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		start=false;
	}
	
	public void playSound(byte gender){
		String str = "";
		switch (currentHandCardLandLord.getCardTypeLandlord()) {
		case dan:
			if(gender == 1){
				str = "Man_" + currentHandCardLandLord.getWeight() + ".mp3";
			}else if(gender == 0){
				str = "Woman_" + currentHandCardLandLord.getWeight() + ".mp3";
			}
			break;
		case dui:
			if(gender == 1){
				str = "Man_dui" + currentHandCardLandLord.getWeight() + ".mp3";
			}else if(gender == 0){
				str = "Woman_dui" + currentHandCardLandLord.getWeight() + ".mp3";
			}
			break;
		case sanbudai:
			if(gender == 1){
				str = "Man_triple" + currentHandCardLandLord.getWeight() + ".mp3";
			}else if(gender == 0){
				str = "Woman_triple" + currentHandCardLandLord.getWeight() + ".mp3";
			}
			break;
		case sandaiyi:
			if(gender == 1){
				str = "Man_sandaiyi.mp3";
			}else if(gender == 0){
				str = "Woman_sandaiyi.mp3";
			}
			break;
		case sandaidui:
			if(gender == 1){
				str = "Man_sandaiyidui.mp3";
			}else if(gender == 0){
				str = "Woman_sandaiyidui.mp3";
			}
			break;
		case sidaierdan:
			if(gender == 1){
				str = "Man_sidaier.mp3";
			}else if(gender == 0){
				str = "Woman_sidaier.mp3";
			}
			break;
		case sidaierdui:
			if(gender == 1){
				str = "Man_sidailiangdui.mp3";
			}else if(gender == 0){
				str = "Woman_sidailiangdui.mp3";
			}
			break;	
		case danshun5:
		case danshun6:	
		case danshun7:
		case danshun8:
		case danshun9:
		case danshun10:
		case danshun11:
		case danshun12:
			if(gender == 1){
				str = "Man_shunzi.mp3";
			}else if(gender == 0){
				str = "Woman_shunzi.mp3";
			}
			break;
		case shuangshun6:
		case shuangshun8:	
		case shuangshun10:
		case shuangshun12:
		case shuangshun14:
			if(gender == 1){
				str = "Man_liandui.mp3";
			}else if(gender == 0){
				str = "Woman_liandui.mp3";
			}
			break;
		case feiji8:
		case feiji10:
		case feiji12:
		case feiji15:
			if(gender == 1){
				str = "Man_feiji.mp3";
			}else if(gender == 0){
				str = "Woman_feiji.mp3";
			}
			break;
		case zhadan:
			if(gender == 1){
				str = "Man_zhadan.mp3";
			}else if(gender == 0){
				str = "Woman_zhadan.mp3";
			}
			break;
		case huojian:
			if(gender == 1){
				str = "Man_wangzha.mp3";
			}else if(gender == 0){
				str = "Woman_wangzha.mp3";
			}
			break;
		default:
			break;
		}
		app.play(str);
	}
	/**
	 *绘图函数
	 */
	public void onGameDraw(){
		synchronized (surfaceHolder) {
			try {
				//锁定整个视图
				canvas = surfaceHolder.lockCanvas();
				// 画背景
				drawBackground();
				//绘制准备界面
				drawPrepareScreen();
				//绘制关闭、设置按钮等
				drawCommonButton();
				
				//绘制玩家1 玩家3的牌
				drawPlayer1_3();
				//绘制玩家2 自己
				drawPlayer2();
				//绘制抢地主情况
//				drawGrabDiZhu();
				//绘制斗地主出牌情况
				drawDDZStatus();
				//绘制输赢情况
				drawGameOverBitmap();
 				
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
	
	

	//主要绘图线程
	@Override
	public void run() {
		
		onGameDraw();
		
		while (start) {
			if(repaint)
			{
				//绘制界面
				onGameDraw();
				repaint=false;
			}
			//修改50毫秒
			Sleep(50);
		}
	}
	// 画背景
	public void drawBackground() {
			Rect src = new Rect(0, 0, background.getWidth(),background.getHeight());
			Rect dst = new Rect(0, 0, screen_width, screen_height);
			canvas.drawBitmap(background, src, dst, null);
	}
	
	//绘制准备界面
	public void drawPrepareScreen(){
		//绘制玩家头像
		if(gameStep==GameStep.landlords){
			if(player1.isDizhu()){
				if(player1.getGender() == 0)
					canvas.drawBitmap(player1Head, 10, 10, null);
				else if(player1.getGender() == 1)
					canvas.drawBitmap(player1Head, 10, 10, null);
			}else{
				if(player1.getGender() == 0)
					canvas.drawBitmap(player1Head, 10, 10, null);
				else if(player1.getGender() == 1)
					canvas.drawBitmap(player1Head, 10, 10, null);
			}
			if(player2.isDizhu()){
				if(player2.getGender() == 0)
					canvas.drawBitmap(player2Head, 10, screen_height/2, null);
				else if(player2.getGender() == 1)
					canvas.drawBitmap(player2Head, 10, screen_height/2, null);
			}else{
				if(player2.getGender() == 0)
					canvas.drawBitmap(player2Head, 10, screen_height/2, null);
				else if(player2.getGender() == 1)
					canvas.drawBitmap(player2Head, 10, screen_height/2, null);
			}
			
			if(player3.isDizhu()){
				if(player3.getGender() == 0)
					canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
				else if(player3.getGender() == 1)
					canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
			}else{
				if(player3.getGender() == 0)
					canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
				else if(player3.getGender() == 1)
					canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
			}
			
		}else{
			canvas.drawBitmap(initHeadBitmap, 10, 10, null);
			canvas.drawBitmap(initHeadBitmap, 10, screen_height/2, null);
			canvas.drawBitmap(initHeadBitmap, screen_width-10-initHeadBitmap.getWidth(), 10, null);
		}
		
		
		if(gameStep==GameStep.landlords){
			//绘制三张牌
			
			drawThreeBitmap(dizhuList.get(0), screen_width/3+cardbeforeBitmap.getWidth()+5, 10);
			drawThreeBitmap(dizhuList.get(1), screen_width/3+2*cardbeforeBitmap.getWidth()+10, 10);
			drawThreeBitmap(dizhuList.get(2), screen_width/3+3*cardbeforeBitmap.getWidth()+15, 10);
			
		}else{
			//绘制三张牌
			canvas.drawBitmap(cardBgBitmap, screen_width/3+cardBgBitmap.getWidth()+5, 10, null);
			canvas.drawBitmap(cardBgBitmap, screen_width/3+2*cardBgBitmap.getWidth()+10, 10, null);
			canvas.drawBitmap(cardBgBitmap, screen_width/3+3*cardBgBitmap.getWidth()+15, 10, null);
		}
	
		
		if(gameStep==GameStep.ready){
			//绘制准备按钮
			canvas.drawBitmap(prepareButtonbgBitmap, 
					          screen_width/2-prepareButtonbgBitmap.getWidth()/2, 
					          screen_height/2, 
					          null);
			canvas.drawBitmap(prepareButtontextBitmap, 
						      screen_width/2-prepareButtontextBitmap.getWidth()/2, 
	 screen_height/2+prepareButtonbgBitmap.getHeight()/2-prepareButtontextBitmap.getHeight()/2, 
	 						  null);
		}
		
		if(gameStep==GameStep.ready){
			//准备ok图标
			canvas.drawBitmap(prepareButtonokBitmap, 
					10+initHeadBitmap.getWidth()/2-prepareButtonokBitmap.getWidth()/2, 
					20+initHeadBitmap.getHeight(), 
					null);
			canvas.drawBitmap(prepareButtonokBitmap, 
					screen_width-prepareButtonokBitmap.getWidth()-10-(initHeadBitmap.getWidth()/2-prepareButtonokBitmap.getWidth()/2),
					20+initHeadBitmap.getHeight(), 
					null);
		}
		
		if(dizhubei<10){
			//绘制数字图标 
			canvas.drawBitmap(numberBitmaps.get(dizhubei), 
					screen_width/3+4*cardBgBitmap.getWidth()+30, 
					10+cardBgBitmap.getHeight()/2-numberBitmaps.get(dizhubei).getHeight()/2, 
					null);
			//绘制倍字 beiBitmap
			canvas.drawBitmap(beiBitmap, 
					screen_width/3+4*cardBgBitmap.getWidth()+30+numberBitmaps.get(dizhubei).getWidth(), 
					10+cardBgBitmap.getHeight()/2-beiBitmap.getHeight()/2, null);
			
		}else{
			
			int a=dizhubei/10;
			int b=dizhubei%10;
			//绘制数字图标 
			canvas.drawBitmap(numberBitmaps.get(a), 
					screen_width/3+4*cardBgBitmap.getWidth()+30, 
					10+cardBgBitmap.getHeight()/2-numberBitmaps.get(a).getHeight()/2, 
					null);
			canvas.drawBitmap(numberBitmaps.get(b), 
					screen_width/3+4*cardBgBitmap.getWidth()+30+numberBitmaps.get(a).getWidth(), 
					10+cardBgBitmap.getHeight()/2-numberBitmaps.get(a).getHeight()/2, 
					null);
			//绘制倍字 beiBitmap
			canvas.drawBitmap(beiBitmap, 
					screen_width/3+4*cardBgBitmap.getWidth()+30+numberBitmaps.get(a).getWidth()+numberBitmaps.get(b).getWidth(), 
					10+cardBgBitmap.getHeight()/2-beiBitmap.getHeight()/2, 
					null);
			
		}
		
		
		
	}
	
	/**
	 * 绘制三张牌
	 * @param card
	 * @param left
	 * @param top
	 */
	public void drawThreeBitmap(Card card,int left,int top){
		//cardbeforeBitmap  判斷是不是地主牌
		canvas.drawBitmap(card.getSmallBitmap(), left, top, null);
	}
	
	
	//绘制关闭、设置按钮
	public void drawCommonButton(){
		canvas.drawBitmap(exitBitmap, 
				          screen_width/3-exitBitmap.getWidth()-10, 
				          20, 
				          null);
		canvas.drawBitmap(setupBitmap, 
				          screen_width/3-5, 
				          20, 
				          null);
	}
	
	//线程休眠方法
	public void Sleep(long i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//洗牌
	public void washCards() {
		//打乱顺序
		for(int i=0;i<100;i++){
			Random random=new Random();
			int a=random.nextInt(54);
			int b=random.nextInt(54);
			Card k=card[a];
			card[a]=card[b];
			card[b]=k;
		}
	}
	
	/**
	 * 发牌业务
	 */
	public void handCards(){
		//开始发牌
		System.out.println("开始发牌");
		
		List<Card> player1List = new ArrayList<Card>();
		List<Card> player2List = new ArrayList<Card>();
		List<Card> player3List = new ArrayList<Card>();
		
		for(int i=0;i<54;i++)
		{
			if(i>50)//地主牌
			{
				//放置地主牌
				dizhuList.add(card[i]);
				continue;
			}
			switch ((i)%3) {
				case 0:
					//左边玩家
					player1List.add(card[i]);
					break;
				case 1:
					//我
					player2List.add(card[i]);
					break;
				case 2:
					//右边玩家
					player3List.add(card[i]);
					break;
			}
		}
		Collections.sort(player1List);
		Collections.sort(player2List);
		Collections.sort(player3List);
		for(int i=0;i<17;i++){
			player1.getCards().add(player1List.get(i));
			player2.getCards().add(player2List.get(i));
			player3.getCards().add(player3List.get(i));
			repaint=true;
			Sleep(150);
		}
		System.out.println("结束发牌");
		//重新排序
		player1.sort();
		player2.sort();
		player3.sort();
		System.out.println("排序");
		repaint=true;
		
		//进入抢地主阶段
		gameStep=GameStep.grab;
		
	}
	
	// 玩家1、3的牌
	public void drawPlayer1_3(){
		if(gameStep==GameStep.ready||gameStep==GameStep.init)
		{
			return;
		}
		//发牌图标
//		canvas.drawBitmap(playCardBitmap, 
//				          5, 
//				          20+initHeadBitmap.getHeight(), 
//				          null);
		//绘制玩家1
		int count=player1.getCards().size();
		if(count<10){
			Bitmap nbm=cardNumberBitmaps[count];
			canvas.drawBitmap(nbm, 5+farmerBitmaps[0].getWidth(), 20, null);
		}else{
			int x=count/10;//十位的数字
			int y=count%10;//个位的数字
			Bitmap nbm1=cardNumberBitmaps[x];
			Bitmap nbm2=cardNumberBitmaps[y];
			canvas.drawBitmap(nbm1, 5+farmerBitmaps[0].getWidth(), 20, null);
			canvas.drawBitmap(nbm2, 5+farmerBitmaps[0].getWidth() + nbm1.getWidth(), 20, null);
		}
		
		count=player3.getCards().size();
		if(count<10){
			Bitmap nbm=cardNumberBitmaps[count];
			canvas.drawBitmap(nbm, screen_width-5-nbm.getWidth()-farmerBitmaps[0].getWidth(), 20, null);
		}else{
			int x=count/10;
			int y=count%10;
			Bitmap nbm1=cardNumberBitmaps[x];
			Bitmap nbm2=cardNumberBitmaps[y];
			canvas.drawBitmap(nbm1, screen_width-5-nbm1.getWidth()*2-farmerBitmaps[0].getWidth(), 20, null);
			canvas.drawBitmap(nbm2, screen_width-5-nbm2.getWidth()-farmerBitmaps[0].getWidth(), 20, null);
			
		}
		
	}
	
	/**
	 * 中间对齐绘制牌 玩家2牌情况
	 */
	public void drawPlayer2(){
		if(gameStep==GameStep.ready||gameStep==GameStep.init)
		{
			return;
		}
		int count=player2.getCards().size();
		System.out.println("自己牌数目："+count);
		int w=screen_width/21;
		int span=(screen_width-w*count-(cardSelectedBitmap.getWidth()-w))/2;
		Card card=null;
		for(int i=0;i<count;i++){
			card=player2.getCards().get(i);
			if(card.isClicked()){
				card.setLocationAndSize(
						span+i*w,
						screen_height-cardSelectedBitmap.getHeight()-35,
						cardSelectedBitmap.getWidth(),
						cardSelectedBitmap.getHeight(),
						w);
			}else{
				card.setLocationAndSize(
						span+i*w,
						screen_height-cardSelectedBitmap.getHeight()-15,
						cardSelectedBitmap.getWidth(),
						cardSelectedBitmap.getHeight(),
						w);
			}
			canvas.drawBitmap(card.getBitmap(), card.getX(), card.getY(), null);
//			for(Card selectedCard:selectedCards){
			Paint paint = new Paint();
			paint.setAlpha(60);
			for(int j = 0; j < selectedCards.size(); j ++){
				Card selectedCard = selectedCards.get(j);
				canvas.drawBitmap(cardSelectedBitmap, selectedCard.getX(), selectedCard.getY(), paint);
			}
		}
	}
	
	/**
	 * 抢地主 
	 * dizhuFlag：
	 *  0： 玩家1
	 *  1： 玩家2
	 *  2： 玩家3
	 *  
	 *  1 2 0 1   int[]
	 *  
	 *  grabindex  0,1,2,3... 第几步
	 */
	public void grabDiZhu(){
			Player dizhu = player2;
			player2.setDizhu(true);
			turn = 1;
			//牌进行排序
			//地主牌交个地址
			dizhu.getCards().addAll(dizhuList);
			dizhu.sort();
			repaint=true;
			//设置游戏状态，出牌
			gameStep=GameStep.landlords;
	}
	
	/**
	 * 绘制抢地主情况
	 */
//	public void drawGrabDiZhu(){
//		//是不是抢地主界面
//		if(GameStep.grab==gameStep){
//			//输出玩家1
//			switch (player1.getStatus()) {
//				case jdz:
//					canvas.drawBitmap(gramTextBitmap[3],
//							10+playCardBitmap.getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					canvas.drawBitmap(gramTextBitmap[2],
//							10+playCardBitmap.getWidth()+gramTextBitmap[3].getWidth(),
//							20+initHeadBitmap.getHeight(),null);
//					canvas.drawBitmap(gramTextBitmap[5],
//							10+playCardBitmap.getWidth()+gramTextBitmap[3].getWidth()+gramTextBitmap[2].getWidth(),
//							20+initHeadBitmap.getHeight(),null);
//					break;
//	
//				case qdz:
//					canvas.drawBitmap(gramTextBitmap[4],
//							10+playCardBitmap.getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					canvas.drawBitmap(gramTextBitmap[2],
//							10+playCardBitmap.getWidth()+gramTextBitmap[4].getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					canvas.drawBitmap(gramTextBitmap[5],
//							10+playCardBitmap.getWidth()+gramTextBitmap[4].getWidth()+gramTextBitmap[2].getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					break;
//				case bj:
//					canvas.drawBitmap(gramTextBitmap[0],
//							10+playCardBitmap.getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					canvas.drawBitmap(gramTextBitmap[3],
//							10+playCardBitmap.getWidth()+gramTextBitmap[3].getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					break;
//				case bq:
//					canvas.drawBitmap(gramTextBitmap[0],
//							10+playCardBitmap.getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					canvas.drawBitmap(gramTextBitmap[4],
//							10+playCardBitmap.getWidth()+gramTextBitmap[0].getWidth(),
//							20+initHeadBitmap.getHeight(),
//							null);
//					break;
//				default:
//					
//					break;
//			}
//			
//			//输出玩家2
//			if(grabindex<4&&nextGrab[grabindex]==1){
//				if((grabindex==0&&nextGrab[0]==1)||dizhubei==0){
//					//不叫
//					canvas.drawBitmap(gramTextBitmap[17], 
//							screen_width/2-gramTextBitmap[17].getWidth(), 
//							screen_height/2, 
//							null);
//					canvas.drawBitmap(gramTextBitmap[6], 
//							screen_width/2-gramTextBitmap[17].getWidth()+(gramTextBitmap[17].getWidth()/2-gramTextBitmap[6].getWidth()/2),
//							screen_height/2+(gramTextBitmap[17].getHeight()/2-gramTextBitmap[6].getHeight()/2), null);
//					//叫地主
//					canvas.drawBitmap(gramTextBitmap[15], 
//							screen_width/2+20, 
//							screen_height/2, 
//							null);
//					canvas.drawBitmap(gramTextBitmap[9], 
//							screen_width/2+20+(gramTextBitmap[15].getWidth()/2-gramTextBitmap[9].getWidth()/2), 
//							screen_height/2+(gramTextBitmap[15].getHeight()/2-gramTextBitmap[9].getHeight()/2), 
//							null);
//				}else{
//					//不抢
//					canvas.drawBitmap(gramTextBitmap[17], 
//							screen_width/2-gramTextBitmap[17].getWidth(), 
//							screen_height/2, 
//							null);
//					canvas.drawBitmap(gramTextBitmap[7], 
//							screen_width/2-gramTextBitmap[17].getWidth()+(gramTextBitmap[17].getWidth()/2-gramTextBitmap[7].getWidth()/2), 
//							screen_height/2+(gramTextBitmap[17].getHeight()/2-gramTextBitmap[7].getHeight()/2), 
//							null);
//					//抢地主
//					canvas.drawBitmap(gramTextBitmap[15], 
//							screen_width/2+20, 
//							screen_height/2, 
//							null);
//					canvas.drawBitmap(gramTextBitmap[11], 
//							screen_width/2+20+(gramTextBitmap[15].getWidth()/2-gramTextBitmap[11].getWidth()/2), 
//							screen_height/2+(gramTextBitmap[15].getHeight()/2-gramTextBitmap[11].getHeight()/2), 
//							null);
//
//				}
//			}else{
//				switch (player2.getStatus()) {
//					case jdz:
//						canvas.drawBitmap(gramTextBitmap[3],screen_width/2-gramTextBitmap[3].getWidth(),screen_height/2,null);
//						canvas.drawBitmap(gramTextBitmap[2],screen_width/2,screen_height/2,null);
//						canvas.drawBitmap(gramTextBitmap[5],screen_width/2+gramTextBitmap[2].getWidth(),screen_height/2,null);	
//						break;
//					case qdz:
//						canvas.drawBitmap(gramTextBitmap[4],screen_width/2-gramTextBitmap[4].getWidth(),screen_height/2,null);
//						canvas.drawBitmap(gramTextBitmap[2],screen_width/2,screen_height/2,null);
//						canvas.drawBitmap(gramTextBitmap[5],screen_width/2+gramTextBitmap[2].getWidth(),screen_height/2,null);
//						break;
//					case bj:
//						canvas.drawBitmap(gramTextBitmap[0],screen_width/2-gramTextBitmap[0].getWidth(),screen_height/2,null);
//						canvas.drawBitmap(gramTextBitmap[3],screen_width/2,screen_height/2,null);
//						break;
//					case bq:
//						canvas.drawBitmap(gramTextBitmap[0],screen_width/2-gramTextBitmap[0].getWidth(),screen_height/2,null);
//						canvas.drawBitmap(gramTextBitmap[4],screen_width/2,screen_height/2,null);
//						break;
//					default:
//						
//						break;
//				}
//			}
//			
//			
//			//输出玩家3
//			switch (player3.getStatus()) {
//				case jdz:
//					canvas.drawBitmap(gramTextBitmap[3],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[3].getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
//					canvas.drawBitmap(gramTextBitmap[2],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
//					canvas.drawBitmap(gramTextBitmap[5],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);	
//					break;
//				case qdz:
//					canvas.drawBitmap(gramTextBitmap[4],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[4].getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
//					canvas.drawBitmap(gramTextBitmap[2],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
//					canvas.drawBitmap(gramTextBitmap[5],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
//					break;
//				case bj:
//					canvas.drawBitmap(gramTextBitmap[0],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[3].getWidth()-gramTextBitmap[0].getWidth(),20+initHeadBitmap.getHeight(),null);
//					canvas.drawBitmap(gramTextBitmap[3],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[3].getWidth(),20+initHeadBitmap.getHeight(),null);
//					break;
//				case bq:
//					canvas.drawBitmap(gramTextBitmap[0],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[4].getWidth()-gramTextBitmap[0].getWidth(),20+initHeadBitmap.getHeight(),null);
//					canvas.drawBitmap(gramTextBitmap[4],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[4].getWidth(),20+initHeadBitmap.getHeight(),null);
//					break;
//				default:
//					
//					break;
//			}
//			
//			
//		}
//	}
	
	/**
	 * 玩家1是否确定叫 、抢地主
	 * @return
	 */
	public boolean player1grabdizhu(){
		return new Random().nextBoolean();
	}
	/**
	 * 玩家3是否确定叫 、抢地主
	 * @return
	 */
	public boolean player3grabdizhu(){
		return new Random().nextBoolean();
	}
	
	/**
	 * 绘制扑克牌出牌情况图
	 */
	public void drawDDZStatus(){
		if(gameStep!=GameStep.landlords){
			return;
		}
		player1OutCard();
		player2OutCard();
		player3OutCard();
	}

	/**
	 * 玩家1出牌
	 */
	public void player1OutCard(){
		
		if(player1.isOut()){
			//显示出牌
			Collections.sort(player1.getOutcards());//排序一下
			int count=player1.getOutcards().size();
			if(count==0){
				return;
			}
			System.out.println("palyer1出牌数目："+count);
			int w=(screen_width-2*playCardBitmap.getWidth()-20)/20;
		//	int span=((screen_width-2*playCardBitmap.getWidth()-20)-w*count-(cardSelectedBitmap.getWidth()-w))/2;
			Card card=null;
			for(int i=0;i<count;i++){
				card=player1.getOutcards().get(i);
				card.setLocationAndSize(playCardBitmap.getWidth()+i*w+5,initHeadBitmap.getHeight()+10,cardSelectedBitmap.getWidth(),cardSelectedBitmap.getHeight(),w);
				canvas.drawBitmap(card.getBitmap(), card.getX(), card.getY(), null);
			}
		}else{
			//绘制不出牌图标
			canvas.drawBitmap(gramTextBitmap[0],10+playCardBitmap.getWidth(),20+initHeadBitmap.getHeight(),null);
			canvas.drawBitmap(gramTextBitmap[1],10+playCardBitmap.getWidth()+gramTextBitmap[1].getWidth(),20+initHeadBitmap.getHeight(),null);

		}
		
				
	}
	
	/**
	 * 玩家2出牌情况
	 */
	public void player2OutCard(){
		if(gameStep==GameStep.over){
			return;
		}
		//已经出牌
		if(turn==1){
			//未出牌，显示出牌按钮
			boolean selected = false;
			for(Card card : player2.getCards()){
				if(card.isClicked()){
					selected = true;
				}
			}
			if(!selected){
					//绘制出牌按钮 不可用
					canvas.drawBitmap(gramTextBitmap[18], screen_width-gramTextBitmap[18].getWidth()-10, screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[14],screen_width-gramTextBitmap[18].getWidth()-10+(gramTextBitmap[18].getWidth()-gramTextBitmap[14].getWidth())/2,screen_height-cardSelectedBitmap.getHeight()-30-gramTextBitmap[18].getHeight()+(gramTextBitmap[18].getHeight()-gramTextBitmap[14].getHeight())/2,null);
					//提示按钮
					canvas.drawBitmap(gramTextBitmap[15], screen_width-2*gramTextBitmap[18].getWidth()-20, screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[15].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[8],screen_width-2*gramTextBitmap[18].getWidth()-20+(gramTextBitmap[15].getWidth()-gramTextBitmap[8].getWidth())/2,screen_height-cardSelectedBitmap.getHeight()-30-gramTextBitmap[15].getHeight()+(gramTextBitmap[15].getHeight()-gramTextBitmap[8].getHeight())/2,null);
					//重选按钮
					canvas.drawBitmap(gramTextBitmap[18], screen_width-3*gramTextBitmap[18].getWidth()-30, screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[16].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[13],screen_width-3*gramTextBitmap[18].getWidth()-30+(gramTextBitmap[18].getWidth()-gramTextBitmap[13].getWidth())/2,screen_height-cardSelectedBitmap.getHeight()-30-gramTextBitmap[18].getHeight()+(gramTextBitmap[18].getHeight()-gramTextBitmap[13].getHeight())/2,null);
		    }else{
					//绘制出牌按钮 可用
					canvas.drawBitmap(gramTextBitmap[16], screen_width-gramTextBitmap[16].getWidth()-10, screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[16].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[14],screen_width-gramTextBitmap[16].getWidth()-10+(gramTextBitmap[16].getWidth()-gramTextBitmap[14].getWidth())/2,screen_height-cardSelectedBitmap.getHeight()-30-gramTextBitmap[16].getHeight()+(gramTextBitmap[16].getHeight()-gramTextBitmap[14].getHeight())/2,null);
					//提示按钮
					canvas.drawBitmap(gramTextBitmap[15], screen_width-2*gramTextBitmap[18].getWidth()-20, screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[15].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[8],screen_width-2*gramTextBitmap[18].getWidth()-20+(gramTextBitmap[15].getWidth()-gramTextBitmap[8].getWidth())/2,screen_height-cardSelectedBitmap.getHeight()-30-gramTextBitmap[15].getHeight()+(gramTextBitmap[15].getHeight()-gramTextBitmap[8].getHeight())/2,null);
					//重选按钮
					canvas.drawBitmap(gramTextBitmap[15], screen_width-3*gramTextBitmap[18].getWidth()-30, screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[16].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[13],screen_width-3*gramTextBitmap[18].getWidth()-30+(gramTextBitmap[18].getWidth()-gramTextBitmap[13].getWidth())/2,screen_height-cardSelectedBitmap.getHeight()-30-gramTextBitmap[18].getHeight()+(gramTextBitmap[18].getHeight()-gramTextBitmap[13].getHeight())/2,null);
			}
			//绘制不出按钮
			canvas.drawBitmap(gramTextBitmap[17], screen_width-4*gramTextBitmap[17].getWidth()-40, screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[17].getHeight()-30, null);
			canvas.drawBitmap(gramTextBitmap[10],screen_width-4*gramTextBitmap[17].getWidth()-40+(gramTextBitmap[17].getWidth()-gramTextBitmap[10].getWidth())/2,screen_height-cardSelectedBitmap.getHeight()-30-gramTextBitmap[17].getHeight()+(gramTextBitmap[17].getHeight()-gramTextBitmap[10].getHeight())/2,null);
		}else if(player2.isOut()){
				//绘制出牌情况
				Collections.sort(player2.getOutcards());//排序一下
				int count=player2.getOutcards().size();
				System.out.println("player2出牌数目："+count);
				int w=screen_width/21;
				int span=(screen_width-w*count-(cardSelectedBitmap.getWidth()-w))/2;
				Card card=null;
				for(int i=0;i<count;i++){
					card=player2.getOutcards().get(i);
					card.setLocationAndSize(span+i*w,screen_height-2*cardSelectedBitmap.getHeight()-5,cardSelectedBitmap.getWidth(),cardSelectedBitmap.getHeight(),w);
					canvas.drawBitmap(card.getBitmap(), card.getX(), card.getY(), null);
				}
			}else{
				//绘制不出牌图标
				canvas.drawBitmap(gramTextBitmap[0],screen_width/2-gramTextBitmap[0].getWidth(),screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[0].getHeight()-30,null);
				canvas.drawBitmap(gramTextBitmap[1],screen_width/2,screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[1].getHeight()-30,null);
			}
	}
		
		
	
	/**
	 * 玩家三出牌
	 * 
	 */
	public void player3OutCard(){
		if(gameStep==GameStep.over){
			return;
		}
		if(player3.isOut()){
			//显示出牌
			Collections.sort(player3.getOutcards());
			int count=player3.getOutcards().size();
			System.out.println("player3出牌数目："+count);
			int w=(screen_width-2*playCardBitmap.getWidth()-20)/20;
			int span=screen_width-playCardBitmap.getWidth()-(cardSelectedBitmap.getWidth()-w)-w*count-10;
			Card card=null;
			for(int i=0;i<count;i++){
				card=player3.getOutcards().get(i);
				card.setLocationAndSize(span+i*w+10,initHeadBitmap.getHeight()+10,cardSelectedBitmap.getWidth(),cardSelectedBitmap.getHeight(),w);
				canvas.drawBitmap(card.getBitmap(), card.getX(), card.getY(), null);
			}
		}else{
			//绘制不出牌图标
			canvas.drawBitmap(gramTextBitmap[0],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[0].getWidth()-gramTextBitmap[1].getWidth(),20+initHeadBitmap.getHeight(),null);
			canvas.drawBitmap(gramTextBitmap[1],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[1].getWidth(),20+initHeadBitmap.getHeight(),null);

		}
	}

	//下一个玩家
	public void nextTurn(){
		turn=(turn+1)%3;
		repaint=true;
	}
	
	public void  drawGameOverBitmap(){
		if(gameStep==GameStep.over){
			//overGamecurrBitmap
			canvas.drawBitmap(overGamecurrBitmap, 
					screen_width/2-overGamecurrBitmap.getWidth()/2, 
					screen_height/2-overGamecurrBitmap.getHeight(), 
					null);
		}
	}
	
	//判断谁赢
	public void win(){
		if(player1.getCards().size()==0){
			gameStep=GameStep.over;
			turnState = 2;
			if(player1.isDizhu()){
				overGamecurrBitmap=overGameBitmaps[2];//农民失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
				overGamecurrBitmap=overGameBitmaps[0];//地主失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
			}
			Sleep(2000);
			gameStep=GameStep.init;
			repaint=true;
		}
		
		if(player2.getCards().size()==0){
			gameStep=GameStep.over;
			turnState = 2;
			if(player2.isDizhu()){
				overGamecurrBitmap=overGameBitmaps[2];//农民失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
				overGamecurrBitmap=overGameBitmaps[0];//地主失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
			}
			Sleep(2000);
			gameStep=GameStep.init;
			repaint=true;
		}
		
		if(player3.getCards().size()==0){
			gameStep=GameStep.over;
			turnState = 2;
			if(player3.isDizhu()){
				overGamecurrBitmap=overGameBitmaps[2];//农民失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
				overGamecurrBitmap=overGameBitmaps[0];//地主失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}
			Sleep(2000);
			gameStep=GameStep.init;
			repaint=true;
			
			
		}
		
	}

	

	/**
	 * down 按下
	 * move 移动
	 * up 松开
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//按钮事件
		EventAction eventAction=new EventAction(appContext,this,event);
		
		//牌的监听
		eventAction.setCard();
		
		//只接受按下事件
		if(event.getAction()!=MotionEvent.ACTION_UP){
			return true;
		}
		//监听准备按钮是否按下
		eventAction.setPrepareButtont(
				screen_width/2-prepareButtonbgBitmap.getWidth()/2, 
				screen_height/2, 
				screen_width/2+prepareButtonbgBitmap.getWidth()/2, 
				screen_height/2+prepareButtonbgBitmap.getHeight()/2);
		
		//不抢、不叫按钮
		eventAction.setGrabGameBQButton(
				screen_width/2-gramTextBitmap[17].getWidth(),
				screen_height/2,
				screen_width/2,
				screen_height/2+gramTextBitmap[17].getHeight());
		//抢、叫地主
		eventAction.setGrabGameQDZButton(
				screen_width/2+20,
				screen_height/2,
				screen_width/2+20+gramTextBitmap[15].getWidth(),
				screen_height/2+gramTextBitmap[15].getHeight());
		//出牌 
		eventAction.setlandlordsGameQDZButton(
				screen_width-gramTextBitmap[18].getWidth()-10, 
				screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, 
				screen_width-10, 
				screen_height-cardSelectedBitmap.getHeight()-30);
		//提示按钮
		eventAction.setHintGameQDZButton(
				screen_width-2*gramTextBitmap[18].getWidth()-20, 
				screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, 
				screen_width-20-gramTextBitmap[18].getWidth(), 
				screen_height-cardSelectedBitmap.getHeight()-30);
		//重选按钮
		eventAction.setResetGameQDZButton(
				screen_width-3*gramTextBitmap[18].getWidth()-30, 
				screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, 
				screen_width-30-gramTextBitmap[18].getWidth()*2, 
				screen_height-cardSelectedBitmap.getHeight()-30);
		//不出按钮
		eventAction.setNotLandlordsGameQDZButton(
				screen_width-4*gramTextBitmap[18].getWidth()-40, 
				screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, 
				screen_width-40-gramTextBitmap[18].getWidth()*3, 
				screen_height-cardSelectedBitmap.getHeight()-30);
	
		//监听退出按钮、设置按钮是否按下。
		eventAction.exitButton(
				screen_width/3-exitBitmap.getWidth()-10,
				20,
				screen_width/3-10,
				20+exitBitmap.getHeight());
		eventAction.setButton(
				screen_width/3-5,
				20,
				screen_width/3-5+setupBitmap.getWidth(),
				20+setupBitmap.getHeight());
		
		return true;
		
	}

	public void showIllegal() {
		// TODO Auto-generated method stub
		
	}

	public void showUnResonable() {
		// TODO Auto-generated method stub
		
	}
	
	
}
