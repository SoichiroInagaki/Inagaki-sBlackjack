package model;

import java.util.ArrayList;

public class PlayerInGame extends Gambler {
	
	private boolean splittable = false;
	private boolean pairOfA = false;
	private boolean splitA = false;
	private boolean splitB = false;
	private boolean actionIsEnd = false;
	private String result = null;
	
	//山札からカードを1枚引くメソッド
	@Override
	public void hit(Deck deck) {
		// TODO 自動生成されたメソッド・スタブ
		hand.add(deck.drawCard());
		calculatePoint();
	}
	
	//初期手札がスプリット可能か確認するメソッド
	public boolean checkSplittable() {
		if(hand.get(0).getNumber() == hand.get(1).getNumber()) {
			splittable = true;
		}
		return splittable;
	}
	
	//初期手札がAのペアか確認するメソッド
	public boolean checkPairOfA() {
		if((hand.get(0).getNumber() == 1) && (hand.get(1).getNumber() == 1)){
			this.pairOfA = true;
		}
		return pairOfA;
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
	
	//スプリット時のメソッド群
	public boolean isSplitA() {
		return splitA;
	}
	public void becomeSplitA() {
		this.splitA = true;
	}
	public boolean isSplitB() {
		return splitB;
	}
	public void becomeSplitB() {
		this.splitB = true;
	}
	
	//手札のアクションの終了に関するメソッド群
	public void actionBecomeEnd() {
		this.actionIsEnd = true;
	}
	public boolean actionIsEnd() {
		return actionIsEnd;
	}
	
	//ゲーム結果のゲッター・セッターメソッド
	public void setResult(String result) {
		this.result = result;
	}
	public String getResult() {
		return result;
	}
	
}
