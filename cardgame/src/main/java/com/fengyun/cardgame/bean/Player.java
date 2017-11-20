package com.fengyun.cardgame.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Player {

	private int id;//玩家编号
	
	private byte gender;
	
	private boolean isBot; //是不是人 ：true，电脑 false
	
	private boolean isDizhu;//是否是地主
	
	private boolean out;//是否出牌或者跟牌
	
	private int turnId;
	
	private List<Card> cards=new ArrayList<Card>();//当前手里的牌
	
	private List<Card> outcards=new ArrayList<Card>();//每次出牌

	public Player(int id, boolean isBot ,byte gender, int turnId){
		this.id=id;
		this.setBot(isBot);
		this.gender = gender;
		init();
	}
	
	public void init(){
		this.setDizhu(false);
		this.setOut(false);
		this.getCards().clear();
		this.getOutcards().clear();
	}
	
	public abstract HandCardLandlord play(HandCardLandlord currentHandCardLandLord, int turnState);
	
	public void sort(){
		Collections.sort(cards);
	}
	
	public void clearClick(){
		for(Card card:getCards()){
			if(card.isClicked()){
				card.setClicked(false);
			}
		}
	}
	public List<Card> getCards() {
		return cards;
	}
	
	public List<Card> getOutcards() {
		return outcards;
	}

	public byte getGender() {
		return gender;
	}

	public void setGender(byte gender) {
		this.gender = gender;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isDizhu() {
		return isDizhu;
	}

	public void setDizhu(boolean isDizhu) {
		this.isDizhu = isDizhu;
	}

	public boolean isBot() {
		return isBot;
	}

	public void setBot(boolean isBot) {
		this.isBot = isBot;
	}

	public boolean isOut() {
		return out;
	}

	public void setOut(boolean out) {
		this.out = out;
	}

	public int getTurnId() {
		return turnId;
	}

	public void setTurnId(int turnId) {
		this.turnId = turnId;
	}
	
	
}
