package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Gambler {
	
	private List<Card> hand;
	private int point;
	private boolean burst;
	
	//ゲーム開始時、カードを2枚引く
	public void prepareHand(Deck deck) {
		hand = new ArrayList<>();
		hand.add(deck.drawCard());
		hand.add(deck.drawCard());
	}
	
	//カードを引くメソッド、抽象メソッド
	public abstract void hit();
	
	//バースト状態を確認するメソッド
	public boolean confirmBurst() {
		if(point <= 21) {
			burst = false;
		}else {
			burst = true;
		}
		return burst;
	}
	
	//現在の点数を計算するメソッド
	public int calculatePoint() {
		for(Card card : hand) {
			switch(card.getNumber()) {
				case 1:
					point += 11;
					if(confirmBurst()) {
						point += -10;
					}
					break;
				case 11:
				case 12:
				case 13:
					point += 10;
					break;
				default:
					point += card.getNumber();
			}
		}
		return point;
	}

}
