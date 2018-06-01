package com.fengyun.cardgame.bean;

/**
 * 牌型
　　火箭：即双王（大王和小王），最大的牌。
　　炸弹：四张点数相同的牌，如：7777。
　　单牌：任意一张单牌。
　　对牌：任意两张点数相同的牌。
　　三张：任意三张点数相同的牌，如888。
　　三带一：点数相同的三张牌+一张单牌或一对牌。如：333+6 或 444+99。
　　单顺(连子)：任意五张或五张以上点数相连的牌，如：45678或78910JQK。不包括 2和双王。
　　双顺：三对或更多的连续对牌，如：334455、7788991010JJ。不包括 2 点和双王。
　　三顺：二个或更多的连续三张牌，如：333444 、555666777888。不包括 2 点和双王。
　　飞机带翅膀：三顺＋同数量的单牌或同数量的对牌。如：444555+79 或333444555+7799JJ
　　四带二：四张牌＋两手牌。（注意：四带二不是炸弹）。如：5555＋3＋8 或 4444＋55＋77 。
 * @author Administrator
 *
 */
public enum CardTypeLandlord {
	dan,//单牌。任意一张单牌
	dui,//对牌。任意两张点数相同的牌。
	sanbudai,//3不带。任意三张点数相同的牌，如888。
	sandaiyi,//3带1。点数相同的三张牌+一张单牌。如：333+6
	zhadan,//炸弹。四张点数相同的牌，如：7777。
	sandaidui,//3带2。点数相同的三张牌+一对牌。如：444+99。
	danshun5,
	sidaierdan,//4带2个单，或者一对 ,四张牌＋两手牌。如：5555＋3＋8 。
	sanshun6,
	shuangshun6,
	danshun6,
	danshun7,
	sishun8,
	sidaierdui,//4带2对 四张牌＋两对手牌 4444＋55＋77 
	feiji8,//三顺(飞机)。二个或更多的连续三张牌，如：333444 、555666777888。不包括 2 点和双王。
	shuangshun8,
	danshun8,
	sanshun9,
	danshun9,
	feiji10,
	shuangshun10,
	danshun10,
	danshun11,
	sishun12,
	sanshun12,
	feiji12,
	shuangshun12,
	danshun12,
	shuangshun14,
	feiji15,
	huojian,//火箭 即双王（大王和小王），最大的牌。
	error//不能出牌
}
