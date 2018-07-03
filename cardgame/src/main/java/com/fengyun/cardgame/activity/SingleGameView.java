package com.fengyun.cardgame.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.fengyun.utils.ImageUtils;
import com.fengyun.cardgame.app.MainApplication;
import com.fengyun.cardgame.bean.BotPlayer;
import com.fengyun.cardgame.bean.Card;
import com.fengyun.cardgame.bean.CardTypeLandlord;
import com.fengyun.cardgame.bean.GameButton;
import com.fengyun.cardgame.bean.GameStep;
import com.fengyun.cardgame.bean.Gender;
import com.fengyun.cardgame.bean.HandCardLandlord;
import com.fengyun.cardgame.bean.Player;
import com.fengyun.cardgame.bean.RealPlayer;
import com.fengyun.cardgame.util.DialogUtils;
import com.fengyun.cardgame.util.LandlordLogic;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * 单机游戏视图
 * 继承SurfaceView，实现SurfaceHolder.Callback和Runnable
 */
public class SingleGameView extends SurfaceView implements SurfaceHolder.Callback {

	//获得MainApplication实例
	private MainApplication app = MainApplication.getInstance();
	
	//获得Asset资源管理器
	private AssetManager assetManager;

	//视图控制器
	private SurfaceHolder surfaceHolder = null;
	
	//画布
	private Canvas canvas=null;
	
	// 屏幕宽度和高度
	private int screen_height = 0;
	private	 int screen_width = 0;
	
	//游戏线程
	private Thread gameThread = null;
	
	private Thread animationThread = null;
	
	//绘图线程
	private Thread drawThread = null;

	private Object lock = new Object();
	////轮流
	private int gameturn = -1;
	private boolean firstTurn1 = true;
	private boolean firstTurn3 = true;

	public int turnState = 2;
	
	public int hintIndex = 0;
	
	public HandCardLandlord currentHandCardLandLord;
	
	public float esx, esy;
	
	public List<Card> selectedCards = new ArrayList<>();
	
	public int preSelected = -1;

	//是否重绘
	public Boolean repaint = false;
	
	//是否开始游戏
	private Boolean start = false;
	
	//上下文
	private Context appContext = null;
	
	//游戏状态
	public GameStep gameStep = GameStep.init;
	
	// 牌对象
	private Card card[] = new Card[54];
	
	//地主牌
	private List<Card> dizhuList=new ArrayList<>();
	
	//玩家信息 ：左边 电脑 ，中间 自己 ， 右边 电脑
	public BotPlayer player1=new BotPlayer(1,Gender.famale, 1);
	public RealPlayer player2=new RealPlayer(2, Gender.male, 2);//设置自己
	public BotPlayer player3=new BotPlayer(3,Gender.male,3);
	
	//下注倍数
	public	int dizhubei=1;
	
	//轮到谁抢地主
	public int grabindex=0;
	
	private Bitmap[] ClubBitmaps = new Bitmap[16];
	private Bitmap[] ClubOutBitmaps = new Bitmap[16];
	private Bitmap[] ClubSmallBitmaps = new Bitmap[16];

	private Bitmap[] DiamondBitmaps = new Bitmap[16];
	private Bitmap[] DiamondOutBitmaps = new Bitmap[16];
	private Bitmap[] DiamondSmallBitmaps = new Bitmap[16];

	private Bitmap[] HeartBitmaps = new Bitmap[16];
	private Bitmap[] HeartOutBitmaps = new Bitmap[16];
	private Bitmap[] HeartSmallBitmaps = new Bitmap[16];

	private Bitmap[] SpadeBitmaps = new Bitmap[16];
	private Bitmap[] SpadeOutBitmaps = new Bitmap[16];
	private Bitmap[] SpadeSmallBitmaps = new Bitmap[16];
	
	private Bitmap BigJoker = null;
	private Bitmap BigJokerOut = null;
	private Bitmap BigJokerSmall = null;

	private Bitmap SmallJoker = null;
	private Bitmap SmallJokerOut = null;
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

	//牌背景图标
	private Bitmap threeCardBgBitmap=null;

	//准备好图标
	public Bitmap prepareOkBitmap =null;

	public Bitmap noOutCardLeftBitmap = null;
	public Bitmap noOutCardRightBitmap = null;

	public Bitmap noGrabLeftBitmap = null;
	public Bitmap noGrabRightBitmap = null;

	//数字图标
	public List<Bitmap> numberBitmaps=new ArrayList<>();
		
	//倍字图像
	public Bitmap beiBitmap = null;

