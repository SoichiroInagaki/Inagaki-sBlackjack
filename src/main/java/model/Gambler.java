package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Gambler {
	
	protected List<Card> hand;
	protected int point;
	protected boolean bust;
	
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
	public boolean checkBust() {
		if(point <= 21) {
			bust = false;
		}else {
			bust = true;
		}
		return bust;
	}
	
	//現在の点数を計算するメソッド
	public void calculatePoint() {
		point = 0;
		for(Card card : hand) {
			switch(card.getNumber()) {
				case 1:
					point += 11;
					if(checkBust()) {
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
	
	//手札のカードの文字列情報を取得するメソッド
	public String getHandCardStr(int i) {
		String strCard = hand.get(i).getCardStr();
		return strCard;
	}
	
	//手札のカードの枚数を取得するメソッド
	public int countHand() {
		return hand.size();
	}
	
	//初期手札がブラックジャックか確認するメソッド
	public boolean checkBlackjack() {
		if((countHand() == 2) && (getPoint() == 21)) {
			return true;
		}else {
			return false;
		}
	}
}
