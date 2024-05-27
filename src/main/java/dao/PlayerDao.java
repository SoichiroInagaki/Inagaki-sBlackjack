package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import exception.BlackjackException;
import model.Player;

public class PlayerDao {
	
	
	//MySQLに接続するためのフィールドを用意
	private Connection con = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	
	//DBに接続するためのメソッド
	private void getConnection() throws BlackjackException {
		try {	
			//MySQLコネクタをロード
			Class.forName("com.mysql.cj.jdbc.Driver");
				
			//DBに接続
			String url = "jdbc:mysql://localhost/blackjack";
			String user = "root";
			String password = "pH8a7fwhN9";
			con = DriverManager.getConnection(url, user, password);
	
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
			throw new BlackjackException("JDBCドライバが見つかりません");
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("SQL実行中に例外が発生しました");
		}
	}
	
	
	//DBから切断するためのメソッド
	protected void close() throws BlackjackException {
		try {
			if(con != null) {
				con.close();
			}
			if(ps != null) {
				ps.close();
			}
			if(rs != null) {
				rs.close();
			}
			System.out.println("closeしました");
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("close処理中に例外が発生しました");
		}
	}
	
	
	//プレイヤーネームが使用されているか確認するメソッド
	public String findPlayerName(String playerNameFinding) throws BlackjackException {
		
		//返す変数を用意
		String playerName = null;
		
		try {
			//DBに接続
			getConnection();
			
			//プレイヤーが存在するかSQLで検索
			String sql = "select * from player where player_name = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, playerNameFinding);
			rs = ps.executeQuery();
			
			//もし存在すれば、変数に代入
			while(rs.next()) {
				playerName = rs.getString("player_name");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("プレイヤー情報の取得に失敗しました");
		}catch(BlackjackException e) {
			throw e;
		}finally {
			close();
		}
		//存在したプレイヤーネームか、nullを返す
		return playerName;
	}

	
	//新規プレイヤーをDBに登録するメソッド
	public void insertPlayer(Player player) throws BlackjackException {
		try {
			
			//プレイヤークラスから属性を取得
			String playerName = player.getName();
			String playerPassword = player.getPassword();
			
			//DBに接続
			getConnection();
			
			//SQLでプレイヤーを登録
			String sql = "insert into player(player_name, player_password) value(?, ?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, playerName);
			ps.setString(2, playerPassword);
			ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("プレイヤーの登録に失敗しました");
//		}catch(BlackjackException e) {
//			throw e;
		}finally {
			close();
		}
	}
	
	
	//プレイヤー情報が合っているか確認するメソッド
	public Player findPlayer(String EnterdName, String EnterdPassword) throws BlackjackException {
		
		//返す変数を用意
		Player player = null;
		
		try {
			//DBに接続
			getConnection();
			
			//SQLでプレイヤーを検索
			String sql = "select * from player where player_name = ? and player_password = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, EnterdName);
			ps.setString(2, EnterdPassword);
			rs = ps.executeQuery();
			
			//検索に該当したプレイヤーがいれば、そのプレイヤーをインスタンス化して変数に代入
			while(rs.next()) {
				String PlayerName = rs.getString("player_name");
				String PlayerPassword = rs.getString("player_password");
				player = new Player(PlayerName, PlayerPassword);
			}
			
			//該当プレイヤーがいなければ、エラーメッセージを表示させる
			if(player == null) {
				throw new BlackjackException("入力内容に誤りがあります。");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("SQL実行中に例外が発生しました");
		}finally {
			close();
		}
		//検索に該当したプレイヤーかnullを返す
		return player;
	}

	
	//プレイヤー情報をDBから削除するメソッド
	public void deletePlayer(Player player) throws BlackjackException {
		
		String playerName = player.getName();
		
		try {
			//DBに接続
			getConnection();
			
			//プレイヤーを削除
			String sql = "delete from player where player_name = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, playerName);
			ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("プレイヤーの削除に失敗しました");
		}catch(BlackjackException e) {
			throw e;
		}finally {
			close();
		}
	}
	
	
	
	
}
