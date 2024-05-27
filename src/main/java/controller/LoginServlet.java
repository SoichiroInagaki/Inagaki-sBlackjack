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

/**
 * ログイン画面用のサーブレット
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	//ログイン用にdoPostメソッドを用いる
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String playerName = request.getParameter("login_name");
		String playerPassword = request.getParameter("login_password");
		String nextPage = null;
		
		try {
			//DB接続用のDaoクラスをインスタンス化
			PlayerDao playerDao = new PlayerDao();
			
			//入力内容でプレイヤーを検索
			Player player = playerDao.findPlayer(playerName, playerPassword);
			
			//playerに中身が入ればログイン成功とみなす
			//セッションにプレイヤー情報をセットする
			HttpSession session = request.getSession();
			session.setAttribute("player", player);
			
			//画面遷移先はメニュー画面を指定
			nextPage = "menu.jsp";
			
		}catch(BlackjackException e) {
			
			//エラーメッセージを表示させる
			String message = e.getMessage();
			request.setAttribute("message", message);
			
			//画面遷移先はログイン画面を指定
			nextPage = "login.jsp";
		}
		request.getRequestDispatcher(nextPage).forward(request, response);
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


}
