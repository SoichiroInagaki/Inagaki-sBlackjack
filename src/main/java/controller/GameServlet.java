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

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    //ゲーム開始時に用いるメソッド
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		Player player = (Player) session.getAttribute("player");
		int id = player.getId();
		int chip = Integer.valueOf(request.getParameter("bet"));
		session.setAttribute("bettingChip", chip);
		String message = null;
		String nextPage = null;
		
		try {
			PlayerDao playerDao = new PlayerDao();
			playerDao.bet(id, chip);
			nextPage = "in-game.jsp";
		}catch(BlackjackException e) {
			
			//エラーメッセージを表示させる
			message = e.getMessage();
			request.setAttribute("message", message);
			
			//画面遷移先はログイン画面を指定
			nextPage = "play.jsp";
		}
		
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		session.setAttribute("deck", deck);
		session.setAttribute("playerInGame", playerInGame);
		session.setAttribute("dealer", dealer);
		
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
	
	//ゲーム進行中に用いるメソッド
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//遷移先・遷移先に表示するメッセージを宣言
		String nextPage = null;
		String message = null;
		
		//処理に必要な属性を取得、宣言
		HttpSession session = request.getSession();
		Player player = (Player) session.getAttribute("player");
		Dealer dealer = (Dealer) session.getAttribute("dealer");
		PlayerInGame playerInGame = (PlayerInGame) 
				session.getAttribute("playerInGame");
		Deck deck = (Deck) session.getAttribute("deck");
		int chip = (Integer)session.getAttribute("bettingChip");
		double cashBackedChip = 0;
		
		try {
			PlayerDao playerDao = new PlayerDao();
			
			if(request.getParameter("clicked").equals("hit")) {
				playerInGame.hit(deck);
				if(playerInGame.confirmBurst()) {
					playerDao.updateRecord(player, "lose");
					nextPage = "burst-end.jsp";
				}else {
					nextPage = "in-game.jsp";
					message = "ヒットしました";
					request.setAttribute("message", message);
				}
			}else {
				dealer.hit(deck);
				request.setAttribute("countHit", dealer.countHit());
				if(dealer.confirmBurst()) {
					playerDao.updateRecord(player, "win");
					nextPage = "win-end.jsp";
					message = "ディーラーはバーストしました！";
					request.setAttribute("burstedDealer", message);
					if(playerInGame.countHand() == 2 && playerInGame.getPoint() == 21) {
						message = "BLACKJACK!!";
						request.setAttribute("blackjack", message);
						cashBackedChip = (chip * 2.5);
						playerDao.cashBack(player.getId(), (int)cashBackedChip);
					}else {
						cashBackedChip = (chip * 2);
						playerDao.cashBack(player.getId(), (int)cashBackedChip);
					}
				}else {
					if(dealer.getPoint() < playerInGame.getPoint()) {
						if(playerInGame.countHand() == 2 && playerInGame.getPoint() == 21) {
							playerDao.updateRecord(player, "win");
							nextPage = "win-end.jsp";
							message = "BLACKJACK!!";
							request.setAttribute("blackjack", message);
							cashBackedChip = (chip * 2.5);
							playerDao.cashBack(player.getId(), (int)cashBackedChip);
						}else {
							playerDao.updateRecord(player, "win");
							nextPage = "win-end.jsp";
							cashBackedChip = (chip * 2);
							playerDao.cashBack(player.getId(), (int)cashBackedChip);
						}
					}else if(dealer.getPoint() == playerInGame.getPoint()) {
						playerDao.updateRecord(player, "draw");
						nextPage = "draw-end.jsp";
						cashBackedChip = chip;
						playerDao.cashBack(player.getId(), (int)cashBackedChip);
					}else {
						playerDao.updateRecord(player, "lose");
						nextPage = "lose-end.jsp";
					}
				}
			}
			request.setAttribute("cashBackedChip", (int)cashBackedChip);
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
