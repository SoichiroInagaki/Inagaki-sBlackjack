package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AfterGameServlet")
public class AfterGameServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nextPage = null;
		String clicked = request.getParameter("clicked");
		
		if(clicked.equals("playAgain")) {
			nextPage = "PlayServlet";
		}else {
			nextPage = "menu.jsp";
		}
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
}
