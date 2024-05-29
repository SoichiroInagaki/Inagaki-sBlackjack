package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class MenuServlet
 */
@WebServlet("/MenuServlet")
public class MenuServlet extends HttpServlet {
	
	//押下されたボタンに対応したページ・サーブレットに遷移
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//遷移先のページを代入する変数を用意
		String nextPage = null;
		
		//押されたボタンを判定してページの変数に代入
		switch(request.getParameter("clicked")) {
			case "play":
				nextPage = "play.jsp";
				break;
			case "record":
				nextPage = "RecordServlet";
				break;
			case "delete":
				nextPage = "delete.jsp";
				break;
			case "logout":
				//ログアウトの場合は、セッションの情報を消去する
				HttpSession session = request.getSession(false);
				session.invalidate();
				
				request.setAttribute("message", "ログアウトしました");
				nextPage = "login.jsp";
				break;
		}
		request.getRequestDispatcher(nextPage).forward(request, response);
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
