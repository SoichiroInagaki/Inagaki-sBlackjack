package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Dealer;
import model.Deck;
import model.PlayerInGame;

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    //ゲーム開始時に用いるメソッド
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		HttpSession session = request.getSession();
		session.setAttribute("deck", deck);
		session.setAttribute("playerInGame", playerInGame);
		session.setAttribute("dealer", dealer);
		
		request.getRequestDispatcher("in-game.jsp").forward(request, response);;
	}
	
	//ゲーム進行中に用いるメソッド
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = null;
		String message = null;
		HttpSession session = request.getSession();
		Dealer dealer = (Dealer) session.getAttribute("dealer");
		PlayerInGame playerInGame = (PlayerInGame) 
				session.getAttribute("playerInGame");
		Deck deck = (Deck) session.getAttribute("deck");
		
		if(request.getParameter("clicked").equals("hit")) {
			playerInGame.hit(deck);
			if(playerInGame.confirmBurst()) {
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
				nextPage = "win-end.jsp";
				message = "ディーラーはバーストしました！";
				request.setAttribute("burstedDealer", message);
			}else {
				if(dealer.getPoint() < playerInGame.getPoint()) {
					if(playerInGame.getPoint() == 21) {
						nextPage = "win-end.jsp";
						message = "BLACKJACK!!";
						request.setAttribute("blackjack", message);
					}else {
						nextPage = "win-end.jsp";
					}
				}else if(dealer.getPoint() == playerInGame.getPoint()) {
					nextPage = "draw-end.jsp";
				}else {
					nextPage = "lose-end.jsp";
				}
			}
		}
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
}
