package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Gambler {
	
	protected List<Card> hand;
	protected int point;
	protected boolean burst;
	
	//ゲーム開始時、カードを2枚引く
	public void prepareHand(Deck deck) {
		hand = new ArrayList<>();
		hand.add(deck.drawCard());
		hand.add(deck.drawCard());
		calculatePoint();
	}
	
	//カードを引くメソッド、抽象メソッド
	public abstract void hit(Deck deck);
	
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
	public void calculatePoint() {
		point = 0;
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
	}
	
	//現在の点数を返すメソッド
	public int getPoint() {
		return point;
	}
	
	//手札のカードを取得するメソッド
	public String getHandCard(int i) {
		String strCard = hand.get(i).getCard();
		return strCard;
	}
	
	//手札のカードの枚数を取得するメソッド
	public int countHand() {
		return hand.size();
	}
}
