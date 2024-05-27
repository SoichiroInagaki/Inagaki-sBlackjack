package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Dealer;
import model.Deck;
import model.PlayerInGame;

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		request.setAttribute("playerInGame", playerInGame);
		request.setAttribute("dealer", dealer);
		
		request.getRequestDispatcher("InGame.jsp");
	}
}