	//游戏结束的谁输谁赢祝贺
	private Bitmap[] overGameBitmaps=new Bitmap[4];
	
	private Bitmap overGamecurrBitmap = null;

	private Bitmap exitBitmap;
	private Bitmap settingBitmap;
	
	private Bitmap prepareBitmap;
	private Bitmap noOutBitmap;
	private Bitmap outBitmap;
	private Bitmap hintBitmap;
	private Bitmap noAffordBitmap;

	private Bitmap prepareBgBitmap;
	private Bitmap noOutDownBitmap;
	private Bitmap outDownBitmap;
	private Bitmap hintDownBitmap;
	private Bitmap noAffordDownBitmap;

    private Bitmap noOutDisableBitmap;
    private Bitmap outDisableBitmap;
    private Bitmap hintDisableBitmap;
    private Bitmap noAffordDisableBitmap;

	private GameButton exitButton;
	private GameButton settingButton;

	private GameButton prepareButton;

	private GameButton noOutButton;
	private GameButton hintButton;
	private GameButton outButton;
	private GameButton noAffordButton;

	/**
	 * 构造方法
	 * @param context 上下文
	 */
	public SingleGameView(Context context) {
		super(context);
		assetManager=context.getAssets();
		//当前视图获得焦点
		setFocusable(true);
		//赋值
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
							                   ClubBitmaps[i],ClubOutBitmaps[i], ClubSmallBitmaps[i]);
					break;
				case 1:
					card[(i-3)*4+j] = new Card("c"+(i-3)*4+j, i, 
							                   DiamondBitmaps[i], DiamondOutBitmaps[i], DiamondSmallBitmaps[i]);
					break;
				case 2:
					card[(i-3)*4+j] = new Card("c"+(i-3)*4+j, i, 
							                   HeartBitmaps[i], HeartOutBitmaps[i],  DiamondSmallBitmaps[i]);
					break;
				case 3:
					card[(i-3)*4+j] = new Card("c"+(i-3)*4+j, i, 
							                   SpadeBitmaps[i], SpadeOutBitmaps[i], SpadeSmallBitmaps[i]);
					break;
				}
			}
		}
		
		//最后小王，大王
		card[52]=new Card("c"+54,16, SmallJoker,SmallJokerOut, SmallJokerSmall);
		card[53]=new Card("c"+53,17, BigJoker, BigJokerOut,BigJokerSmall);
	}

	public void initBitMap(){

		try {
			//游戏界面背景
            Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open("images/scene/lord_play_bg.png"));
			background = ImageUtils.zoomBitmapByHeight(bitmap,screen_height);

//            bitmap = BitmapFactory.decodeStream(assetManager.open("images/logo_unknown.png"));
//            initHeadBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int) (screen_height * 0.15));

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/poke_back_header.png"));
            threeCardBgBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/ready.png"));
            prepareOkBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));

			//数字图片
			for(int i=0;i<10;i++){
                bitmap = BitmapFactory.decodeStream(assetManager.open("images/beishu_"+i+".png"));
                Bitmap numberBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
				numberBitmaps.add(numberBitmap);
			}
			
			//倍字图像
            bitmap = BitmapFactory.decodeStream(assetManager.open("images/game_icon_bei.png"));
            beiBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			for(int n=0;n<10;n++){
                bitmap = BitmapFactory.decodeStream(assetManager.open("images/number/otherplayer_cards_num_"+n+".png"));
                
                Bitmap numberBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
                cardNumberBitmaps[n] = numberBitmap;
			}
			
			for(int n=3;n<=15;n++){
                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_club_" + n + ".png"));
                Bitmap clubBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35));
                ClubBitmaps[n] = clubBitmap;

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_club_" + n + ".png"));
                Bitmap clubOutBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35 * 0.65));
                ClubOutBitmaps[n] = clubOutBitmap;

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_club_" + n + "_small.png"));
                Bitmap clubSmallBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
                ClubSmallBitmaps[n] = clubSmallBitmap;

			}
			for(int n=3;n<=15;n++){

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_diamond_" + n + ".png"));
                Bitmap clubBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35));
                DiamondBitmaps[n] = clubBitmap;

				bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_diamond_" + n + ".png"));
				Bitmap clubOutBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35 * 0.65));
				DiamondOutBitmaps[n] = clubOutBitmap;

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_diamond_" + n + "_small.png"));
                Bitmap clubSmallBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
                DiamondSmallBitmaps[n] = clubSmallBitmap;
			}
			for(int n=3;n<=15;n++){

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_heart_" + n + ".png"));
                Bitmap clubBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35));
                HeartBitmaps[n] = clubBitmap;

				bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_heart_" + n + ".png"));
				Bitmap clubOutBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35 * 0.65));
				HeartOutBitmaps[n] = clubOutBitmap;

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_heart_" + n + "_small.png"));
                Bitmap clubSmallBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
                HeartSmallBitmaps[n] = clubSmallBitmap;

			}
			for(int n=3;n<=15;n++){

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_spade_" + n + ".png"));
                Bitmap clubBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35));
                SpadeBitmaps[n] = clubBitmap;

				bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_spade_" + n + ".png"));
				Bitmap clubOutBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35 * 0.65));
				SpadeOutBitmaps[n] = clubOutBitmap;

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_spade_" + n + "_small.png"));
                Bitmap clubSmallBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
                SpadeSmallBitmaps[n] = clubSmallBitmap;
			}

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_joker_big.png"));
            Bitmap bigJokerBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35));
            BigJoker = bigJokerBitmap;

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_joker_big.png"));
			Bitmap bigJokerOutBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35 * 0.65));
			BigJokerOut = bigJokerOutBitmap;

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_joker_big_small.png"));
            Bitmap bigJokerSmallBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
            BigJokerSmall = bigJokerSmallBitmap;

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_joker_small.png"));
			Bitmap smallJokerBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35));
			SmallJoker = smallJokerBitmap;

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_joker_small.png"));
			Bitmap smallJokerOutBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35 * 0.65));
			SmallJokerOut = smallJokerOutBitmap;

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/card/lord_card_joker_small_small.png"));
            Bitmap smallJokerSmallBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
            SmallJokerSmall = smallJokerSmallBitmap;

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/state/lord_card_selected.png"));
            bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.35));
            cardSelectedBitmap = bitmap;

			//头像图标
			for(int i = 1; i <= 4; i++){
                bitmap = BitmapFactory.decodeStream(assetManager.open("images/head/lord_classic_playerinfo_icon_farmer_normal_"+i+ ".png"));
                bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.3));
                farmerBitmaps[i-1] = bitmap;
			}
			for(int i = 1; i <= 4; i++){

                bitmap = BitmapFactory.decodeStream(assetManager.open("images/head/lord_classic_playerinfo_icon_farmer_think_"+i+ ".png"));
                bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.3));
                farmerBitmaps[i+3] = bitmap;
			}
			for(int i = 1; i <= 4; i++){
                bitmap = BitmapFactory.decodeStream(assetManager.open("images/head/lord_classic_playerinfo_icon_lord_normal_"+i+ ".png"));
                bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.3));
                landlordBitmaps[i-1] = bitmap;
			}
			for(int i = 1; i <= 4; i++){
                bitmap = BitmapFactory.decodeStream(assetManager.open("images/head/lord_classic_playerinfo_icon_lord_think_"+i+ ".png"));
                bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int) (0.3 * screen_height));
                landlordBitmaps[i+3] = bitmap;
			}

			//牌正面背景
            bitmap = BitmapFactory.decodeStream(assetManager.open("images/poke_gb_header.png"));
            bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_speak_pass_left.png"));
			bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			noOutCardLeftBitmap = bitmap;

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_speak_pass_right.png"));
			bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			noOutCardRightBitmap = bitmap;

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_speak_no_call_left.png"));
			bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			noGrabLeftBitmap = bitmap;

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_speak_no_call_right.png"));
			bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			noGrabRightBitmap = bitmap;

            //牌正面背景
            bitmap = BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_lose.png"));
            bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.20));
            overGameBitmaps[0] = bitmap;

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_win.png"));
            bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.20));
            overGameBitmaps[1] = bitmap;

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_lose.png"));
            bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.20));
            overGameBitmaps[2] = bitmap;

            bitmap = BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_win.png"));
            bitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.20));
            overGameBitmaps[3] = bitmap;

			;bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/big_blue_btn.png"));
			 prepareBgBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));;
			 bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/text_ready.png"));
			 prepareBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			 prepareButton = new GameButton(screen_width * 0.45f, screen_height * 0.55f,
					 prepareBitmap, prepareBgBitmap, prepareBitmap, SingleGameView.this) {
				 @Override
				 protected void doAction() {
					 if(gameView.gameStep!= GameStep.ready){
						 return;
					 }
					 MainApplication.getInstance().play("SpecOk.ogg");
					 System.out.println("准备按钮被点击 :");
					 gameView.gameStep=GameStep.deal;
					 gameView.repaint=true;
				 }
			 };

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/game_icon_exit.png"));
			exitBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			exitButton = new GameButton(screen_width * 0.3f, screen_height * 0.02f, exitBitmap,exitBitmap,exitBitmap, SingleGameView.this) {
				@Override
				protected void doAction() {
					MainApplication.getInstance().play("SpecOk.ogg");
					System.out.println("退出按钮被点击：");
					DialogUtils.exitGameDialog(this.gameView.appContext);
				}
			};
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/game_icon_setting.png"));
			settingBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.10));
			settingButton = new GameButton(screen_width * 0.38f
			, screen_height * 0.02f, settingBitmap, settingBitmap, settingBitmap,SingleGameView.this) {
				@Override
				protected void doAction() {
					MainApplication.getInstance().play("SpecOk.ogg");
					System.out.println("设置按钮被点击");
					DialogUtils.setupDialog(this.gameView.appContext,2);
				}
			};
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_pass_n.png"));
			noOutBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_pass_d.png"));
			noOutDownBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_pass_disable.png"));
			noOutDisableBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			noOutButton = new GameButton(screen_width * 0.27f, screen_height * 0.48f,
					noOutBitmap, noOutDownBitmap,noOutDisableBitmap, SingleGameView.this) {
				@Override
				protected void doAction() {
					if(gameView.gameStep!=GameStep.landlords){
						return;
					}
					MainApplication.getInstance().play("SpecOk.ogg");
					gameView.player2.setOut(false);
					Random random = new Random();
					playSound(player2.getGender(), "buyao" + (random.nextInt(4) + 1));
					gameView.turnState ++;
					gameView.nextTurn();
				}
			};

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_prompt_n.png"));
			hintBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_prompt_d.png"));
			hintDownBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			hintButton = new GameButton(screen_width * 0.27f + noOutBitmap.getWidth(),// + screen_width * 0.02f,
					screen_height * 0.48f, hintBitmap, hintDownBitmap, hintBitmap, SingleGameView.this) {
				@Override
				protected void doAction() {
					if(gameView.gameStep!=GameStep.landlords){
						return;
					}

					MainApplication.getInstance().play("SpecOk.ogg");
					List<Card> cards=gameView.player2.getCards();
					gameView.player2.getOutcards().clear();
					gameView.player2.clearClick();
					//需智能算法
					if(cards.size()>0){
						HandCardLandlord handCardLandlord = null;
						if(gameView.turnState == 2){
							handCardLandlord = HandCardLandlord.getLowestHandCardLandlord(cards);
						}else{
							List<HandCardLandlord> handCardLandlordList = HandCardLandlord.getHintList(
									cards, gameView.currentHandCardLandLord);
							if(handCardLandlordList.size() > 0){
								if(gameView.hintIndex > handCardLandlordList.size() - 1){
									gameView.hintIndex = 0;
								}
								handCardLandlord = handCardLandlordList.get(
										handCardLandlordList.size() - 1 - gameView.hintIndex);
								gameView.hintIndex ++;
							}
						}
						if(handCardLandlord != null){
							for(int i = 0; i < cards.size(); i ++){
								for(int j = 0; j < handCardLandlord.getList().size(); j++){
									if(cards.get(i) == handCardLandlord.getList().get(j)){
										cards.get(i).setClicked(true);
									}
								}
							}
						}
					}
					gameView.repaint=true;
				}
			};

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_product_n.png"));
			outBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_product_d.png"));
			outDownBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_product_disable.png"));
			outDisableBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			outButton = new GameButton(screen_width * 0.27f + noOutBitmap.getWidth() + hintBitmap.getWidth(), //+ screen_width * 0.02f,
					screen_height * 0.48f, outBitmap, outDownBitmap, outDisableBitmap,SingleGameView.this) {
				@Override
				protected void doAction() {
					if(gameView.gameStep!=GameStep.landlords){
						return;
					}

					MainApplication.getInstance().play("SpecOk.ogg");
					List<Card> cards=gameView.player2.getCards();
					gameView.player2.getOutcards().clear();

					for(Card card:cards){
						if(card.isClicked()){
							System.out.println("选择牌为:"+card.getName());
							gameView.player2.getOutcards().add(card);
						}
					}
					if(LandlordLogic.getCardTypeLandlord(gameView.player2.getOutcards()) !=
							CardTypeLandlord.error){
						HandCardLandlord selected = new HandCardLandlord(
								gameView.player2.getOutcards(),
								LandlordLogic.getCardTypeLandlord(gameView.player2.getOutcards()),
								LandlordLogic.getWeightLandlord(gameView.player2.getOutcards()));
						if(gameView.turnState == 2){
							gameView.currentHandCardLandLord = selected;
							gameView.playSound(gameView.player2.getGender(),"");
							gameView.player2.getCards().removeAll(gameView.player2.getOutcards());
							gameView.turnState = 0;
							gameView.hintIndex = 0;
							gameView.player2.setOut(true);
							gameView.nextTurn();
						}else if(LandlordLogic.reasonable(gameView.currentHandCardLandLord, selected)){
							gameView.currentHandCardLandLord = selected;
							gameView.playSound(gameView.player2.getGender(), "");
							gameView.player2.getCards().removeAll(gameView.player2.getOutcards());
							gameView.turnState = 0;
							gameView.hintIndex = 0;
							gameView.player2.setOut(true);
							gameView.nextTurn();
						}else{
							gameView.showUnResonable();
						}
					}else{
						gameView.showIllegal();
					}
				}
			};

			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_nobig_n.png"));
			noAffordBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			bitmap = BitmapFactory.decodeStream(assetManager.open("images/button/lord_btn_nobig_d.png"));
			noAffordDownBitmap = ImageUtils.zoomBitmapByHeight(bitmap, (int)(screen_height * 0.15));
			noAffordButton = new GameButton(screen_width * 0.45f, screen_height * 0.48f,
					noAffordBitmap, noAffordDownBitmap, noAffordBitmap,SingleGameView.this) {
				@Override
				protected void doAction() {
					if(gameView.gameStep!=GameStep.landlords){
						return;
					}
					MainApplication.getInstance().play("SpecOk.ogg");
					gameView.player2.setOut(false);
					gameView.turnState ++;
					gameView.nextTurn();
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 初始化 加载资源图片

	
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
//		repaint = true;
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
							switch (gameturn) {
								case 0:
									Sleep(5000-app.getSpeed());
									handCardLandlord = player1.play(currentHandCardLandLord,turnState);
									if(handCardLandlord == null){
										Random random = new Random();
										playSound(player1.getGender(), "buyao" + (random.nextInt(4) + 1));
										turnState++;
									}else{
										currentHandCardLandLord = handCardLandlord;
										playSound(player1.getGender(), "");
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
										Random random = new Random();
										playSound(player3.getGender(), "buyao" + (random.nextInt(4) + 1));
										turnState++;
									}else{
										currentHandCardLandLord = handCardLandlord;
										playSound(player3.getGender(), "");
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
		drawThread=new Thread(new Runnable() {
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
		});
		drawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		start=false;
	}
	
	public void playSound(Gender gender, String string){
		if(string == "") {
			String str = "";
			switch (currentHandCardLandLord.getCardTypeLandlord()) {
				case dan:
					if (gender == Gender.male) {
						str = "Man_" + currentHandCardLandLord.getWeight() + ".mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_" + currentHandCardLandLord.getWeight() + ".mp3";
					}
					break;
				case dui:
					if (gender == Gender.male) {
						str = "Man_dui" + currentHandCardLandLord.getWeight() + ".mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_dui" + currentHandCardLandLord.getWeight() + ".mp3";
					}
					break;
				case sanbudai:
					if (gender == Gender.male) {
						str = "Man_triple" + currentHandCardLandLord.getWeight() + ".mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_triple" + currentHandCardLandLord.getWeight() + ".mp3";
					}
					break;
				case sandaiyi:
					if (gender == Gender.male) {
						str = "Man_sandaiyi.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_sandaiyi.mp3";
					}
					break;
				case sandaidui:
					if (gender == Gender.male) {
						str = "Man_sandaiyidui.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_sandaiyidui.mp3";
					}
					break;
				case sidaierdan:
					if (gender == Gender.male) {
						str = "Man_sidaier.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_sidaier.mp3";
					}
					break;
				case sidaierdui:
					if (gender == Gender.male) {
						str = "Man_sidailiangdui.mp3";
					} else if (gender == Gender.famale) {
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
					if (gender == Gender.male) {
						str = "Man_shunzi.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_shunzi.mp3";
					}
					break;
				case shuangshun6:
				case shuangshun8:
				case shuangshun10:
				case shuangshun12:
				case shuangshun14:
					if (gender == Gender.male) {
						str = "Man_liandui.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_liandui.mp3";
					}
					break;
				case feiji8:
				case feiji10:
				case feiji12:
				case feiji15:
					if (gender == Gender.male) {
						str = "Man_feiji.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_feiji.mp3";
					}
					break;
				case zhadan:
					if (gender == Gender.male) {
						str = "Man_zhadan.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_zhadan.mp3";
					}
					break;
				case huojian:
					if (gender == Gender.male) {
						str = "Man_wangzha.mp3";
					} else if (gender == Gender.famale) {
						str = "Woman_wangzha.mp3";
					}
					break;
				default:
					break;
			}
			app.play(str);
		}else {
			if(gender == Gender.male) {
				app.play("Man_" + string + ".mp3");
			}else if(gender == Gender.famale) {
				app.play("Woman_" + string + ".mp3");
			}
		}
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
				drawHead();
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
	
	// 画背景
	public void drawBackground() {
			Rect src = new Rect(0, 0, background.getWidth(),background.getHeight());
			Rect dst = new Rect(0, 0, screen_width, screen_height);
			canvas.drawBitmap(background, src, dst, null);
	}

	public void onGameDrawHead(){
		synchronized (surfaceHolder) {
			try {
				//锁定整个视图
				canvas = surfaceHolder.lockCanvas();
				drawHead();

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

	public void drawHead(){
		if(player1.isDizhu()){
			if(player1.getGender() == Gender.famale)
				canvas.drawBitmap(player1Head, 10, 10, null);
			else if(player1.getGender() == Gender.male)
				canvas.drawBitmap(player1Head, 10, 10, null);
		}else{
			if(player1.getGender() == Gender.famale)
				canvas.drawBitmap(player1Head, 10, 10, null);
			else if(player1.getGender() == Gender.male)
				canvas.drawBitmap(player1Head, 10, 10, null);
		}
		if(player2.isDizhu()){
			if(player2.getGender() == Gender.famale)
				canvas.drawBitmap(player2Head, 10, screen_height * 0.35f, null);
			else if(player2.getGender() == Gender.male)
				canvas.drawBitmap(player2Head, 10, screen_height * 0.35f, null);
		}else{
			if(player2.getGender() == Gender.famale)
				canvas.drawBitmap(player2Head, 10, screen_height * 0.35f, null);
			else if(player2.getGender() == Gender.male)
				canvas.drawBitmap(player2Head, 10, screen_height * 0.35f, null);
		}

		if(player3.isDizhu()){
			if(player3.getGender() == Gender.famale)
				canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
			else if(player3.getGender() == Gender.male)
				canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
		}else{
			if(player3.getGender() == Gender.famale)
				canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
			else if(player3.getGender() == Gender.male)
				canvas.drawBitmap(player3Head, screen_width-10-player3Head.getWidth(), 10, null);
		}
	}
	//绘制准备界面
	public void drawPrepareScreen(){
		//绘制玩家头像

		if(gameStep==GameStep.landlords){
			//绘制三张牌
			drawThreeBitmap(dizhuList.get(0), screen_width * 0.45f, screen_height * 0.02f);
			drawThreeBitmap(dizhuList.get(1), screen_width * 0.50f, screen_height * 0.02f);
			drawThreeBitmap(dizhuList.get(2), screen_width * 0.55f, screen_height * 0.02f);
		}else{
			//绘制三张牌
			canvas.drawBitmap(threeCardBgBitmap, screen_width * 0.45f, screen_height * 0.02f, null);
			canvas.drawBitmap(threeCardBgBitmap, screen_width * 0.50f, screen_height * 0.02f, null);
			canvas.drawBitmap(threeCardBgBitmap, screen_width * 0.55f, screen_height * 0.02f, null);
		}
	
		
		if(gameStep==GameStep.ready){
			//绘制准备按钮
			prepareButton.onDraw(canvas, GameButton.NORMAL);
		}
		
		if(gameStep==GameStep.ready){
			//准备ok图标
			canvas.drawBitmap(prepareOkBitmap, screen_width * 0.02f, screen_height * 0.22f, null);
			canvas.drawBitmap(prepareOkBitmap, screen_width * 0.95f, screen_height * 0.22f, null);
		}
		if(dizhubei<10){
			//绘制数字图标
			canvas.drawBitmap(numberBitmaps.get(dizhubei), screen_width * 0.60f,screen_height * 0.02f, null);
			//绘制倍字 beiBitmap
			canvas.drawBitmap(beiBitmap, screen_width * 0.63f, screen_height * 0.02f, null);
		}else{
			int a=dizhubei/10;
			int b=dizhubei%10;
			//绘制数字图标 
			canvas.drawBitmap(numberBitmaps.get(a), screen_width * 0.60f, screen_height * 0.02f, null);
			canvas.drawBitmap(numberBitmaps.get(b), screen_width * 0.63f, screen_height * 0.02f, null);
			//绘制倍字 beiBitmap
			canvas.drawBitmap(beiBitmap, screen_width * 0.66f, screen_height * 0.02f, null);
		}

	}
	
	/**
	 * 绘制三张牌
	 * @param card
	 * @param left
	 * @param top
	 */
	public void drawThreeBitmap(Card card,float left,float top){
		//cardbeforeBitmap  判斷是不是地主牌
		canvas.drawBitmap(card.getSmallBitmap(), left, top, null);
	}
	
	
	//绘制关闭、设置按钮
	public void drawCommonButton(){
		exitButton.onDraw(canvas, GameButton.NORMAL);
		settingButton.onDraw(canvas, GameButton.NORMAL);
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
//		canvas.drawBitmap(BigJoker, 
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
		float w = screen_width * 0.048f;
		float sx = (screen_width - w * count - w + screen_width * 0.01f) / 2;
		Card card = null;
		for(int i=0;i<count;i++){
			card=player2.getCards().get(i);
			if(card.isClicked()){
				card.setLocationAndSize(
						sx+i*w,
						screen_height - BigJoker.getHeight() - screen_height * 0.05f,
						cardSelectedBitmap.getWidth(),
						cardSelectedBitmap.getHeight(), w);
			}else{
				card.setLocationAndSize(
						sx+i*w,
						screen_height - BigJoker.getHeight(),
						cardSelectedBitmap.getWidth(),
						cardSelectedBitmap.getHeight(), w);
			}
			canvas.drawBitmap(card.getBitmap(), card.getX(), card.getY(), null);
//			for(Card selectedCard:selectedCards){
			Paint paint = new Paint();
			paint.setAlpha(150);
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
			synchronized (lock) {
				gameturn = 1;
			}
			//牌进行排序
			//地主牌交个地址
			dizhu.getCards().addAll(dizhuList);
			dizhu.sort();
			repaint=true;
			//设置游戏状态，出牌
			gameStep=GameStep.landlords;
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
			float w = screen_width * 0.032f;
			Card card = null;
			for(int i=0;i<count;i++){
				card=player1.getOutcards().get(i);
				card.setLocationAndSize(farmerBitmaps[0].getWidth() + i * w + 5,screen_height * 0.20f ,BigJokerOut.getWidth(),BigJokerOut.getHeight(),w);
				canvas.drawBitmap(card.getOutBitmap(), card.getX(), card.getY(), null);
			}
		}else{
			//绘制不出牌图标
			if(firstTurn3){
				canvas.drawBitmap(noGrabLeftBitmap, screen_width * 0.10f, screen_height * 0.2f, null);
			}else {
				canvas.drawBitmap(noOutCardLeftBitmap, screen_width * 0.10f, screen_height * 0.2f, null);
			}
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
		if(gameturn == 1){
			//未出牌，显示出牌按钮
			//绘制出牌按钮 不可用
            if(player2.getOutcards() == null || player2.getOutcards().size() == 0) {
                outButton.onDraw(canvas,GameButton.DISABLE);
            }else {
                outButton.onDraw(canvas, GameButton.NORMAL);
            }
			//提示按钮
			hintButton.onDraw(canvas, GameButton.NORMAL);
			//不出按钮
            if(turnState == 2) {
                noOutButton.onDraw(canvas, GameButton.DISABLE);
            }else {
                noOutButton.onDraw(canvas, GameButton.NORMAL);
            }
			//重选按钮

		}else if(player2.isOut()){
				//绘制出牌情况
				Collections.sort(player2.getOutcards());//排序一下
				int count=player2.getOutcards().size();
				System.out.println("player2出牌数目："+count);
				float w = screen_width * 0.032f;
				float sx = screen_width / 2 - w * count / 2;
				Card card = null;
				for(int i=0;i<count;i++){
					card=player2.getOutcards().get(i);
					card.setLocationAndSize(sx + i * w,screen_height * 0.4f,BigJokerOut.getWidth(),BigJokerOut.getHeight(),w);
					canvas.drawBitmap(card.getOutBitmap(), card.getX(), card.getY(), null);
				}
			}else{
				//绘制不出牌图标
				canvas.drawBitmap(noOutCardLeftBitmap,10 + landlordBitmaps[0].getWidth(),screen_height * 0.55f,null);
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
			float w = screen_width * 0.032f;
			float sx = screen_width - farmerBitmaps[0].getWidth() - w * count - BigJokerOut.getWidth() / 2 - screen_width * 0.02f;
			Card card=null;
			for(int i=0;i<count;i++){
				card=player3.getOutcards().get(i);
				card.setLocationAndSize(sx + i * w,screen_height * 0.20f,BigJokerOut.getWidth(),BigJokerOut.getHeight(),w);
				canvas.drawBitmap(card.getOutBitmap(), card.getX(), card.getY(), null);
			}
		}else{
			//绘制不出牌图标
			if(firstTurn3){
				canvas.drawBitmap(noGrabRightBitmap, screen_width * 0.78f, screen_height * 0.2f, null);
			}else {
				canvas.drawBitmap(noOutCardRightBitmap, screen_width * 0.78f, screen_height * 0.2f, null);
			}

		}
	}

	//下一个玩家
	public void nextTurn(){
		if(gameturn == 3 && firstTurn1){
			firstTurn1 = false;
		}
		if(gameturn == 1 && firstTurn3){
			firstTurn3 = false;
		}
		synchronized (lock) {
			gameturn = (gameturn + 1) % 3;
		}
		repaint=true;
	}
	
	public void  drawGameOverBitmap(){
		if(gameStep==GameStep.over){
			//overGamecurrBitmap
			canvas.drawBitmap(overGamecurrBitmap, screen_width * 0.40f, screen_height * 0.45f, null);
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
//				Sleep(1000);
//				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
//				repaint=true;
//				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
//				overGamecurrBitmap=overGameBitmaps[0];//地主失败
//				repaint=true;
//				MainApplication.getInstance().play("MusicEx_Lose.ogg");
//				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
//				MainApplication.getInstance().play("MusicEx_Win.ogg");
			}
			Sleep(2000);
			gameStep=GameStep.init;
			repaint=true;
		}
		
		if(player2.getCards().size()==0){
			gameStep=GameStep.over;
			turnState = 2;
			if(player2.isDizhu()){
//				overGamecurrBitmap=overGameBitmaps[2];//农民失败
//				repaint=true;
//				MainApplication.getInstance().play("MusicEx_Lose.ogg");
//				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
//				overGamecurrBitmap=overGameBitmaps[0];//地主失败
//				repaint=true;
//				MainApplication.getInstance().play("MusicEx_Lose.ogg");
//				Sleep(1000);
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
//				Sleep(1000);
//				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
//				repaint=true;
//				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
//				overGamecurrBitmap=overGameBitmaps[0];//地主失败
//				repaint=true;
//				MainApplication.getInstance().play("MusicEx_Lose.ogg");
//				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
//				MainApplication.getInstance().play("MusicEx_Win.ogg");
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
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

		//牌的监听
        setCard(event);
		
		//只接受按下事件
		if(event.getAction()!=MotionEvent.ACTION_UP){
			return true;
		}
		//监听准备按钮是否按下
		prepareButton.onTouchEvent(event);
		
		//出牌
		outButton.onTouchEvent(event);
		//提示按钮
		hintButton.onTouchEvent(event);
		//重选按钮
//		eventAction.setResetGameQDZButton(
//				screen_width-3*gramTextBitmap[18].getWidth()-30,
//				screen_height-cardSelectedBitmap.getHeight()-gramTextBitmap[18].getHeight()-30,
//				screen_width-30-gramTextBitmap[18].getWidth()*2,
//				screen_height-cardSelectedBitmap.getHeight()-30);
		//不出按钮
		noOutButton.onTouchEvent(event);
	
		//监听退出按钮、设置按钮是否按下。
		exitButton.onTouchEvent(event);
		settingButton.onTouchEvent(event);
		return true;
		
	}

    public void setCard(MotionEvent event){

//		if(gameView.gameStep!=GameStep.landlords){
//			return;
//		}
        List<Card> cards = player2.getCards();
        float x = event.getX();
        float y = event.getY();
        int i = -1;
        for(Card card:cards){
            if((x>card.getX())
                    &&(y>card.getY())
                    &&(x<card.getX()+card.getSw())
                    &&(y<card.getY()+card.getHeight())){
                i = cards.indexOf(card);
            }
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN){
//            esx = x;
//            esy = y;
			if(i >= 0) {
				selectedCards.add(cards.get(i));
				repaint = true;
			}
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(i == -1){
                return;
            }
            if(i == preSelected){
//				gameView.repaint=true;
                return;
            }else{
                preSelected = i;
                selectedCards.add(cards.get(i));
                repaint=true;
            }
        }else{
            System.out.println("点击扑克牌");

            for(Card card:selectedCards){
                if(card.isClicked()) {
                    card.setClicked(false);
                }else {
                    card.setClicked(true);
                }
            }
            selectedCards.clear();
            repaint=true;
            MainApplication.getInstance().play("SpecSelectCard.ogg");
        }
    }

	public void showIllegal() {
		// TODO Auto-generated method stub

	}

	public void showUnResonable() {
		// TODO Auto-generated method stub
		
	}
	
	
}
