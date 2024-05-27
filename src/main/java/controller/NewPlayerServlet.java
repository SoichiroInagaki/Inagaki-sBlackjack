package controller;

import java.io.IOException;

import dao.PlayerDao;
import exception.BlackjackException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Player;

/**
 * Servlet implementation class NewPlayerServlet
 */
@WebServlet("/NewPlayerServlet")
public class NewPlayerServlet extends HttpServlet {
	/**
	 * 新規ユーザーのDB登録処理
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//リクエストから使用する情報を取得
		request.setCharacterEncoding("UTF-8");
		String playerName = request.getParameter("new_name");
		String playerPassword1 = request.getParameter("new_password1");
		String playerPassword2 = request.getParameter("new_password2");
		
		//使用する変数を用意
		String message = null;
		String nextPage = null;
		
		//新規パスワードと確認パスワードが一致しているかで動作を分岐させる
		if(playerPassword1.equals(playerPassword2)) {
			
			//一致している場合、新規ユーザーの登録処理に移る
			try {
				//DB接続用に、Daoクラスをインスタンス化
				PlayerDao playerDao = new PlayerDao();
				
				//入力されたユーザが既に存在しないか確認
				String playerNameFinded = playerDao.findPlayerName(playerName);
				
				//存在の有無で動作を分岐させる
				if(playerNameFinded == null) {
					
					//存在しなければユーザーを新規登録する
					Player player = new Player(playerName, playerPassword1);
					playerDao.insertPlayer(player);
					
					//処理完了画面に遷移する
					request.setAttribute("process", "新規ユーザーの登録");
					request.setAttribute("playerName", playerName);
					nextPage = "completed.jsp";
					
				}else {
					
					//存在していたら、元の画面に戻し、エラーを表示させる
					message = "そのニックネームは既に使用されています";
					request.setAttribute("message", message);
					nextPage = "new_player.jsp";
				}
								
			//登録処理中に例外が発生した場合、その概要を元の画面に表示する
			}catch(BlackjackException e) {
				message = e.getMessage();
				request.setAttribute("message", message);
				nextPage = "new_player.jsp";
			}
			
		}else {
			//一致しない場合、元の画面に戻し、エラーを表示させる
			message = "パスワードが一致しません";
			request.setAttribute("message", message);
			nextPage = "new_player.jsp";
		}
		
		//画面を遷移させる
		request.getRequestDispatcher(nextPage).forward(request, response);
		
	}
	
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
	}

}
