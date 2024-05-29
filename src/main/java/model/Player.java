package model;

public class Player {
	
	private int id;
	private String name;
	private String password;
	private double winRate;
	private int games;
	
	public Player(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public Player(int id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}
	
	public Player(String name, int winRate, int games) {
		this.name = name;
		this.winRate = winRate;
		this.games = games;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public double getWinRate() {
		return winRate;
	}
	
	public void setWinRate(double winRate) {
		this.winRate = winRate;
	}
	
	public int getGames() {
		return games;
	}
	
	public void setGames(int games) {
		this.games = games;
	}
}
