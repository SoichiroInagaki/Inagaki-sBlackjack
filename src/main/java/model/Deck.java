package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
	
	private List<Card> deck;
	
	//コンストラクタで山札を生成する
	public Deck() {
		
		String[] marks = {"スペード", "ハート", "ダイヤ", "クローバー"};
		int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
		
		deck = new ArrayList<>();
		
		//カードをインスタンス化し、山札を生成
		for(String mark : marks) {
			for(int number : numbers) {
				deck.add(new Card(mark, number));
			}
		}
		
		//山札をシャッフル
		Collections.shuffle(deck);
		
	}
	
	//カードを１枚引く
	//引かれたカードは山札から取り除く
	public Card drawCard() {
		Card card = deck.get(0);
		deck.remove(0);
		return card;
	}

}
