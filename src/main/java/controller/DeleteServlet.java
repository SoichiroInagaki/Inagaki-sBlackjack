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
 * Servlet implementation class DeleteServlet
 */
@WebServlet("/DeleteServlet")
public class DeleteServlet extends HttpServlet {
	
	//プレイヤー削除画面で入力されたパスワードが合っているか確認する際に使用される
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		Player player = (Player) session.getAttribute("player");
		String correctPassword = player.getPassword();
		String enterdPassword = request.getParameter("delete_password");
		String nextPage = null;
		
		//入力されたパスワードが正しいか判定
		if(correctPassword.equals(enterdPassword)) {
			//正しいなら、確認画面に遷移
			nextPage = "confirm.jsp";
		}else {
			//違うなら、メッセージを表示させて元の画面に戻る
			String message = "パスワードに誤りがあります";
			request.setAttribute("message", message);
			nextPage = "delete.jsp";
		}
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
	
	
	//プレイヤー削除確認画面で承諾された際に使用される
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//使用する変数の宣言
		String message = null;
		String nextPage = null;
		
		if(request.getParameter("clicked").equals("yes")) {
			try {
				//DB接続用にDaoを取得
				PlayerDao playerDao = new PlayerDao();
				
				//プレイヤーの削除を実行
				HttpSession session = request.getSession();
				Player player = (Player) session.getAttribute("player");
				playerDao.deletePlayer(player);
				
				//削除したら完了画面にメッセージとともに飛ばす
				request.setAttribute("process", "プレイヤーの削除");
				request.setAttribute("playerName", player.getName());
				nextPage = "completed.jsp";
				
				//セッション情報も消す
				session = request.getSession(false);
				session.invalidate();
			}catch(BlackjackException e) {
				message = e.getMessage();
				request.setAttribute("message", message);
				nextPage = "new_player.jsp";
			}
		}else {
			message = "プレイヤー削除処理を中断しました";
			request.setAttribute("message", message);	
			nextPage = "delete.jsp";
		}
		//画面遷移
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
}
