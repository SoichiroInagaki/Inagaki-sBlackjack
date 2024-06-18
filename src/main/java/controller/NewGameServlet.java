package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dao.PlayerDao;
import exception.BlackjackException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Dealer;
import model.Deck;
import model.Player;
import model.PlayerInGame;
import model.Result;

@WebServlet("/NewGameServlet")
public class NewGameServlet extends HttpServlet {
	
	protected void doPost
		(HttpServletRequest request, HttpServletResponse response) 
				throws ServletException, IOException {
		
		//カードを準備し、ディーラーとプレイヤーに手札を配る
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		//スプリット対応のため、プレイヤーの手札はリスト化する
		List<PlayerInGame> playerHandList = new ArrayList<>();
		playerHandList.add(playerInGame);
				
		//カード状況は複数の画面で共有するため、セッションに保持
		HttpSession session = request.getSession();
		session.setAttribute("deck", deck);
		session.setAttribute("playerHandList", playerHandList);
		session.setAttribute("dealer", dealer);
		
		//チップ状況はプレイヤーの手札に情報を保持
		int chip = Integer.valueOf(request.getParameter("bet"));
		playerInGame.setChip(chip);
		
		// 画面遷移用の変数を宣言
		String nextPage = null;
		
		//DBを利用する処理を行う
		try {
			//DB接続用に、プレイヤーのDaoインスタンスを取得
			PlayerDao playerDao = new PlayerDao();
				
			//賭けられたチップ枚数分、DBに登録されているチップ総量を減らす
			Player player = (Player) session.getAttribute("player");
			playerDao.bet(player.getId(), chip);
			
			//スプリット可能なら、プレイヤーの保有チップ枚数をリクエストに保持
			if(playerInGame.checkSplittable()) {
				int totalChips = playerDao.getChip(player.getId());
				request.setAttribute("totalChips", totalChips);
			}
			
			//ブラックジャック判定
			//勝った場合
			if(playerInGame.checkBlackjack() && !dealer.checkBlackjack()) {
				
				//配当されるチップ枚数を計算
				//ブラックジャックで勝った時の配当は2.5倍
				double winRate = 2.5; 
				double calculatedChip = (chip * winRate);
				int cashBackedChip = (int)calculatedChip;
				
				//メッセージを登録
				Result.winOfBlackjack(request);
				Result.win(request, cashBackedChip);
				
				//DBに配当チップと戦績を反映
				playerDao.cashBack(player.getId(), cashBackedChip);
				playerDao.updateRecord(player, "win");
				
				//遷移先は結果画面
				nextPage = "result.jsp";
						
			//引き分けた場合
			}else if(playerInGame.checkBlackjack() && dealer.checkBlackjack()) {
						
				//メッセージを登録
				Result.drawOfBlackjack(request);
				Result.draw(request, chip);
				
				//DBに引き分け回収のチップと戦績を反映
				playerDao.cashBack(player.getId(), chip);
				playerDao.updateRecord(player, "draw");
				
				//遷移先は結果画面
				nextPage = "result.jsp";
				
			//負けた場合
			}else if(!(playerInGame.checkBlackjack()) && dealer.checkBlackjack()) {
				
				//メッセージの登録
				Result.loseOfBlackjack(request);
				Result.lose(request, chip);
				
				/*
				 * DBに戦績の反映
				 * 試合に負けたため、キャッシュバックされるチップはない
				 */
				playerDao.updateRecord(player, "lose");
			
				//遷移先は結果画面
				nextPage = "result.jsp";
				
			//ブラックジャックが完成していなければ、ゲーム画面に遷移する
			}else {
				nextPage = "in-game.jsp";
			}
		}catch(BlackjackException e) {
			//エラーメッセージが出た時は取得してリクエストに保持
			request.setAttribute("message", e.getMessage());
			//遷移先はゲーム開始画面
			nextPage = "play.jsp";
		}
		//try処理が終わったら画面遷移を行う
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
}
