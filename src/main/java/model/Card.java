package model;

public class Card {
	
	private String mark;
	private int number;
	
	public Card(String mark, int number) {
		this.mark = mark;
		this.number = number;
	}
	
	public String getMark() {
		return mark;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getCard() {
		switch(getNumber()) {
			case 11:
				return (getMark() + "J");
			case 12:
				return (getMark() + "Q");
			case 13:
				return (getMark() + "K");
			default:
				return (getMark() + Integer.toString(number));
		}
	}
}
