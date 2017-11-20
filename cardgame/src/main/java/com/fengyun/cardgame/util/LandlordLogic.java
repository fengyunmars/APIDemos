package com.fengyun.cardgame.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fengyun.cardgame.bean.Card;
import com.fengyun.cardgame.bean.CardTypeGouJi;
import com.fengyun.cardgame.bean.HandCardLandlord;

public class LandlordLogic {
	
	public static boolean reasonable(HandCardLandlord current, 
			                         HandCardLandlord selected){
		if(current == null){
			return true;
		}
		if(current.getCardTypeLandlord() == CardTypeGouJi.huojian){
			return false;
		}else if(current.getCardTypeLandlord() == CardTypeGouJi.zhadan){
			if(selected.getCardTypeLandlord() == CardTypeGouJi.huojian){
				return true;
			}else if(selected.getCardTypeLandlord() == CardTypeGouJi.zhadan &&
					 selected.getWeight() > current.getWeight()){
				return true;
			}
		}else{
			if(selected.getCardTypeLandlord() == CardTypeGouJi.huojian ||
			   selected.getCardTypeLandlord() == CardTypeGouJi.zhadan){
				return true;
			}else{
				if(selected.getCardTypeLandlord() == current.getCardTypeLandlord() &&
				   selected.getWeight() > current.getWeight()){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	public static CardTypeGouJi getCardTypeLandlord(List<Card> list){
		Collections.sort(list);
		if(list.size() == 1)
			return CardTypeGouJi.dan;
		else if(list.size() == 2){
			if(list.get(0).getName() == list.get(1).getName()){
				return CardTypeGouJi.dui;
			}else if(list.get(0).getName() == 17 && list.get(1).getName() == 16){
				return CardTypeGouJi.huojian;
			}else{
				return CardTypeGouJi.error;
			}
		}else if(list.size() == 3){
			if(list.get(0).getName() == list.get(1).getName() && 
			   list.get(0).getName() == list.get(2).getName())
			   return CardTypeGouJi.sanbudai;
			else 
			   return CardTypeGouJi.error;
		}else if(list.size() == 4){
			   return getCardTypeLandlordFour(list);
				}else if(list.size() == 5){
					return getCardTypeLandlordFive(list);
				}else if(list.size() == 6){
					return getCardTypeLandlordSix(list);
				}else if(list.size() == 8){
					return getCardTypeLandlordEight(list);
				}else if(list.size() == 9){
					return getCardTypeLandlordNine(list);
				}else if(list.size() == 10){
					return getCardTypeLandlordTen(list);
				}else if(list.size() == 12){
					return getCardTypeLandlordTwelve(list);
				}else if(list.size() == 14){
					return getCardTypeLandlordFourTeen(list);
				}else{
					return getCardTypeLandlordRank(list);
				}
	}
	
	public static int getWeightLandlord(List<Card> list){
		Collections.sort(list);
		if(list.size() == 1)
			return list.get(0).getName();
		else if(list.size() == 2){
			if(list.get(0).getName() == list.get(1).getName()){
				return list.get(0).getName();
			}else if(list.get(0).getName() == 17 && list.get(1).getName() == 16){
				return list.get(1).getName();
			}else{
				return -1;
			}
		}else if(list.size() == 3){
			if(list.get(0).getName() == list.get(1).getName() && 
					list.get(0).getName() == list.get(2).getName())
				return list.get(0).getName();
			else 
				return -1;
		}else if(list.size() == 4){
			return getWeightLandlordFour(list);
		}else if(list.size() == 5){
			return getWeightLandlordFive(list);
		}else if(list.size() == 6){
			return getWeightLandlordSix(list);
		}else if(list.size() == 8){
			return getWeightLandlordEight(list);
		}else if(list.size() == 9){
			return getWeightLandlordNine(list);
		}else if(list.size() == 10){
			return getWeightLandlordTen(list);
		}else if(list.size() == 12){
			return getWeightLandlordTwelve(list);
		}else if(list.size() == 14){
			return getWeightLandlordFourTeen(list);
		}else{
			return getWeightLandlordRank(list);
		}
	}
	public static boolean isLegal(List<Card> list){
		if(list.size() == 1)
			return true;
		else if(list.size() == 2){
			if(list.get(0).getName() == list.get(1).getName()){
				return true;
			}else{
				return false;
			}
		}else if(list.size() == 3){
			if(list.get(0).getName() == list.get(1).getName() && 
			   list.get(0).getName() == list.get(2).getName())
				return true;
			else 
				return false;
		}else if(list.size() == 4){
			return isLegalFour(list);
		}else if(list.size() == 5){
			return isLegalFive(list);
		}else if(list.size() == 6){
			return isLegalSix(list);
		}else if(list.size() == 8){
			return isLegalEight(list);
		}else if(list.size() == 9){
			return isLegalNine(list);
		}else if(list.size() == 10){
			return isLegalTen(list);
		}else{
			return isRank(list);
		}
	}

	public static boolean isLegalFour(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(0).getName() == list.get(3).getName())
			return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				   list.get(0).getName() == list.get(2).getName())
				return true;
		else if(list.get(1).getName() == list.get(2).getName() && 
				   list.get(1).getName() == list.get(3).getName())
				return true;
		return false;
	}
	
	public static CardTypeGouJi getCardTypeLandlordFour(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(0).getName() == list.get(3).getName())
			return CardTypeGouJi.zhadan;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName())
			return CardTypeGouJi.sandaiyi;
		else if(list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName())
			return CardTypeGouJi.sandaiyi;
		return CardTypeGouJi.error;
	}
	
