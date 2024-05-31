package model;

import java.util.ArrayList;

public class PlayerInGame extends Gambler {
	
	@Override
	public void hit(Deck deck) {
		// TODO 自動生成されたメソッド・スタブ
		hand.add(deck.drawCard());
		calculatePoint();
	}
	
	//初期手札がスプリット可能か確認するメソッド
	public boolean checkSplit() {
		if(hand.get(0).getNumber() == hand.get(1).getNumber()) {
			return true;
		}else {
			return false;
		}
	}
	
	//初期手札がAのペアか確認するメソッド
	public boolean checkAPair() {
		if((hand.get(0).getNumber() == 1) && (hand.get(1).getNumber() == 1)){
			return true;
		}else {
			return false;
		}
	}
	
	//スプリットされた手札を用意するメソッド
	public void prepareSplit(Card card, Deck deck) {
		hand = new ArrayList<Card>();
		hand.add(card);
		hit(deck);
	}
	
	//手札のカードのインスタンスを取得するメソッド
	public Card getHandCard(int i) {
		return hand.get(i);
	}
	
	
}
