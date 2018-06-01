package com.fengyun.cardgame.bean;

import java.util.List;

public class BotPlayer extends Player{

	public BotPlayer(int id, Gender gender, int turnId) {
		super(id, false, gender, turnId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HandCardLandlord play(HandCardLandlord currentHandCardLandLord, int turnState) {
		// TODO Auto-generated method stub
		getOutcards().clear();
		HandCardLandlord handCardLandlord = null;
		if(turnState == 2){
		  handCardLandlord = HandCardLandlord.getLowestHandCardLandlord(getCards());
		}else{
			List<HandCardLandlord> hintList= HandCardLandlord.getHintList
					(getCards(), currentHandCardLandLord);
			if(hintList.size() > 0){
				handCardLandlord = hintList.get(hintList.size() - 1);
			}
		}
		if(handCardLandlord != null){
			getOutcards().addAll(handCardLandlord.getList());
			getCards().removeAll(handCardLandlord.getList());
			setOut(true);
			return handCardLandlord;
		}else{
			setOut(false);
			return null;
		}
	}
	
	
}
