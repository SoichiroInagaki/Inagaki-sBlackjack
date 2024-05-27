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
				return (getMark() + "のJ");
			case 12:
				return (getMark() + "のQ");
			case 13:
				return (getMark() + "のK");
			default:
				return (getMark() + Integer.toString(number));
		}
	}
}
