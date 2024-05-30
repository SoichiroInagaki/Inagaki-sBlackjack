package controller;

import java.io.IOException;

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

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    //ゲーム開始時に用いるメソッド
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//処理に必要な変数等を準備
		HttpSession session = request.getSession();
		Player player = (Player) session.getAttribute("player");
		int id = player.getId();
		int chip = Integer.valueOf(request.getParameter("bet"));
		String message = null;
		String nextPage = null;
		
		//カードを準備
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		//DB処理を行う
		try {
			PlayerDao playerDao = new PlayerDao();
			playerDao.bet(id, chip);
			
			//ブラックジャック判定で処理を分岐
			//ブラックジャックが完成していた場合、結果画面に遷移する
			//完成していなければ、ゲーム画面に遷移する
			if(playerInGame.checkBlackjack() && !(dealer.checkBlackjack())) {
				message = "BLACKJACK!!";
				request.setAttribute("blackjackMessageForPlayer", message);
				message = "ディーラーの手札はブラックジャックではなかったので、あなたの勝ちです！";
				request.setAttribute("blackjackMessageForDealer", message);
				double calculatedChip = (chip * 2.5);
				int cashBackedChip = (int)calculatedChip;
				Result.win(request, cashBackedChip);
				playerDao.cashBack(id, cashBackedChip);
				playerDao.updateRecord(player, "win");
				nextPage = "result.jsp";
			}else if(playerInGame.checkBlackjack() && dealer.checkBlackjack()) {
				message = "BLACKJACK!!";
				request.setAttribute("blackjackMessageForPlayer", message);
				message = "ディーラーの手札もブラックジャックでした！このゲームは引き分けです";
				request.setAttribute("blackjackMessageForDealer", message);
				Result.draw(request, chip);
				playerDao.cashBack(id, chip);
				playerDao.updateRecord(player, "draw");
				nextPage = "result.jsp";
			}else if(!(playerInGame.checkBlackjack()) && dealer.checkBlackjack()) {
				message = "ディーラーの手札はブラックジャックでした。お気の毒ですが、あなたの負けです";
				request.setAttribute("blackjackMessageForDealer", message);
				Result.lose(request, chip);
				playerDao.updateRecord(player, "lose");
				nextPage = "result.jsp";
			}else {
				nextPage = "in-game.jsp";
			}
		}catch(BlackjackException e) {
			//エラーメッセージを表示させる
			message = e.getMessage();
			request.setAttribute("message", message);
			//画面遷移先はゲーム開始画面を指定
			nextPage = "play.jsp";
		}
		
		//チップ・カード状況をセッションに保持
		session.setAttribute("bettingChip", chip);
		session.setAttribute("deck", deck);
		session.setAttribute("playerInGame", playerInGame);
		session.setAttribute("dealer", dealer);
		
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
	
	//ゲーム進行中に用いるメソッド
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
		//処理に必要な変数を用意
		String nextPage = null;
		String message = null;
		HttpSession session = request.getSession();
		Player player = (Player) session.getAttribute("player");
		Dealer dealer = (Dealer) session.getAttribute("dealer");
		PlayerInGame playerInGame = (PlayerInGame) 
				session.getAttribute("playerInGame");
		Deck deck = (Deck) session.getAttribute("deck");
		int chip = (Integer)session.getAttribute("bettingChip");
		
		try {
			PlayerDao playerDao = new PlayerDao();
			
			if(request.getParameter("clicked").equals("hit")) {
				playerInGame.hit(deck);
				message = "ヒットしました";
				request.setAttribute("hit", message);
				if(playerInGame.confirmBust()) {
					message = "21点を超えたのでバーストしてしまいました。あなたの負けです";
					request.setAttribute("bustedPlayer", message);
					Result.lose(request, chip);
					playerDao.updateRecord(player, "lose");
					nextPage = "result.jsp";
				}else {
					nextPage = "in-game.jsp";
				}
			}else {
				dealer.hit(deck);
				request.setAttribute("countHit", dealer.countHit());
				if(dealer.confirmBust()) {
					message = "21点を超えたので、ディーラーはバーストしました！";
					request.setAttribute("bustedDealer", message);
					Result.win(request, (chip * 2));
					playerDao.cashBack(player.getId(), (chip * 2));
					playerDao.updateRecord(player, "win");
					nextPage = "result.jsp";
				}else {
					if(dealer.getPoint() < playerInGame.getPoint()) {
						message = "あなたの方が21点に近いため、あなたの勝利です！";
						request.setAttribute("situationMessage", message);
						Result.win(request, (chip * 2));
						playerDao.cashBack(player.getId(), (chip * 2));
						playerDao.updateRecord(player, "win");
						nextPage = "result.jsp";
					}else if(dealer.getPoint() == playerInGame.getPoint()) {
						message = "合計点数が同じなので、このゲームは引き分けです";
						request.setAttribute("situationMessage", message);
						Result.draw(request, chip);
						playerDao.cashBack(player.getId(), chip);
						playerDao.updateRecord(player, "draw");
						nextPage = "result.jsp";
					}else {
						message = "ディーラーの方が21点に近いため、ディーラーの勝利です";
						request.setAttribute("situationMessage", message);
						Result.lose(request, chip);
						playerDao.updateRecord(player, "lose");
						nextPage = "result.jsp";
					}
				}
			}
			request.getRequestDispatcher(nextPage).forward(request, response);
		}catch(BlackjackException e) {
			
			//エラーメッセージを表示させる
			message = e.getMessage();
			request.setAttribute("message", message);
			
			//画面遷移先はログイン画面を指定
			nextPage = "play.jsp";
		}
	}
}
