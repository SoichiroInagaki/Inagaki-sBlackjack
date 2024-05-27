package model;

public class Dealer extends Gambler {

	@Override
	public void hit(Deck deck) {
		// TODO 自動生成されたメソッド・スタブ
		while(point < 17) {
			hand.add(deck.drawCard());
			calculatePoint();
		}

	}

}
