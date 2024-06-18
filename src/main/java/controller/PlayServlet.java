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
import model.Player;

@WebServlet("/PlayServlet")
public class PlayServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		Player player = (Player) session.getAttribute("player");
		int chip = 0;
		String nextPage = null;
		String message = null;
		
		try {
			PlayerDao playerDao = new PlayerDao();
			chip = playerDao.getChip(player.getId());
			nextPage = "play.jsp";
		}catch(BlackjackException e) {
			message = e.getMessage();
			request.setAttribute("message", message);
			nextPage = "menu.jsp";
		}
		
		request.setAttribute("chip", chip);
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
}
