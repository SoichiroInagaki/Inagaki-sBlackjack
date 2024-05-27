package model;

public class PlayerInGame extends Gambler {

	@Override
	public void hit(Deck deck) {
		// TODO 自動生成されたメソッド・スタブ
		hand.add(deck.drawCard());
		calculatePoint();
	}

}
