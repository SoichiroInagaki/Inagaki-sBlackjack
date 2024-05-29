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

@WebServlet("/RecordServlet")
public class RecordServlet extends HttpServlet {
	
	//MenuServletのdoGetメソッドから連動して動作
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nextPage = null;
		HttpSession session = request.getSession();
		Player loginedPlayer = (Player) session.getAttribute("player");
		Player playerForRecord = null;
		Player[] rankedRecords = null;
		
		try {
			PlayerDao playerDao = new PlayerDao();
			playerForRecord = playerDao.getRecord(loginedPlayer);
			rankedRecords = playerDao.getRankedRecords();
			
			request.setAttribute("playerForRecord", playerForRecord);
			request.setAttribute("rankedRecords", rankedRecords);
			
			nextPage = "record.jsp";
			
		}catch(BlackjackException e) {
			String message = e.getMessage();
			request.setAttribute("message", message);
			nextPage = "menu.jsp";
		}
		request.getRequestDispatcher(nextPage).forward(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
