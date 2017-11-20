package com.fengyun.cardgame.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HandCardLandlord implements Comparable<HandCardLandlord>{
	
	private List<Card> list = new ArrayList<Card>();
	private CardTypeGouJi cardTypeLandlord;
	private int weight;
	private int count;
	
	public HandCardLandlord(List<Card> list, CardTypeGouJi cardTypeLandlord, int weight){
		this.setList(list); 
		this.setCardTypeLandlord(cardTypeLandlord);
		this.weight = weight;
		this.count = list.size();
	}
	
	public List<Card> getList() {
		return list;
	}

	public void setList(List<Card> list) {
		this.list = list;
	}
	
	public CardTypeGouJi getCardTypeLandlord() {
		return cardTypeLandlord;
	}

	public void setCardTypeLandlord(CardTypeGouJi cardTypeLandlord) {
		this.cardTypeLandlord = cardTypeLandlord;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "list = " + list + " cardTypeLandlord = " + cardTypeLandlord + " weight = " + weight
				+ " count = " + count + "\n";
	}
	public static List<HandCardLandlord> getHintList(List<Card> list, 
													 HandCardLandlord handCardLandlord){
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		switch (handCardLandlord.getCardTypeLandlord()) {
		case dan:
			handCardLandlordList = getHintDan(list,handCardLandlord);
			break;
		case dui:
			handCardLandlordList = getHintDui(list,handCardLandlord);
			break;
		case sanbudai:
			handCardLandlordList = getHintSan(list,handCardLandlord);
			break;
		case sandaiyi:
			handCardLandlordList = getHintSandaiyi(list,handCardLandlord);
			break;
		case sandaidui:
			handCardLandlordList = getHintSandaidui(list,handCardLandlord);
			break;
		case sidaierdan:
			handCardLandlordList = getHintSidaierdan(list,handCardLandlord);
			break;
		case sidaierdui:
			handCardLandlordList = getHintSidaierdui(list,handCardLandlord);
			break;
		case feiji8:
			handCardLandlordList = getHintFeiji8(list,handCardLandlord);
			break;
		case feiji10:
			handCardLandlordList = getHintFeiji10(list,handCardLandlord);
			break;
		case feiji12:
			handCardLandlordList = getHintFeiji12(list,handCardLandlord);
			break;
		case feiji15:
			handCardLandlordList = getHintFeiji15(list,handCardLandlord);
			break;
		case danshun5:
		case danshun6:
		case danshun7:
		case danshun8:
		case danshun9:
		case danshun10:
		case danshun11:
		case danshun12:
		case shuangshun6:
		case shuangshun8:
		case shuangshun10:
		case shuangshun12:
		case shuangshun14:
		case sanshun6:
		case sanshun9:
		case sanshun12:
			handCardLandlordList = getHintRank(list,handCardLandlord);
			break;
		default:
			break;
		}
		Collections.sort(handCardLandlordList);
		return handCardLandlordList;
	}
	
	public static HandCardLandlord getLowestHandCardLandlord(List<Card> allCards){
		List<Card> lowestCards = getLowestCards(allCards);
		if(lowestCards.size() == 1){
			List<HandCardLandlord> rankList = getRank(allCards);
			if(rankList.size() > 0){
				if(rankList.get(0).getList().containsAll(lowestCards))
					return rankList.get(0);
			}
			return new HandCardLandlord(lowestCards, 
					CardTypeGouJi.dan, lowestCards.get(0).getName());
		}else if(lowestCards.size() == 2){
			List<HandCardLandlord> rankList = getRankDui(allCards);
			if(rankList.size() > 0){
				if(rankList.get(0).getList().containsAll(lowestCards))
					return rankList.get(0);
			}
			return new HandCardLandlord(lowestCards, 
					CardTypeGouJi.dui, lowestCards.get(0).getName());
		}else if(lowestCards.size() == 3){
			
			List<HandCardLandlord> feiji15 = getFeiji15(allCards);
			for(int i = feiji15.size() - 1; i >= 0; i --){
				if(feiji15.get(i).getList().containsAll(lowestCards)){
					return feiji15.get(i);
				}
			}
			List<HandCardLandlord> feiji12 = getFeiji12(allCards);
			for(int i = feiji12.size() - 1; i >= 0; i --){
				if(feiji12.get(i).getList().containsAll(lowestCards)){
					return feiji12.get(i);
				}
			}
			List<HandCardLandlord> feiji10 = getFeiji10(allCards);
			for(int i = feiji10.size() - 1; i >= 0; i --){
				if(feiji10.get(i).getList().containsAll(lowestCards)){
					return feiji10.get(i);
				}
			}
			List<HandCardLandlord> feiji8 = getFeiji8(allCards);
			for(int i = feiji8.size() - 1; i >= 0; i --){
				if(feiji8.get(i).getList().containsAll(lowestCards)){
					return feiji8.get(i);
				}
			}
			
			List<HandCardLandlord> sandaiyi = getSandaidui(allCards);
			for(int i = sandaiyi.size() - 1; i >= 0; i --){
				if(sandaiyi.get(i).getList().containsAll(lowestCards)){
					return sandaiyi.get(i);
				}
			}
			
			List<HandCardLandlord> sandaidui = getSandaidui(allCards);
			for(int i = sandaidui.size() - 1; i >= 0; i --){
				if(sandaidui.get(i).getList().containsAll(lowestCards)){
					return sandaidui.get(i);
				}
			}
		}
		return null;
	}
	
	public static List<Card> getLowestCards(List<Card> allCards){
		Collections.sort(allCards);
		List<Card> lowestCards = new ArrayList<Card>();
		int lowestValue = allCards.get(allCards.size() - 1).getName();
		for(int i = allCards.size() - 1; i >= 0; i --){
			if(allCards.get(i).getName() == lowestValue){
				lowestCards.add(allCards.get(i));
			}else{
				break;
			}
		}
		return lowestCards;
	}
	
	public static List<HandCardLandlord> getHintDan(List<Card> allCards, 
	          HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getCardTypesBetween(allCards, 1, 2, 1);
		Iterator<HandCardLandlord> iterator= handCardLandlordList.iterator();
		while(iterator.hasNext()){
			if(iterator.next().getWeight() <= handCardLandlord.getWeight()){
				iterator.remove();
			}
		}
//		for(i = 0; i < handCardLandlordList.size(); i++){
//			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight())
//				handCardLandlordList.remove(i);
//		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getCardTypesBetween(allCards, 2, 4, 1);
			iterator= handCardLandlordList.iterator();
			while(iterator.hasNext()){
				if(iterator.next().getWeight() <= handCardLandlord.getWeight()){
					iterator.remove();
				}
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getHintDui(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getCardTypesBetween(allCards, 2, 3, 2);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getCardTypesBetween(allCards, 3, 4, 2);
			for(i = 0; i < handCardLandlordList.size(); i++){
				if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
					handCardLandlordList.remove(i);
					i--;
				}
			} 
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getHintSan(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getCardTypesBetween(allCards, 3, 4, 3);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getCardTypesBetween(allCards, 4, 5, 3);
			for(i = 0; i < handCardLandlordList.size(); i++){
				if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
					handCardLandlordList.remove(i);
					i--;
				}
			} 
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	} 
	
	public static List<HandCardLandlord> getHintSandaiyi(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getSandaiyi(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getHintSandaidui(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getSandaidui(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getHintSidaierdan(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getSidaierdan(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getHintSidaierdui(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getSidaierdui(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getHintFeiji8(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getFeiji8(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getHintFeiji10(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getFeiji10(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getHintFeiji12(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getFeiji12(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getHintFeiji15(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getFeiji15(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getHintZhadan(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getZhadan(allCards);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getHintRank(List<Card> allCards, 
			HandCardLandlord handCardLandlord) {
		// TODO Auto-generated method stub
		List<HandCardLandlord> handCardLandlordList = getAllRank(allCards, 
				handCardLandlord.cardTypeLandlord);
		int i;
		for(i = 0; i < handCardLandlordList.size(); i++){
			if(handCardLandlordList.get(i).getWeight() <= handCardLandlord.getWeight()){
				handCardLandlordList.remove(i);
				i--;
			}
		}
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getZhadan(allCards);
		}
		
		if(handCardLandlordList.size() == 0){
			handCardLandlordList = getHuoJian(allCards);
		}
		
		return handCardLandlordList;
	}
	
//	public static List<HandCardLandlord> getDan(List<Card> allCards){
//		Collections.sort(allCards);
//		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
//		HandCardLandlord handCardLord = null;
//		int num = allCards.get(allCards.size() - 1).getName();
//		int numcount = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				if(numcount == 1){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i+1));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.dan, allCards.get(i+1).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				if(i == 0){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.dan, allCards.get(i).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				
//				numcount = 1;
//			}else{
//				numcount++;
//			}
//		}
//		return handCardLandlordList;
//	}
//	
//	public static List<HandCardLandlord> getDui(List<Card> allCards){
//		Collections.sort(allCards);
//		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
//		HandCardLandlord handCardLord = null;
//		int num = allCards.get(allCards.size() - 1).getName();
//		int numcount = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				if(numcount == 2){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i+1));
//					list.add(allCards.get(i+2));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.dui, allCards.get(i+2).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				if(i == 0 && allCards.get(i).getName() == allCards.get(i+1).getName()){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i));
//					list.add(allCards.get(i+1));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.dui, allCards.get(i).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				numcount = 1;
//			}else{
//				numcount++;
//			}
//		}
//		return handCardLandlordList;
//	}
//	
//	public static List<HandCardLandlord> getSan(List<Card> allCards){
//		Collections.sort(allCards);
//		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
//		HandCardLandlord handCardLord = null;
//		int num = allCards.get(allCards.size() - 1).getName();
//		int numcount = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				if(numcount == 3){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i+1));
//					list.add(allCards.get(i+2));
//					list.add(allCards.get(i+3));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.sanbudai, allCards.get(i+3).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				if(i == 0 && allCards.get(i).getName() == allCards.get(i+1).getName() && numcount == 2){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i));
//					list.add(allCards.get(i+1));
//					list.add(allCards.get(i+2));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.sanbudai, allCards.get(i).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				numcount = 1;
//			}else{
//				numcount++;
//			}
//		}
//		return handCardLandlordList;
//	}
//	
//	public static List<HandCardLandlord> getSi(List<Card> allCards){
//		Collections.sort(allCards);
//		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
//		HandCardLandlord handCardLord = null;
//		int num = allCards.get(allCards.size() - 1).getName();
//		int numcount = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				if(numcount == 4){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i+1));
//					list.add(allCards.get(i+2));
//					list.add(allCards.get(i+3));
//					list.add(allCards.get(i+4));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.zhadan, allCards.get(i+4).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				if(i == 0 && allCards.get(i).getName() == allCards.get(i+1).getName() && numcount == 3){
//					List<Card> list = new ArrayList<Card>();
//					list.add(allCards.get(i));
//					list.add(allCards.get(i+1));
//					list.add(allCards.get(i+2));
//					list.add(allCards.get(i+3));
//					handCardLord = new HandCardLandlord(
//							list, CardTypeLandlord.zhadan, allCards.get(i).getName());
//					handCardLandlordList.add(handCardLord);
//				}
//				numcount = 1;
//			}else{
//				numcount++;
//			}
//		}
//		return handCardLandlordList;
//	}
	
//	public static List<HandCardLandlord> getCardTypes(List<Card> allCards){
//		Collections.sort(allCards);
//		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
//		HandCardLandlord handCardLandlord;
//		List<Card> list = null;
//		int num = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				list = new ArrayList<Card>();
//				list.add(allCards.get(i));
//				handCardLandlord = new HandCardLandlord(list, 
//						CardTypeLandlord.dan, allCards.get(i).getName());
//				handCardLandlordList.add(handCardLandlord);
//			 }
//		}
//		Collections.sort(handCardLandlordList);
//		return handCardLandlordList;
//	} 
	
	public static List<HandCardLandlord> getCardTypesBetween(List<Card> allCards, 
			int greaterOrEqual, int lessValue, int count){
		Collections.sort(allCards);
		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
		HandCardLandlord handCardLandlord;
		List<Card> list = null;
		int num = 0; 
		int numcount = 0;
		for(int i = allCards.size() - 1; i >= 0; i--){
			if(allCards.get(i).getName() != num){
				if(numcount >= greaterOrEqual && numcount < lessValue){
					list = new ArrayList<Card>();
					for(int j = 0; j < count; j++){ 
						list.add(allCards.get(i+j+1));
					}
					handCardLandlord = new HandCardLandlord(list, 
							getCardType(count), list.get(0).getName()); 
					handCardLandlordList.add(handCardLandlord);
				}
				if(greaterOrEqual == 1  && i == 0 && allCards.size() == 1){
					list = new ArrayList<Card>();
					for(int j = 0; j < count; j++){
						list.add(allCards.get(i+j));
					}
					handCardLandlord = new HandCardLandlord(list, 
							getCardType(count), list.get(0).getName()); 
					handCardLandlordList.add(handCardLandlord);
				}else if(greaterOrEqual == 1  && i == 0 
						&& allCards.get(0).getName() != allCards.get(1).getName()){
					list = new ArrayList<Card>();
					for(int j = 0; j < count; j++){
						list.add(allCards.get(i+j));
					}
					handCardLandlord = new HandCardLandlord(list, 
							getCardType(count), list.get(0).getName()); 
					handCardLandlordList.add(handCardLandlord);
				}
				num = allCards.get(i).getName();
				numcount = 1;
			}else{
				numcount++;
				if(i == 0 && numcount >= greaterOrEqual && numcount < lessValue){
					list = new ArrayList<Card>();
					for(int j = 0; j < count; j++){
						list.add(allCards.get(i+j));
					}
					handCardLandlord = new HandCardLandlord(list, 
							getCardType(count), list.get(0).getName()); 
					handCardLandlordList.add(handCardLandlord);
				}
//				if(numcount == 2 && greaterOrEqual == 2 && i == 0 
//						&& allCards.get(0).getName() == allCards.get(1).getName()){
//					list = new ArrayList<Card>();
//					for(int j = 0; j < count; j++){
//						list.add(allCards.get(i+j));
//					}
//					handCardLandlord = new HandCardLandlord(list, 
//							getCardType(count), list.get(0).getName()); 
//					handCardLandlordList.add(handCardLandlord);
//				}
//				if(numcount == 3 && greaterOrEqual == 3 && i == 0 
//						&& allCards.get(0).getName() == allCards.get(1).getName()){
//					list = new ArrayList<Card>();
//					for(int j = 0; j < count; j++){
//						list.add(allCards.get(i+j));
//					}
//					handCardLandlord = new HandCardLandlord(list, 
//							getCardType(count), list.get(0).getName()); 
//					handCardLandlordList.add(handCardLandlord);
//				}
//				if(numcount == 4 && greaterOrEqual == 4 && i == 0 
//						&& allCards.get(0).getName() == allCards.get(1).getName()){
//					list = new ArrayList<Card>();
//					for(int j = 0; j < count; j++){
//						list.add(allCards.get(i+j));
//					}
//					handCardLandlord = new HandCardLandlord(list, 
//							getCardType(count), list.get(0).getName()); 
//					handCardLandlordList.add(handCardLandlord);
//				}
			}
		} 
		Collections.sort(handCardLandlordList);
		return handCardLandlordList;
	}
	
//	public static List<Card> getCardTypesDuiMore(List<Card> allCards){
//		Collections.sort(allCards);
//		List<Card> cardTypes = new ArrayList<Card>();
//		int num = 0;
//		int numcount = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				if(numcount >= 2){
//					for(int j = 0; j < numcount; j++){
//						cardTypes.add(allCards.get(i+j+1));
//					}
//				}
//				numcount = 1;
//			 }else{
//				 numcount++;
//			 }
//		}  
//		return cardTypes;
//	}   
	
//	public static List<HandCardLandlord> getCardTypesDui(List<Card> allCards){
//		Collections.sort(allCards);
//		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
//		HandCardLandlord handCardLandlord;
//		List<Card> duiList = null;
////		List<Card> cardTypes = new ArrayList<Card>();
//		int num = 0;
//		int numcount = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				if(numcount >= 2){
//					duiList = new ArrayList<Card>();
//					for(int j = 0; j < 2; j++){
//						duiList.add(allCards.get(i+j+1));
//					}
//					handCardLandlord = new HandCardLandlord(duiList, 
//							CardTypeLandlord.dui, duiList.get(0).getName()); 
//					handCardLandlordList.add(handCardLandlord);
//				}
//				numcount = 1;
//			}else{
//				numcount++;
//				if(i == 0 && numcount >= 2 
//						&& allCards.get(0).getName() == allCards.get(1).getName()){
//					duiList = new ArrayList<Card>();
//					duiList.add(allCards.get(0));
//					duiList.add(allCards.get(1));
//					handCardLandlord = new HandCardLandlord(duiList, 
//							CardTypeLandlord.dui, duiList.get(0).getName()); 
//					handCardLandlordList.add(handCardLandlord);
//				}
//			}
//		} 
//		Collections.sort(handCardLandlordList);
//		return handCardLandlordList;
//	}   
	
//	public static List<HandCardLandlord> getCardTypesSan(List<Card> allCards){
//		Collections.sort(allCards);
//		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
//		HandCardLandlord handCardLandlord;
//		List<Card> duiList = null;
////		List<Card> cardTypes = new ArrayList<Card>();
//		int num = 0;
//		int numcount = 0;
//		for(int i = allCards.size() - 1; i >= 0; i--){
//			if(allCards.get(i).getName() != num){
//				num = allCards.get(i).getName();
//				if(numcount >= 3){
//					duiList = new ArrayList<Card>();
//					for(int j = 0; j < 3; j++){
//						duiList.add(allCards.get(i+j+1));
//					}
//					handCardLandlord = new HandCardLandlord(duiList, 
//							CardTypeLandlord.sanbudai, duiList.get(0).getName()); 
//					handCardLandlordList.add(handCardLandlord);
//				}
//				numcount = 1;
//			}else{
//				numcount++;
//				if(i == 0 && numcount >= 3 
//						&& allCards.get(0).getName() == allCards.get(1).getName()){
//					duiList = new ArrayList<Card>();
//					duiList.add(allCards.get(0));
//					duiList.add(allCards.get(1));
//					duiList.add(allCards.get(2));
//					handCardLandlord = new HandCardLandlord(duiList, 
//							CardTypeLandlord.dui, duiList.get(0).getName()); 
//					handCardLandlordList.add(handCardLandlord);
//				}
//			}
//		} 
//		Collections.sort(handCardLandlordList);
//		return handCardLandlordList;
//	}   
	

//	public static List<Card> getRankableCardTypes(List<Card> allCards){
//		List<Card> cardTypes = getCardTypes(allCards);
//		Collections.sort(cardTypes);
//		for(int i = cardTypes.size() - 1; i >= 0; i--){
//			if(cardTypes.get(i).getName() > 14){
//				cardTypes.remove(cardTypes.get(i));
//			 }
//		}
//		return cardTypes;
//	}
	
	public static List<HandCardLandlord> getRankableCardTypesHandCard(
			List<HandCardLandlord> handCardLandlordList){
//		List<Card> cardTypes = getCardTypes(allCards);
		Collections.sort(handCardLandlordList);
		for(int i = handCardLandlordList.size() - 1; i >= 0; i--){
			if(handCardLandlordList.get(i).getWeight() > 14){
				handCardLandlordList.remove(handCardLandlordList.get(i));
			}
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getRank(List<Card> allCards){
		Collections.sort(allCards);
		List<HandCardLandlord> handCardLandlordList = getCardTypesBetween(allCards, 1, 5, 1);
		handCardLandlordList = getRankableCardTypesHandCard(handCardLandlordList);
		Collections.sort(handCardLandlordList);
		List<HandCardLandlord> rankList= new ArrayList<HandCardLandlord>();
		HandCardLandlord handCardLandlord;
		List<Card> rank = null;
		int rankcount = 1;
		for(int i = handCardLandlordList.size() - 1; i > 0; i--){
			if(handCardLandlordList.get(i-1).getWeight() - 
					handCardLandlordList.get(i).getWeight() == 1){
				rankcount++;
				 if(rankcount >= 5 && i == 1){
						rank = new ArrayList<Card>();
						for(int j = -1; j < rankcount - 1; j++){
							rank.addAll(handCardLandlordList.get(i + j).getList());
							System.out.println(handCardLandlordList.get(i + j));
						}
						Collections.sort(rank);
						handCardLandlord = new HandCardLandlord(rank, 
						getCardTypeLandlordRank(rank.size()), rank.get(rank.size() - 1).getName());
						rankList.add(handCardLandlord);
				 }
			 }else{ 
				 if(rankcount >= 5){
						rank = new ArrayList<Card>();
						for(int j = 0; j < rankcount; j++){
							rank.addAll(handCardLandlordList.get(i + j).getList());
							System.out.println(handCardLandlordList.get(i + j).getList());
						}
						Collections.sort(rank);
						handCardLandlord = new HandCardLandlord(rank, 
						getCardTypeLandlordRank(rank.size()), rank.get(rank.size() - 1).getName());
						rankList.add(handCardLandlord);
				 }
				 rankcount = 1;
			 }
		}
		return rankList;
	}
	
	public static List<HandCardLandlord> getRankDui(List<Card> allCards){
		Collections.sort(allCards);
		List<HandCardLandlord> handCardLandlordList = getCardTypesBetween(allCards, 2, 5, 2);
		handCardLandlordList = getRankableCardTypesHandCard(handCardLandlordList);
		Collections.sort(handCardLandlordList);
		List<HandCardLandlord> rankList= new ArrayList<HandCardLandlord>();
		HandCardLandlord handCardLandlord;
		List<Card> rank = null;
		int rankcount = 1;
		for(int i = handCardLandlordList.size() - 1; i > 0; i--){
			if(handCardLandlordList.get(i-1).getWeight() - 
					handCardLandlordList.get(i).getWeight() == 1){
				rankcount++;
				 if(rankcount >= 3 && i == 1){
						rank = new ArrayList<Card>();
						for(int j = -1; j < rankcount - 1; j++){
							rank.addAll(handCardLandlordList.get(i + j).getList());
//							System.out.println(handCardLandlordList.get(i + j));
						}
						Collections.sort(rank);
						handCardLandlord = new HandCardLandlord(rank, 
						getCardTypeLandlordRankDui(rank.size()), rank.get(rank.size() - 1).getName());
						rankList.add(handCardLandlord);
				 }
			 }else{
				 if(rankcount >= 3){
						rank = new ArrayList<Card>();
						for(int j = 0; j < rankcount; j++){
							rank.addAll(handCardLandlordList.get(i + j).getList());
//							System.out.println(handCardLandlordList.get(i + j));
						}
						Collections.sort(rank);
						handCardLandlord = new HandCardLandlord(rank, 
						getCardTypeLandlordRankDui(rank.size()), rank.get(rank.size() - 1).getName());
						rankList.add(handCardLandlord);
				 }
				 rankcount = 1;
			 }
		}
		return rankList;
	}
	
	public static List<HandCardLandlord> getRankSan(List<Card> allCards){
		Collections.sort(allCards);
		List<HandCardLandlord> handCardLandlordList = getCardTypesBetween(allCards, 3, 5, 3);
		handCardLandlordList = getRankableCardTypesHandCard(handCardLandlordList);
		List<HandCardLandlord> rankList= new ArrayList<HandCardLandlord>();
		HandCardLandlord handCardLandlord;
		List<Card> rank = null; 
		int rankcount = 1;
		Collections.sort(handCardLandlordList);
		for(int i = handCardLandlordList.size() - 1; i > 0; i--){
			if(handCardLandlordList.get(i-1).getWeight() - 
					handCardLandlordList.get(i).getWeight() == 1){
				rankcount++;
				if(rankcount >= 2 && i == 1){
					rank = new ArrayList<Card>();
					for(int j = -1; j < rankcount - 1; j++){
						rank.addAll(handCardLandlordList.get(i + j).getList());
//							System.out.println(handCardLandlordList.get(i + j));
					}
					Collections.sort(rank);
					handCardLandlord = new HandCardLandlord(rank, 
							getCardTypeLandlordRankSan(rank.size()), rank.get(rank.size() - 1).getName());
					rankList.add(handCardLandlord);
				}
			}else{
				if(rankcount >= 2){
					rank = new ArrayList<Card>();
					for(int j = 0; j < rankcount; j++){
						rank.addAll(handCardLandlordList.get(i + j).getList());
//							System.out.println(handCardLandlordList.get(i + j));
					}
					Collections.sort(rank);
					handCardLandlord = new HandCardLandlord(rank, 
							getCardTypeLandlordRankSan(rank.size()), rank.get(rank.size() - 1).getName());
					rankList.add(handCardLandlord);
				}
				rankcount = 1;
			}
		}
		return rankList;
	}
	
	public static List<HandCardLandlord> getAllRank(List<Card> allCards, 
			CardTypeGouJi cardTypeLandlord){
		switch (cardTypeLandlord) {
		case danshun5:
			return getAllSubRankAll(getRank(allCards), 5);
		case danshun6:
			return getAllSubRankAll(getRank(allCards), 6);
		case danshun7:
			return getAllSubRankAll(getRank(allCards), 7);
		case danshun8:
			return getAllSubRankAll(getRank(allCards), 8);
		case danshun9:
			return getAllSubRankAll(getRank(allCards), 9);
		case danshun10:
			return getAllSubRankAll(getRank(allCards), 10);
		case danshun11:
			return getAllSubRankAll(getRank(allCards), 11);
		case danshun12:
			return getAllSubRankAll(getRank(allCards), 12);
		case shuangshun6:
			return getAllSubRankAllDui(getRankDui(allCards), 6);
		case shuangshun8:
			return getAllSubRankAllDui(getRankDui(allCards), 8);
		case shuangshun10:
			return getAllSubRankAllDui(getRankDui(allCards), 10);
		case shuangshun12:
			return getAllSubRankAllDui(getRankDui(allCards), 12);
		case sanshun6:
			return getAllSubRankAllSan(getRankSan(allCards), 6);
		case sanshun9:
			return getAllSubRankAllSan(getRankSan(allCards), 9);
		case sanshun12:
			return getAllSubRankAllSan(getRankSan(allCards), 12);
		default:
			return null;
		}
	}
	public static List<HandCardLandlord> getAllSubRankAll(
			List<HandCardLandlord> list, int size){
		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
		for(int i=0; i < list.size(); i ++){
			if(getAllSubRankSingle(list.get(i), size) != null)
				handCardLandlordList.addAll(getAllSubRankSingle(list.get(i), size));
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getAllSubRankAllDui(
			List<HandCardLandlord> list, int size){
		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
		for(int i=0; i < list.size(); i ++){
			if(getAllSubRankSingleDui(list.get(i), size) != null)
				handCardLandlordList.addAll(getAllSubRankSingleDui(list.get(i), size));
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getAllSubRankAllSan(
			List<HandCardLandlord> list, int size){
		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
		for(int i=0; i < list.size(); i ++){
			if(getAllSubRankSingleSan(list.get(i), size) != null)
				handCardLandlordList.addAll(getAllSubRankSingleSan(list.get(i), size));
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getAllSubRankSingle(HandCardLandlord handCardLandlord, int size){
		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
		HandCardLandlord newHandCardLandlord;
		List<Card> rank = null;
		if(size <= handCardLandlord.getCount()){
			for(int i=size; i <= handCardLandlord.getCount(); i ++){
				rank = handCardLandlord.getList();
				rank = rank.subList(i-size, i-size + size);
				newHandCardLandlord = new HandCardLandlord(rank, getCardTypeLandlordRank(size), 
						rank.get(rank.size()-1).getName());
				handCardLandlordList.add(newHandCardLandlord);
			}
			return handCardLandlordList;
		}
		return null;
	}
	
	public static List<HandCardLandlord> getAllSubRankSingleDui(HandCardLandlord handCardLandlord, 
			int size){
		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
		HandCardLandlord newHandCardLandlord;
		List<Card> rank = null;
		if(size <= handCardLandlord.getCount()){
			for(int i=size; i <= handCardLandlord.getCount();i=i+2){
				rank = handCardLandlord.getList();
				rank = rank.subList(i-size, i-size + size);
				newHandCardLandlord = new HandCardLandlord(rank, getCardTypeLandlordRankDui(size), 
						rank.get(rank.size()-1).getName());
				handCardLandlordList.add(newHandCardLandlord);
			}
			return handCardLandlordList;
		}
		return null;
	}
	
	public static List<HandCardLandlord> getAllSubRankSingleSan(HandCardLandlord handCardLandlord, 
			int size){
		List<HandCardLandlord> handCardLandlordList= new ArrayList<HandCardLandlord>();
		HandCardLandlord newHandCardLandlord;
		List<Card> rank = null;
		if(size <= handCardLandlord.getCount()){
			for(int i=size; i <= handCardLandlord.getCount();i=i+3){
				rank = handCardLandlord.getList();
				rank = rank.subList(i-size, i-size + size);
				newHandCardLandlord = new HandCardLandlord(rank, getCardTypeLandlordRankSan(size), 
						rank.get(rank.size()-1).getName());
				handCardLandlordList.add(newHandCardLandlord);
			}
			return handCardLandlordList;
		}
		return null;
	}

	public static List<HandCardLandlord> getSandaiyi(List<Card> allCards){
		Collections.sort(allCards);
		List<HandCardLandlord> sanList = getCardTypesBetween(allCards, 3, 4, 3);
		System.out.println(sanList);
		List<HandCardLandlord> danList = getCardTypesBetween(allCards, 1, 2, 1);
		System.out.println(danList);
		List<HandCardLandlord> duiList = getCardTypesBetween(allCards, 2, 3, 2);
		System.out.println(duiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		if(sanList.size() > 0){
			if(danList.size() > 0){
				for(int i = sanList.size() - 1; i >= 0; i--){
					for(int j = danList.size() - 1; j >= 0; j--){
						cardList = new ArrayList<Card>();
						cardList.addAll(sanList.get(i).getList());
						cardList.addAll(danList.get(j).getList());
						handCardLandlord = new HandCardLandlord(cardList, 
								CardTypeGouJi.sandaiyi, sanList.get(i).getWeight());
						handCardLandlordList.add(handCardLandlord);
						
					}
				}
			}
			if(duiList.size() > 0){
				for(int i = sanList.size() - 1; i >= 0; i--){
					for(int j = duiList.size() - 1; j >= 0; j--){
						cardList = new ArrayList<Card>();
						cardList.addAll(sanList.get(i).getList());
						cardList.add(duiList.get(j).getList().get(0));
						handCardLandlord = new HandCardLandlord(cardList, 
								CardTypeGouJi.sandaiyi, sanList.get(i).getWeight());
						handCardLandlordList.add(handCardLandlord);
						
					}
				}
			}
			if(sanList.size() > 1){
				for(int i = sanList.size() - 1; i >= 0; i--){
					for(int j = sanList.size() - 1; j >= 0; j--){
						if(i != j){
							cardList = new ArrayList<Card>();
							cardList.addAll(sanList.get(i).getList());
							cardList.add(sanList.get(j).getList().get(0));
							handCardLandlord = new HandCardLandlord(cardList, 
									CardTypeGouJi.sandaiyi, sanList.get(i).getWeight());
							handCardLandlordList.add(handCardLandlord);
						}
						
					}
				}
			}
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getSidaierdan(List<Card> allCards){ 
		Collections.sort(allCards);
		List<HandCardLandlord> siList = getCardTypesBetween(allCards, 4, 5, 4);
		System.out.println(siList);
		List<HandCardLandlord> daipaiList = getCardTypesBetween(allCards, 1, 4, 1);
		System.out.println(daipaiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		for(int i = 0; i < siList.size(); i++){
			for(int j = 0; j < daipaiList.size(); j++){
				for(int k = 0; k < daipaiList.size(); k++){
					if(daipaiList.get(j).getWeight() > daipaiList.get(k).getWeight()){
						cardList = new ArrayList<Card>();
						cardList.addAll(siList.get(i).getList());
						cardList.addAll(daipaiList.get(j).getList());
						cardList.addAll(daipaiList.get(k).getList());
						handCardLandlord = new HandCardLandlord(cardList, 
								CardTypeGouJi.sidaierdui, siList.get(i).getWeight());
						handCardLandlordList.add(handCardLandlord);
					}
				}
			}
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getFeiji8(List<Card> allCards){ 
		Collections.sort(allCards);
		List<HandCardLandlord> sanshunList = getAllRank(allCards, CardTypeGouJi.sanshun6);
		System.out.println(sanshunList);
		List<HandCardLandlord> daipaiList = getCardTypesBetween(allCards, 1, 4, 1);
		System.out.println(daipaiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		for(int i = 0; i < sanshunList.size(); i++){
			for(int j = 0; j < daipaiList.size(); j++){
				for(int k = 0; k < daipaiList.size(); k++){
					if(daipaiList.get(j).getWeight() > daipaiList.get(k).getWeight() &&
					   daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight() &&
					   daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight()+1 &&
					   daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight() &&
					   daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight()+1){
						cardList = new ArrayList<Card>();
						cardList.addAll(sanshunList.get(i).getList());
						cardList.addAll(daipaiList.get(j).getList());
						cardList.addAll(daipaiList.get(k).getList());
						handCardLandlord = new HandCardLandlord(cardList, 
								CardTypeGouJi.feiji8, sanshunList.get(i).getWeight());
						handCardLandlordList.add(handCardLandlord);
					}
				}
			}
		}
		Collections.sort(handCardLandlordList);
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getFeiji10(List<Card> allCards){ 
		Collections.sort(allCards);
		List<HandCardLandlord> sanshunList = getAllRank(allCards, CardTypeGouJi.sanshun6);
		System.out.println(sanshunList);
		List<HandCardLandlord> daipaiList = getCardTypesBetween(allCards, 2, 4, 2);
		System.out.println(daipaiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		for(int i = 0; i < sanshunList.size(); i++){
			for(int j = 0; j < daipaiList.size(); j++){
				for(int k = 0; k < daipaiList.size(); k++){
					if(daipaiList.get(j).getWeight() > daipaiList.get(k).getWeight() &&
							daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight() &&
							daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight()+1 &&
							daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight() &&
							daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight()+1){
						cardList = new ArrayList<Card>();
						cardList.addAll(sanshunList.get(i).getList());
						cardList.addAll(daipaiList.get(j).getList());
						cardList.addAll(daipaiList.get(k).getList());
						handCardLandlord = new HandCardLandlord(cardList, 
								CardTypeGouJi.feiji10, sanshunList.get(i).getWeight());
						handCardLandlordList.add(handCardLandlord);
					}
				}
			}
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getFeiji12(List<Card> allCards){
		Collections.sort(allCards);
		List<HandCardLandlord> sanshunList = getAllRank(allCards, CardTypeGouJi.sanshun9);
		System.out.println(sanshunList);
		List<HandCardLandlord> daipaiList = getCardTypesBetween(allCards, 1, 4, 1);
		System.out.println(daipaiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		for(int i = 0; i < sanshunList.size(); i++){
			for(int j = 0; j < daipaiList.size(); j++){
			for(int k = 0; k < daipaiList.size(); k++){
			for(int l = 0; l < daipaiList.size(); l++){
				if(daipaiList.get(j).getWeight() > daipaiList.get(k).getWeight() &&
				   daipaiList.get(k).getWeight() > daipaiList.get(l).getWeight() &&
				   daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight() &&
			       daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight()+1 &&
			       daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight()+2 &&
				   daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight() &&
				   daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight()+1 &&
				   daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight()+2 &&
				   daipaiList.get(l).getWeight() != sanshunList.get(i).getWeight() &&
				   daipaiList.get(l).getWeight() != sanshunList.get(i).getWeight()+1 &&
				   daipaiList.get(l).getWeight() != sanshunList.get(i).getWeight()+2){
				   cardList = new ArrayList<Card>();
				   cardList.addAll(sanshunList.get(i).getList());
				   cardList.addAll(daipaiList.get(j).getList());
				   cardList.addAll(daipaiList.get(k).getList());
				   cardList.addAll(daipaiList.get(l).getList());
				   handCardLandlord = new HandCardLandlord(cardList, 
						   CardTypeGouJi.feiji12, sanshunList.get(i).getWeight());
				   handCardLandlordList.add(handCardLandlord);
				  }
			  }
			  }
			  }
		   }
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getFeiji15(List<Card> allCards){ 
		Collections.sort(allCards);
		List<HandCardLandlord> sanshunList = getAllRank(allCards, CardTypeGouJi.sanshun9);
		System.out.println(sanshunList);
		List<HandCardLandlord> daipaiList = getCardTypesBetween(allCards, 2, 4, 2);
		System.out.println(daipaiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		for(int i = 0; i < sanshunList.size(); i++){
			for(int j = 0; j < daipaiList.size(); j++){
				for(int k = 0; k < daipaiList.size(); k++){
					for(int l = 0; l < daipaiList.size(); l++){
						if(daipaiList.get(j).getWeight() > daipaiList.get(k).getWeight() &&
								daipaiList.get(k).getWeight() > daipaiList.get(l).getWeight() &&
								daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight() &&
								daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight()+1 &&
								daipaiList.get(j).getWeight() != sanshunList.get(i).getWeight()+2 &&
								daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight() &&
								daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight()+1 &&
								daipaiList.get(k).getWeight() != sanshunList.get(i).getWeight()+2 &&
								daipaiList.get(l).getWeight() != sanshunList.get(i).getWeight() &&
								daipaiList.get(l).getWeight() != sanshunList.get(i).getWeight()+1 &&
								daipaiList.get(l).getWeight() != sanshunList.get(i).getWeight()+2){
							cardList = new ArrayList<Card>();
							cardList.addAll(sanshunList.get(i).getList());
							cardList.addAll(daipaiList.get(j).getList());
							cardList.addAll(daipaiList.get(k).getList());
							cardList.addAll(daipaiList.get(l).getList());
							handCardLandlord = new HandCardLandlord(cardList, 
									CardTypeGouJi.feiji15, sanshunList.get(i).getWeight());
							handCardLandlordList.add(handCardLandlord);
						}
					}
				}
			}
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getZhadan(List<Card> allCards){
		Collections.sort(allCards);
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		List<HandCardLandlord> siList = getCardTypesBetween(allCards, 4, 5, 4);
		handCardLandlordList.addAll(siList);
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getHuoJian(List<Card> allCards){
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		List<Card> list = new ArrayList<Card>();
		HandCardLandlord handCardLandlord;
		if(allCards.get(0).getName() == 17 && allCards.get(1).getName() == 16){
			list.add(allCards.get(0));
			list.add(allCards.get(1));
			handCardLandlord = new HandCardLandlord(list, CardTypeGouJi.huojian, 16);
			handCardLandlordList.add(handCardLandlord);
		}
		return handCardLandlordList;
	}
	public static List<HandCardLandlord> getSidaierdui(List<Card> allCards){ 
		List<HandCardLandlord> siList = getCardTypesBetween(allCards, 4, 5, 4);
		System.out.println(siList);
		List<HandCardLandlord> daipaiList = getCardTypesBetween(allCards, 2, 4, 2);
		System.out.println(daipaiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		for(int i = 0; i < siList.size() - 1; i++){
			for(int j = 0; j < daipaiList.size(); j++){
				for(int k = 0; k < daipaiList.size(); k++){
					if(daipaiList.get(j).getWeight() > daipaiList.get(k).getWeight()){
						cardList = new ArrayList<Card>();
						cardList.addAll(siList.get(i).getList());
						cardList.addAll(daipaiList.get(j).getList());
						cardList.addAll(daipaiList.get(k).getList());
						handCardLandlord = new HandCardLandlord(cardList, 
								CardTypeGouJi.sidaierdui, siList.get(i).getWeight());
						handCardLandlordList.add(handCardLandlord);
					}
				}
			}
		}
		return handCardLandlordList;
	}
	
	public static List<HandCardLandlord> getSandaidui(List<Card> allCards){
		List<HandCardLandlord> sanList = getCardTypesBetween(allCards, 3, 4, 3);
		System.out.println(sanList);
		List<HandCardLandlord> duiList = getCardTypesBetween(allCards, 2, 3, 2);
		System.out.println(duiList);
		HandCardLandlord handCardLandlord;
		List<Card> cardList;
		List<HandCardLandlord> handCardLandlordList = new ArrayList<HandCardLandlord>();
		if(sanList.size() > 0){
			if(duiList.size() > 0){
				for(int i = sanList.size() - 1; i >= 0; i--){
					for(int j = duiList.size() - 1; j >= 0; j--){
						cardList = new ArrayList<Card>();
						cardList.addAll(sanList.get(i).getList());
						cardList.addAll(duiList.get(j).getList());
						handCardLandlord = new HandCardLandlord(cardList, 
								CardTypeGouJi.sandaidui, sanList.get(i).getWeight());
						handCardLandlordList.add(handCardLandlord);
						
					}
				}
			}
			if(sanList.size() > 1){
				for(int i = sanList.size() - 1; i >= 0; i--){
					for(int j = sanList.size() - 1; j >= 0; j--){
						if(i != j){
							cardList = new ArrayList<Card>();
							cardList.addAll(sanList.get(i).getList());
							cardList.add(sanList.get(j).getList().get(0));
							cardList.add(sanList.get(j).getList().get(1));
							handCardLandlord = new HandCardLandlord(cardList, 
									CardTypeGouJi.sandaidui, sanList.get(i).getWeight());
							handCardLandlordList.add(handCardLandlord);
						}
						
					}
				}
			}
		}
		return handCardLandlordList; 
	}
	public static CardTypeGouJi getCardTypeLandlordRank(int size){
		switch (size) {
		case 5:
			return CardTypeGouJi.danshun5;
		case 6: 
			return CardTypeGouJi.danshun6;
		case 7:
			return CardTypeGouJi.danshun7;
		case 8:
			return CardTypeGouJi.danshun8;
		case 9:
			return CardTypeGouJi.danshun9;
		case 10:
			return CardTypeGouJi.danshun10;
		case 11:
			return CardTypeGouJi.danshun11;
		case 12:
			return CardTypeGouJi.danshun12;
		default:
			return CardTypeGouJi.error;
		}
	}
	
	public static CardTypeGouJi getCardType(int size){
		switch (size) {
		case 1:
			return CardTypeGouJi.dan;
		case 2: 
			return CardTypeGouJi.dui;
		case 3:
			return CardTypeGouJi.sanbudai;
		case 4:
			return CardTypeGouJi.zhadan;
		default:
			return CardTypeGouJi.error;
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordRankDui(int size){
		switch (size) {
		case 6:
			return CardTypeGouJi.shuangshun6;
		case 8:
			return CardTypeGouJi.shuangshun8;
		case 10:
			return CardTypeGouJi.shuangshun10;
		case 12:
			return CardTypeGouJi.shuangshun12;
		case 14:
			return CardTypeGouJi.shuangshun14;
		default:
			return CardTypeGouJi.error;
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordRankSan(int size){
		switch (size) {
		case 6:
			return CardTypeGouJi.sanshun6;
		case 9:
			return CardTypeGouJi.sanshun9;
		case 12:
			return CardTypeGouJi.sanshun12;
		default:
			return CardTypeGouJi.error;
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordRankSi(int size){
		switch (size) {
		case 8:
			return CardTypeGouJi.sishun8;
		case 12:
			return CardTypeGouJi.sishun12;
		default:
			return CardTypeGouJi.error;
		} 
	}
	
	 
	
	@Override
	public int compareTo(HandCardLandlord another) {
		// TODO Auto-generated method stub
		if(this.cardTypeLandlord == CardTypeGouJi.sandaiyi && 
		   another.cardTypeLandlord == CardTypeGouJi.sandaiyi){
			if(this.weight == another.weight){
				return another.list.get(3).getName() - this.list.get(3).getName();
			}else{
				return another.weight - this.weight;
			}
		}else if(this.cardTypeLandlord == CardTypeGouJi.sandaidui &&
				 another.cardTypeLandlord == CardTypeGouJi.sandaidui){
			if(this.weight == another.weight){
				return another.list.get(3).getName() - this.list.get(3).getName();
			}else{
				return another.weight - this.weight;
			}
		}else if(this.cardTypeLandlord == CardTypeGouJi.sidaierdan &&
				 another.cardTypeLandlord == CardTypeGouJi.sidaierdan){
			if(this.weight == another.weight){
				return another.list.get(4).getName() + another.list.get(5).getName() - 
					   this.list.get(4).getName() - this.list.get(5).getName();
			}else{
				return another.weight - this.weight;
			}
		}else if(this.cardTypeLandlord == CardTypeGouJi.sidaierdui &&
				 another.cardTypeLandlord == CardTypeGouJi.sidaierdui){
			if(this.weight == another.weight){
				return another.list.get(4).getName() + another.list.get(6).getName() - 
					   this.list.get(4).getName() - this.list.get(6).getName();
			}else{
				return another.weight - this.weight;
			}
		}else if(this.cardTypeLandlord == CardTypeGouJi.feiji8 &&
				 another.cardTypeLandlord == CardTypeGouJi.feiji8){
			if(this.weight == another.weight){
				return another.list.get(6).getName() + another.list.get(7).getName() - 
					   this.list.get(6).getName() - this.list.get(7).getName();
			}else{
				return another.weight - this.weight;
			}
		}else if(this.cardTypeLandlord == CardTypeGouJi.feiji10 &&
				 another.cardTypeLandlord == CardTypeGouJi.feiji10){
			if(this.weight == another.weight){
				return another.list.get(6).getName() + another.list.get(8).getName() - 
					   this.list.get(6).getName() - this.list.get(8).getName();
			}else{
				return another.weight - this.weight;
			}
		}else if(this.cardTypeLandlord == CardTypeGouJi.feiji12 &&
				 another.cardTypeLandlord == CardTypeGouJi.feiji12){
			if(this.weight == another.weight){
				return another.list.get(9).getName() + another.list.get(10).getName() + 
					   another.list.get(11).getName() - this.list.get(9).getName() - 
					   this.list.get(10).getName() - this.list.get(11).getName();
			}else{
				return another.weight - this.weight;
			}
		}else if(this.cardTypeLandlord == CardTypeGouJi.feiji15 &&
				 another.cardTypeLandlord == CardTypeGouJi.feiji15){
			if(this.weight == another.weight){
				return another.list.get(9).getName() + another.list.get(11).getName() + 
					   another.list.get(13).getName() - this.list.get(9).getName() - 
					   this.list.get(11).getName() - this.list.get(13).getName();
			}else{
				return another.weight - this.weight;
			}
		}else{
			return another.weight - this.weight;
		}
	}
	
	
}
