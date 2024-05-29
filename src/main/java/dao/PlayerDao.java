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

	
	//プレイヤーを探すメソッド
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
				int playerId = rs.getInt("id");
				String playerName = rs.getString("player_name");
				String playerPassword = rs.getString("player_password");
				player = new Player(playerId, playerName, playerPassword);
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
			
			//新規プレイヤーの戦績DBも作成
			player = findPlayer(playerName, playerPassword);
			
			//findPlayerメソッドでcloseされているため、もう一度DB接続
			getConnection();
			
			sql = "insert into record(player_id, coin) value(?, ?)";
			ps = con.prepareStatement(sql);
			ps.setInt(1, player.getId());
			ps.setInt(2, 100);
			ps.executeUpdate();
			
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("プレイヤーの登録に失敗しました");
		}catch(BlackjackException e) {
			throw e;
		}finally {
			close();
		}
	}
	
	
	//プレイヤー情報をDBから削除するメソッド
	public void deletePlayer(Player player) throws BlackjackException {
		
		int playerId = player.getId();
		
		try {
			//DBに接続
			getConnection();
			
			//プレイヤーの戦績を削除
			String sql = "delete from record where player_id = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, playerId);
			ps.executeUpdate();
			
			//プレイヤーを削除
			sql = "delete from player where id = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, playerId);
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
	
	public void updateRecord(Player player, String result) throws BlackjackException {
		try {
			getConnection();
			
			//ログイン中のプレイヤーの戦績を取得
			String sql = "select * from record where player_id = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, player.getId());
			rs = ps.executeQuery();
			
			//戦績記録用の変数を用意
			int record = 0;
			
			//勝敗で分岐
			if(result.equals("win")) {
				
				//勝利数を取得
				while(rs.next()) {
					record = rs.getInt("winning_game");
				}
				
				//勝利時用のSQL文を用意
				sql = "update record set winning_game = ? where player_id = ?";
			}else if(result.equals("draw")) {
				
				//引き分け数を取得
				while(rs.next()) {
					record = rs.getInt("drawn_game");
				}
				
				//引き分け時用のSQL文を用意
				sql = "update record set drawn_game = ? where player_id = ?";
			}else {
				
				//敗北数を取得
				while(rs.next()) {
					record = rs.getInt("lost_game");
				}
				
				//敗北時用のSQL文を用意
				sql = "update record set lost_game = ? where player_id = ?";
			}
			//戦績を更新
			ps = con.prepareStatement(sql);
			ps.setInt(1, (record + 1));
			ps.setInt(2, player.getId());
			ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("戦績の記録に失敗しました");
		}catch(BlackjackException e) {
			throw e;
		}finally {
			close();
		}
	}
	
	
	public Player getRecord(Player player) throws BlackjackException {
		
		//戻り値用の変数を用意
		Player playerForRecord = null;
		
		try {
			getConnection();
			
			//プレイヤー名・勝敗内訳をDBから取得
			String sql = 
					"select t1.player_name, t2.winning_game, t2.lost_game, t2.drawn_game "
					+ "from player as t1 inner join record as t2 on t1.id = t2.player_id "
					+ "where t1.id = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, player.getId());
			rs = ps.executeQuery();
			
			//数値を勝率・試合数に変換
			//プレイヤー名・勝率・試合数を戻り値用変数にセット
			while(rs.next()) {
				String playerName = rs.getString("player_name");
				double playerWinRate = 
						((double)rs.getInt("winning_game") / (rs.getInt("winning_game") + rs.getInt("lost_game")));
				int playerGames = 
						(rs.getInt("winning_game") + rs.getInt("lost_game") + rs.getInt("drawn_game"));
				playerForRecord = new Player(playerName, playerWinRate, playerGames);
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("SQL実行中に例外が発生しました");
		}finally {
			close();
		}
		return playerForRecord;
	}
	
	
	public Player[] getRankedRecords() throws BlackjackException {
		
		Player[] rankedRecords = new Player[5];
		
		try {
			getConnection();
			
			String sql = 
					"select t1.player_name, t2.winning_game, t2.lost_game, t2.drawn_game "
					+ "from player as t1 inner join record as t2 on t1.id = t2.player_id "
					+ "order by (t2.winning_game / (t2.winning_game + t2.lost_game)) desc "
					+ "limit 5";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			
			for(int i = 0; i < 5; i++) {
				rs.next();
				String playerName = rs.getString("player_name");
				double playerWinRate =
						((double) rs.getInt("winning_game") / (rs.getInt("winning_game") + rs.getInt("lost_game")));
				int playerGames = 
						(rs.getInt("winning_game") + rs.getInt("lost_game") + rs.getInt("drawn_game"));
				rankedRecords[i] = new Player(playerName, playerWinRate, playerGames);
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new BlackjackException("SQL実行中に例外が発生しました");
		}finally {
			close();
		}
		return rankedRecords;
	}
	
}