	public static int getWeightLandlordFour(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(0).getName() == list.get(3).getName())
			return list.get(0).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName())
			return list.get(0).getName();
		else if(list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName())
			return list.get(1).getName();
		return -1;
	}
	
	public static boolean isLegalFive(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(3).getName() == list.get(4).getName())
			return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				   list.get(2).getName() == list.get(3).getName() && 
				   list.get(2).getName() == list.get(4).getName())
			return true;
		else{
			return isRank(list);
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordFive(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(3).getName() == list.get(4).getName())
			return CardTypeGouJi.sandaidui;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName())
			return CardTypeGouJi.sandaidui;
		else{
			return getCardTypeLandlordRank(list);
		}
	}
	
	public static int getWeightLandlordFive(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName())
			return list.get(0).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName())
			return list.get(2).getName();
		else{
			return getWeightLandlordRank(list);
		}
	}
	
	public static boolean isLegalSix(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(0).getName() == list.get(3).getName() &&
		   list.get(4).getName() != list.get(5).getName())
		   return true;
		else if(list.get(0).getName() != list.get(5).getName() && 
			    list.get(1).getName() == list.get(2).getName() && 
			    list.get(1).getName() == list.get(3).getName() &&
			    list.get(1).getName() == list.get(4).getName())
			   return true;
		else if(list.get(0).getName() != list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(2).getName() == list.get(5).getName())
				return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName())
				return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1)
				return true;
		else{
			return isRank(list);
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordSix(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(0).getName() == list.get(3).getName() &&
		   list.get(4).getName() != list.get(5).getName())
		   return CardTypeGouJi.sidaierdan;
		else if(list.get(0).getName() != list.get(5).getName() && 
				list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName() &&
				list.get(1).getName() == list.get(4).getName())
			    return CardTypeGouJi.sidaierdan;
		else if(list.get(0).getName() != list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(2).getName() == list.get(5).getName())
			return CardTypeGouJi.sidaierdan;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName())
			return CardTypeGouJi.sanshun6;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1)
			return CardTypeGouJi.shuangshun6;
		else{
			return getCardTypeLandlordRank(list);
		}
	}
	
	public static int getWeightLandlordSix(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(0).getName() == list.get(3).getName() &&
				list.get(4).getName() != list.get(5).getName())
			return list.get(0).getName();
		else if(list.get(0).getName() != list.get(5).getName() && 
				list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName() &&
				list.get(1).getName() == list.get(4).getName())
			return list.get(1).getName();
		else if(list.get(0).getName() != list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(2).getName() == list.get(5).getName())
			return list.get(2).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName())
			return list.get(3).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1)
			return list.get(4).getName();
		else{
			return getWeightLandlordRank(list);
		}
	}
	
	public static boolean isLegalEight(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(0).getName() == list.get(3).getName() &&
		   list.get(4).getName() == list.get(5).getName() &&
		   list.get(4).getName() == list.get(6).getName() &&
		   list.get(4).getName() == list.get(7).getName())
		   return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
			    list.get(0).getName() == list.get(2).getName() && 
			    list.get(0).getName() == list.get(3).getName() &&
			    list.get(4).getName() == list.get(5).getName() &&
			    list.get(6).getName() == list.get(7).getName())
			    return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
			    list.get(2).getName() == list.get(3).getName() && 
			    list.get(2).getName() == list.get(4).getName() &&
			    list.get(2).getName() == list.get(5).getName() &&
			    list.get(6).getName() == list.get(7).getName())
				return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
			    list.get(2).getName() == list.get(3).getName() && 
			    list.get(4).getName() == list.get(5).getName() &&
			    list.get(4).getName() == list.get(6).getName() &&
			    list.get(4).getName() == list.get(7).getName())
			    return true;
		else if(list.get(0).getName() != list.get(1).getName() && 
			    list.get(2).getName() == list.get(3).getName() && 
			    list.get(2).getName() == list.get(4).getName() &&
			    list.get(5).getName() == list.get(6).getName() &&
			    list.get(5).getName() == list.get(7).getName() &&
			    list.get(2).getName() - list.get(5).getName() == 1)
			    return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
			    list.get(0).getName() == list.get(2).getName() && 
			    list.get(3).getName() == list.get(4).getName() &&
			    list.get(3).getName() == list.get(5).getName() &&
			    list.get(6).getName() != list.get(7).getName() &&
			    list.get(0).getName() - list.get(3).getName() == 1)
			    return true;
		else if(list.get(0).getName() != list.get(7).getName() && 
			    list.get(1).getName() == list.get(2).getName() && 
			    list.get(1).getName() == list.get(3).getName() &&
			    list.get(4).getName() == list.get(5).getName() &&
			    list.get(4).getName() == list.get(6).getName() &&
			    list.get(1).getName() - list.get(4).getName() == 1)
			    return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1)
			return true;
		else{
			return isRank(list);
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordEight(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(0).getName() == list.get(3).getName() &&
		   list.get(4).getName() == list.get(5).getName() &&
		   list.get(4).getName() == list.get(6).getName() &&
		   list.get(4).getName() == list.get(7).getName())
	       return CardTypeGouJi.sishun8;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(0).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName())
				return CardTypeGouJi.sidaierdui;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(2).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName())
				return CardTypeGouJi.sidaierdui;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(4).getName() == list.get(7).getName())
				return CardTypeGouJi.sidaierdui;
		else if(list.get(0).getName() != list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(5).getName() == list.get(6).getName() &&
				list.get(5).getName() == list.get(7).getName() &&
				list.get(2).getName() - list.get(5).getName() == 1)
				return CardTypeGouJi.feiji8;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() != list.get(7).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1)
				return CardTypeGouJi.feiji8;
		else if(list.get(0).getName() != list.get(7).getName() && 
				list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(1).getName() - list.get(4).getName() == 1)
				return CardTypeGouJi.feiji8;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1)
				return CardTypeGouJi.shuangshun8;
		else{
				return getCardTypeLandlordRank(list);
		}
	}
	
	public static int getWeightLandlordEight(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(0).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(4).getName() == list.get(7).getName())
			return list.get(4).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(0).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName())
			return list.get(0).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(2).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName())
			return list.get(2).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(4).getName() == list.get(7).getName())
			return list.get(4).getName();
		else if(list.get(0).getName() != list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(5).getName() == list.get(6).getName() &&
				list.get(5).getName() == list.get(7).getName() &&
				list.get(2).getName() - list.get(5).getName() == 1)
			return list.get(5).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() != list.get(7).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1)
			return list.get(3).getName();
		else if(list.get(0).getName() != list.get(7).getName() && 
				list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(1).getName() - list.get(4).getName() == 1)
			return list.get(4).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1)
			return list.get(6).getName();
		else{
			return getWeightLandlordRank(list);
		}
	}
	
	public static boolean isLegalNine(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(3).getName() == list.get(4).getName() &&
		   list.get(3).getName() == list.get(5).getName() &&
		   list.get(6).getName() == list.get(7).getName() &&
		   list.get(6).getName() == list.get(8).getName())
		   return true;
		else{
			return isRank(list);
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordNine(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
	       list.get(0).getName() == list.get(2).getName() && 
		   list.get(3).getName() == list.get(4).getName() &&
		   list.get(3).getName() == list.get(5).getName() &&
		   list.get(6).getName() == list.get(7).getName() &&
		   list.get(6).getName() == list.get(8).getName())
			return CardTypeGouJi.sanshun9;
		else{
			return getCardTypeLandlordRank(list);
		}
	}
	
	public static int getWeightLandlordNine(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName())
			return list.get(6).getName();
		else{
			return getWeightLandlordRank(list);
		}
	}
	
	public static boolean isLegalTen(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(0).getName() == list.get(2).getName() && 
		   list.get(3).getName() == list.get(4).getName() &&
		   list.get(3).getName() == list.get(5).getName() &&
		   list.get(6).getName() == list.get(7).getName() &&
		   list.get(8).getName() == list.get(9).getName() &&
		   list.get(0).getName() - list.get(3).getName() == 1)
			return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
			    list.get(2).getName() == list.get(3).getName() && 
			    list.get(2).getName() == list.get(4).getName() &&
			    list.get(5).getName() == list.get(6).getName() &&
			    list.get(5).getName() == list.get(7).getName() &&
			    list.get(8).getName() == list.get(9).getName() &&
			    list.get(2).getName() - list.get(5).getName() == 1)
				return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
			    list.get(2).getName() == list.get(3).getName() && 
			    list.get(4).getName() == list.get(5).getName() &&
			    list.get(4).getName() == list.get(6).getName() &&
			    list.get(7).getName() == list.get(8).getName() &&
			    list.get(7).getName() == list.get(9).getName() &&
			    list.get(4).getName() - list.get(7).getName() == 1)
				return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(8).getName() == 1)
			return true;
		else{
			return isRank(list);
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordTen(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
	  	   list.get(0).getName() == list.get(2).getName() && 
		   list.get(3).getName() == list.get(4).getName() &&
		   list.get(3).getName() == list.get(5).getName() &&
		   list.get(6).getName() == list.get(7).getName() &&
		   list.get(8).getName() == list.get(9).getName() &&
		   list.get(0).getName() - list.get(3).getName() == 1)
		   return CardTypeGouJi.feiji10;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(5).getName() == list.get(6).getName() &&
				list.get(5).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(2).getName() - list.get(5).getName() == 1)
				return CardTypeGouJi.feiji10;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(7).getName() == list.get(8).getName() &&
				list.get(7).getName() == list.get(9).getName() &&
				list.get(4).getName() - list.get(7).getName() == 1)
				return CardTypeGouJi.feiji10;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(8).getName() == 1)
				return CardTypeGouJi.shuangshun10;
		else{
				return getCardTypeLandlordRank(list);
		}
	}
	
	public static int getWeightLandlordTen(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1)
			return list.get(3).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(5).getName() == list.get(6).getName() &&
				list.get(5).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(2).getName() - list.get(5).getName() == 1)
			return list.get(5).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(7).getName() == list.get(8).getName() &&
				list.get(7).getName() == list.get(9).getName() &&
				list.get(4).getName() - list.get(7).getName() == 1)
			return list.get(7).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(8).getName() == 1)
			return list.get(8).getName();
		else{
			return getWeightLandlordRank(list);
		}
	}
	
	public static boolean isLegalTwelve(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
	  	   list.get(0).getName() == list.get(2).getName() && 
		   list.get(0).getName() == list.get(3).getName() &&
		   list.get(4).getName() == list.get(5).getName() &&
		   list.get(4).getName() == list.get(6).getName() &&
		   list.get(4).getName() == list.get(7).getName() &&
		   list.get(8).getName() == list.get(9).getName() &&
		   list.get(8).getName() == list.get(10).getName() &&
		   list.get(8).getName() == list.get(11).getName() &&
		   list.get(0).getName() - list.get(4).getName() == 1 &&
		   list.get(4).getName() - list.get(8).getName() == 1)
		  return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(9).getName() == list.get(10).getName() &&
				list.get(9).getName() == list.get(11).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1 &&
				list.get(3).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(9).getName() == 1)
				return true;
		else if(list.get(0).getName() != list.get(1).getName() &&
				list.get(0).getName() != list.get(2).getName() &&
				list.get(1).getName() != list.get(2).getName() &&
				list.get(3).getName() == list.get(4).getName() && 
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(9).getName() == list.get(10).getName() &&
				list.get(9).getName() == list.get(11).getName() &&
				list.get(3).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(9).getName() == 1)
				return true;
		else if(list.get(0).getName() != list.get(1).getName() &&
				list.get(0).getName() != list.get(11).getName() &&
				list.get(1).getName() != list.get(11).getName() &&
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(5).getName() == list.get(6).getName() &&
				list.get(5).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(8).getName() == list.get(10).getName() &&
				list.get(2).getName() - list.get(5).getName() == 1 &&
				list.get(5).getName() - list.get(8).getName() == 1)
			return true;
		else if(list.get(0).getName() != list.get(10).getName() &&
				list.get(0).getName() != list.get(11).getName() &&
				list.get(10).getName() != list.get(11).getName() &&
				list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(7).getName() == list.get(8).getName() &&
				list.get(7).getName() == list.get(9).getName() &&
				list.get(1).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(7).getName() == 1)
			return true;
		else if(list.get(9).getName() != list.get(10).getName() &&
				list.get(9).getName() != list.get(11).getName() &&
				list.get(10).getName() != list.get(11).getName() &&
				list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() &&
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1 &&
				list.get(3).getName() - list.get(6).getName() == 1)
			return true;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(10).getName() == list.get(11).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(8).getName() == 1 &&
				list.get(8).getName() - list.get(10).getName() == 1)
				return true;
		else{
			return isRank(list);
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordTwelve(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
	  	   list.get(0).getName() == list.get(2).getName() && 
		   list.get(0).getName() == list.get(3).getName() &&
		   list.get(4).getName() == list.get(5).getName() &&
		   list.get(4).getName() == list.get(6).getName() &&
		   list.get(4).getName() == list.get(7).getName() &&
		   list.get(8).getName() == list.get(9).getName() &&
		   list.get(8).getName() == list.get(10).getName() &&
		   list.get(8).getName() == list.get(11).getName() &&
		   list.get(0).getName() - list.get(4).getName() == 1 &&
		   list.get(4).getName() - list.get(8).getName() == 1)
		  return CardTypeGouJi.sishun12;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(9).getName() == list.get(10).getName() &&
				list.get(9).getName() == list.get(11).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1 &&
				list.get(3).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(9).getName() == 1)
				return CardTypeGouJi.sanshun12;
		else if(list.get(0).getName() != list.get(1).getName() &&
				list.get(0).getName() != list.get(2).getName() &&
				list.get(1).getName() != list.get(2).getName() &&
				list.get(3).getName() == list.get(4).getName() && 
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(9).getName() == list.get(10).getName() &&
				list.get(9).getName() == list.get(11).getName() &&
				list.get(3).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(9).getName() == 1)
				return CardTypeGouJi.feiji12;
		else if(list.get(0).getName() != list.get(1).getName() &&
				list.get(0).getName() != list.get(11).getName() &&
				list.get(1).getName() != list.get(11).getName() &&
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(5).getName() == list.get(6).getName() &&
				list.get(5).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(8).getName() == list.get(10).getName() &&
				list.get(2).getName() - list.get(5).getName() == 1 &&
				list.get(5).getName() - list.get(8).getName() == 1)
			return CardTypeGouJi.feiji12;
		else if(list.get(0).getName() != list.get(10).getName() &&
				list.get(0).getName() != list.get(11).getName() &&
				list.get(10).getName() != list.get(11).getName() &&
				list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(7).getName() == list.get(8).getName() &&
				list.get(7).getName() == list.get(9).getName() &&
				list.get(1).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(7).getName() == 1)
			return CardTypeGouJi.feiji12;
		else if(list.get(9).getName() != list.get(10).getName() &&
				list.get(9).getName() != list.get(11).getName() &&
				list.get(10).getName() != list.get(11).getName() &&
				list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() &&
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1 &&
				list.get(3).getName() - list.get(6).getName() == 1)
			return CardTypeGouJi.feiji12;
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(10).getName() == list.get(11).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(8).getName() == 1 &&
				list.get(8).getName() - list.get(10).getName() == 1)
				return CardTypeGouJi.shuangshun12;
		else{
			return getCardTypeLandlordRank(list);
		}
	}
	
	public static int getWeightLandlordTwelve(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(0).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(4).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(8).getName() == list.get(10).getName() &&
				list.get(8).getName() == list.get(11).getName() &&
				list.get(0).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(8).getName() == 1)
			return list.get(8).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() && 
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(9).getName() == list.get(10).getName() &&
				list.get(9).getName() == list.get(11).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1 &&
				list.get(3).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(9).getName() == 1)
			return list.get(9).getName();
		else if(list.get(0).getName() != list.get(1).getName() &&
				list.get(0).getName() != list.get(2).getName() &&
				list.get(1).getName() != list.get(2).getName() &&
				list.get(3).getName() == list.get(4).getName() && 
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(9).getName() == list.get(10).getName() &&
				list.get(9).getName() == list.get(11).getName() &&
				list.get(3).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(9).getName() == 1)
			return list.get(9).getName();
		else if(list.get(0).getName() != list.get(1).getName() &&
				list.get(0).getName() != list.get(11).getName() &&
				list.get(1).getName() != list.get(11).getName() &&
				list.get(2).getName() == list.get(3).getName() && 
				list.get(2).getName() == list.get(4).getName() &&
				list.get(5).getName() == list.get(6).getName() &&
				list.get(5).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(8).getName() == list.get(10).getName() &&
				list.get(2).getName() - list.get(5).getName() == 1 &&
				list.get(5).getName() - list.get(8).getName() == 1)
			return list.get(8).getName();
		else if(list.get(0).getName() != list.get(10).getName() &&
				list.get(0).getName() != list.get(11).getName() &&
				list.get(10).getName() != list.get(11).getName() &&
				list.get(1).getName() == list.get(2).getName() && 
				list.get(1).getName() == list.get(3).getName() &&
				list.get(4).getName() == list.get(5).getName() &&
				list.get(4).getName() == list.get(6).getName() &&
				list.get(7).getName() == list.get(8).getName() &&
				list.get(7).getName() == list.get(9).getName() &&
				list.get(1).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(7).getName() == 1)
			return list.get(7).getName();
		else if(list.get(9).getName() != list.get(10).getName() &&
				list.get(9).getName() != list.get(11).getName() &&
				list.get(10).getName() != list.get(11).getName() &&
				list.get(0).getName() == list.get(1).getName() && 
				list.get(0).getName() == list.get(2).getName() &&
				list.get(3).getName() == list.get(4).getName() &&
				list.get(3).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(6).getName() == list.get(8).getName() &&
				list.get(0).getName() - list.get(3).getName() == 1 &&
				list.get(3).getName() - list.get(6).getName() == 1)
			return list.get(6).getName();
		else if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(10).getName() == list.get(11).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(8).getName() == 1 &&
				list.get(8).getName() - list.get(10).getName() == 1)
			return list.get(10).getName();
		else{
			return getWeightLandlordRank(list);
		}
	}
	
	public static boolean isLegalFourTeen(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(2).getName() == list.get(3).getName() && 
		   list.get(4).getName() == list.get(5).getName() &&
		   list.get(6).getName() == list.get(7).getName() &&
		   list.get(8).getName() == list.get(9).getName() &&
		   list.get(10).getName() == list.get(11).getName() &&
		   list.get(12).getName() == list.get(13).getName() &&
		   list.get(0).getName() - list.get(2).getName() == 1 &&
		   list.get(2).getName() - list.get(4).getName() == 1 &&
		   list.get(4).getName() - list.get(6).getName() == 1 &&
		   list.get(6).getName() - list.get(8).getName() == 1 &&
		   list.get(8).getName() - list.get(10).getName() == 1 &&
		   list.get(10).getName() - list.get(12).getName() == 1)
				return true;
		else{
			return isRank(list);
		}
	}
	
	public static CardTypeGouJi getCardTypeLandlordFourTeen(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
		   list.get(2).getName() == list.get(3).getName() && 
		   list.get(4).getName() == list.get(5).getName() &&
		   list.get(6).getName() == list.get(7).getName() &&
		   list.get(8).getName() == list.get(9).getName() &&
		   list.get(10).getName() == list.get(11).getName() &&
		   list.get(12).getName() == list.get(13).getName() &&
		   list.get(0).getName() - list.get(2).getName() == 1 &&
		   list.get(2).getName() - list.get(4).getName() == 1 &&
		   list.get(4).getName() - list.get(6).getName() == 1 &&
		   list.get(6).getName() - list.get(8).getName() == 1 &&
		   list.get(8).getName() - list.get(10).getName() == 1 &&
		   list.get(10).getName() - list.get(12).getName() == 1)
		   return CardTypeGouJi.shuangshun14;
		else{
			return CardTypeGouJi.error;
		}
	}
	
	public static int getWeightLandlordFourTeen(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		if(list.get(0).getName() == list.get(1).getName() && 
				list.get(2).getName() == list.get(3).getName() && 
				list.get(4).getName() == list.get(5).getName() &&
				list.get(6).getName() == list.get(7).getName() &&
				list.get(8).getName() == list.get(9).getName() &&
				list.get(10).getName() == list.get(11).getName() &&
				list.get(12).getName() == list.get(13).getName() &&
				list.get(0).getName() - list.get(2).getName() == 1 &&
				list.get(2).getName() - list.get(4).getName() == 1 &&
				list.get(4).getName() - list.get(6).getName() == 1 &&
				list.get(6).getName() - list.get(8).getName() == 1 &&
				list.get(8).getName() - list.get(10).getName() == 1 &&
				list.get(10).getName() - list.get(12).getName() == 1)
			return list.get(12).getName();
		else{
			return -1;
		}
	}
	
	public static boolean isRank(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		for(int i = 0; i < list.size() - 1; i++){
			if(list.get(0).getName() < 15 && list.get(i).getName() - list.get(i+1).getName() != 1){
				return false;
			}
		}
		return true;
	}
	
	public static CardTypeGouJi getCardTypeLandlordRank(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		for(int i = 0; i < list.size() - 1; i++){
			if(list.get(0).getName() < 15 && list.get(i).getName() - list.get(i+1).getName() != 1){
				return CardTypeGouJi.error;
			}
		}
		switch (list.size()) {
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
	
	public static int getWeightLandlordRank(List<Card> list) {
		// TODO Auto-generated method stub
		Collections.sort(list);
		for(int i = 0; i < list.size() - 1; i++){
			if(list.get(0).getName() < 15 && list.get(i).getName() - list.get(i+1).getName() != 1){
				return -1;
			}
		}
		return list.get(list.size() - 1).getName();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Card card1 = new Card("c1", 3, null, null);
		Card card2 = new Card("c2", 3, null, null);
		Card card3 = new Card("c3", 3, null, null);
		Card card4 = new Card("c4", 3, null, null);
		Card card5 = new Card("c5", 4, null, null);
		Card card6 = new Card("c6", 4, null, null);
		Card card7 = new Card("c7", 4, null, null);
		Card card8 = new Card("c8", 4, null, null);
		Card card9 = new Card("c9", 5, null, null);
		Card card10 = new Card("c10", 5, null, null);
		Card card11 = new Card("c11", 5, null, null);
		Card card12 = new Card("c12", 5, null, null);
		Card card13 = new Card("c13", 6, null, null);
		Card card14 = new Card("c14", 6, null, null);
		Card card15 = new Card("c15", 6, null, null);
		Card card16 = new Card("c16", 6, null, null);
		Card card17 = new Card("c17", 7, null, null);
		Card card18 = new Card("c18", 7, null, null);
		Card card19 = new Card("c19", 7, null, null);
		Card card20 = new Card("c20", 7, null, null);
		Card card21 = new Card("c21", 8, null, null);
		Card card22 = new Card("c22", 8, null, null);
		Card card23 = new Card("c23", 8, null, null);
		Card card24 = new Card("c24", 8, null, null);
		Card card25 = new Card("c25", 9, null, null);
		Card card26 = new Card("c26", 9, null, null);
		Card card27 = new Card("c27", 9, null, null);
		Card card28 = new Card("c28", 9, null, null);
		Card card29 = new Card("c29", 10, null, null);
		Card card30 = new Card("C30", 10, null, null);
		Card card31 = new Card("c31", 10, null, null);
		Card card32 = new Card("c32", 10, null, null);
		Card card33 = new Card("c33", 11, null, null);
		Card card34 = new Card("c34", 11, null, null);
		Card card35 = new Card("c35", 11, null, null);
		Card card36 = new Card("c36", 11, null, null);
		Card card37 = new Card("c37", 12, null, null);
		Card card38 = new Card("c38", 12, null, null);
		Card card39 = new Card("c39", 12, null, null);
		Card card40 = new Card("c40", 12, null, null);
		Card card41 = new Card("c41", 13, null, null);
		Card card42 = new Card("c42", 13, null, null);
		Card card43 = new Card("c43", 13, null, null);
		Card card44 = new Card("c44", 13, null, null);
		Card card45 = new Card("c45", 14, null, null);
		Card card46 = new Card("c46", 14, null, null);
		Card card47 = new Card("c47", 14, null, null);
		Card card48 = new Card("c48", 14, null, null);
		Card card49 = new Card("c49", 15, null, null);
		Card card50 = new Card("c50", 15, null, null);
		Card card51 = new Card("c51", 15, null, null);
		Card card52 = new Card("c52", 15, null, null);
		Card card53 = new Card("c53", 16, null, null);
		Card card54 = new Card("c54", 17, null, null);
		List<Card> list = new ArrayList<Card>();
//		list.add(card1);  //3
		list.add(card2);  //3
//		list.add(card3);  //3
//		list.add(card4);  //3
		list.add(card5);  //4
		list.add(card6);  //4
//		list.add(card7);  //4
//		list.add(card8);  //4
		list.add(card9);  //5
//		list.add(card10); //5
//		list.add(card11); //5
//		list.add(card12); //5
//		list.add(card13); //6
		list.add(card14); //6
//		list.add(card15); //6
//		list.add(card16); //6
//		list.add(card17); //7
//		list.add(card18); //7
		list.add(card19); //7
		list.add(card20); //7
//		list.add(card21); //8
//		list.add(card22); //8
//		list.add(card23); //8
		list.add(card24); //8
//		list.add(card25); //9
		list.add(card26); //9
		list.add(card27); //9
		list.add(card28); //9
//		list.add(card29); //10
//		list.add(card30); //10
//		list.add(card31); //10
		list.add(card32); //10
//		list.add(card33); //11
//		list.add(card34); //11
//		list.add(card35); //11
		list.add(card36); //11
//		list.add(card37); //12
//		list.add(card38); //12
		list.add(card39); //12
		list.add(card40); //12
		list.add(card41); //13
//		list.add(card42); //13
//		list.add(card43); //13
//		list.add(card44); //13
//		list.add(card45); //14
//		list.add(card46); //14
//		list.add(card47); //14
		list.add(card48); //14
//		list.add(card49); //15
//		list.add(card50); //15
//		list.add(card51); //15
		list.add(card52); //15
		list.add(card53); //16
		list.add(card54); //17
//		boolean legal = isLegal(list);
//		System.out.println(legal);
//		CardTypeLandlord cardTypeLandlord = getCardTypeLandlord(list);
//		System.out.println(cardTypeLandlord);
		List<Card> handcardlist = new ArrayList<Card>();
		handcardlist.add(card13);
		HandCardLandlord handCardLandlord = new HandCardLandlord(
				handcardlist, CardTypeGouJi.dan, card13.getName());
//		List<HandCardLandlord> handCardLandlordList = HandCardLandlord.getHintList
//				(list, handCardLandlord);
//		System.out.println(handCardLandlordList);
//		List<Card> lowestCards = HandCardLandlord.getLowestCards(list);
//		System.out.println(lowestCards);
		HandCardLandlord handCardLandlord2 = HandCardLandlord.getLowestHandCardLandlord(list);
		System.out.println(handCardLandlord2);
//		List<HandCardLandlord> handCardLandlordlist = HandCardLandlord.getDan(list);
//		List<HandCardLandlord> handCardLandlordlist = HandCardLandlord.getDui(list);
//		List<HandCardLandlord> handCardLandlordlist = HandCardLandlord.getSan(list);
//		List<HandCardLandlord> handCardLandlordlist = HandCardLandlord.getSi(list);
//		System.out.println(handCardLandlordlist);
//		List<HandCardLandlord> cardTypes = HandCardLandlord.getCardTypes(list);
//		System.out.println(cardTypes);
//		List<HandCardLandlord> cardTypes = HandCardLandlord.getCardTypesBetween(list, 1, 2, 1);
//		System.out.println(cardTypes);
//		List<Card> rankableCardTypes = HandCardLandlord.getRankableCardTypes(list);
//		System.out.println(rankableCardTypes);
//		List<HandCardLandlord> ranklist = HandCardLandlord.getRank(list);
//		System.out.println(ranklist);
//		List<HandCardLandlord> subRanklist = HandCardLandlord.getAllSubRankAll(ranklist, 5);
//		System.out.println(subRanklist);
//		List<Card> cardTypes = HandCardLandlord.getCardTypesDuiMore(list);
//		System.out.println(cardTypes);
//		List<HandCardLandlord> handCardLandlordlist = HandCardLandlord.getCardTypesDui(list);
//		System.out.println(handCardLandlordlist);
//		List<HandCardLandlord> handCardLandlordlist = HandCardLandlord.getCardTypesSan(list);
//		System.out.println(handCardLandlordlist);
//		List<HandCardLandlord> rankList = HandCardLandlord.getRankSan(list);
//		System.out.println(rankList);
//		List<HandCardLandlord> subRankList = 
//				HandCardLandlord.getAllSubRankAllSan(rankList, 6);
//		System.out.println(subRankList);
//		List<HandCardLandlord> handCardLandlordList = 
//				HandCardLandlord.getAllRank(list, CardTypeLandlord.shuangshun6);
//		System.out.println(handCardLandlordList);
//		List<HandCardLandlord> sandaiyiList = HandCardLandlord.getSandaiyi(list);
//		System.out.println(sandaiyiList);
//		List<HandCardLandlord> sandaiduiList = HandCardLandlord.getSandaidui(list);
//		System.out.println(sandaiduiList);
//		List<HandCardLandlord> sidaierdanList = HandCardLandlord.getSidaierdui(list);
//		System.out.println(sidaierdanList);
//		List<HandCardLandlord> feiji8List = HandCardLandlord.getFeiji8(list);
//		System.out.println(feiji8List);
//		List<HandCardLandlord> feiji12List = HandCardLandlord.getFeiji12(list);
//		System.out.println(feiji12List);
//		List<HandCardLandlord> feiji15List = HandCardLandlord.getFeiji15(list);
//		System.out.println(feiji15List);
	}
}
