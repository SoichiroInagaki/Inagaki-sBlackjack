package controller;

import java.io.IOException;
import java.util.List;

import dao.PlayerDao;
import exception.BlackjackException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Action;
import model.Dealer;
import model.Deck;
import model.Player;
import model.PlayerInGame;
import model.Result;

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {

	protected void doPost
		(HttpServletRequest request, HttpServletResponse response) 
				throws ServletException, IOException {		
		
		//画面遷移に必要な変数を用意
		String nextPage = null;
		
		//DB接続を行うため、try文
		try {
			//DB接続用のDaoインスタンスを作成
			PlayerDao playerDao = new PlayerDao();
			
			//ログインプレイヤー・カード状況をセッションから取得
			HttpSession session = request.getSession();
			Player player = (Player) session.getAttribute("player");
			Dealer dealer = (Dealer) session.getAttribute("dealer");
			Deck deck = (Deck) session.getAttribute("deck");
			@SuppressWarnings("unchecked")
			List<PlayerInGame> playerHandList = 
				(List<PlayerInGame>) session.getAttribute("playerHandList");
			
			//チップ状況は手札から取得
			int chip = playerHandList.get(0).getChip();
			
			/*
			 * プレイヤーの手札それぞれに対してアクション処理を行う
			 * なお、スプリット時に行うリスト操作はループの外で
			 * 行う必要があるため、判別用の変数も宣言しておく
			 */
			boolean splitted = false;
			for(PlayerInGame playerInGame : playerHandList) {
				
				//手札に対して選択されたアクションを判別しながら取得
				String action = null;
				if(playerInGame.isSplitA()) {
					action = request.getParameter("actionA");
				}else if(playerInGame.isSplitB()) {
					action = request.getParameter("actionB");
				}else {
					action = request.getParameter("clicked");
				}
				
				//選択されたアクションを処理する
				if(action != null && action.equals("split")) {
					//スプリット時は判別用変数をtrueに
					splitted = true;
				}else if(action != null && action.equals("hit")) {
					Action.hit(playerInGame, deck, request);
				}else {
					Action.stand(playerInGame);
				}
			}
			
			//スプリット選択時のアクション処理を行う
			if(splitted) {	
				Action.split(playerHandList, deck);
				//スプリット時は再度チップをベットする必要がある
				playerDao.bet(player.getId(), chip);
			}
			
			//アクションが終了した手札を数える
			int endHands = 0;
			for(PlayerInGame playerInGame : playerHandList) {
				if(playerInGame.actionIsEnd()) {
					Action.setActionEndMessage(playerInGame, request);
					endHands ++;
				}
			}
			
			//プレイヤーの手札の全てがアクション終了なら、勝敗判定を行う
			if(endHands == playerHandList.size()) {
				
				//ディーラーに点数が17以上になるまでカードを引かせる
				Result.actionOfDealer(dealer, deck, request);
				
				//手札それぞれに対して、勝敗判定を行う
				for(PlayerInGame playerInGame : playerHandList) {
					Result.decideWinner(dealer, playerInGame, request);
				}
			//アクションがまだ終わっていないなら、遷移先をゲーム画面に指定
			}else {
				nextPage = "in-game.jsp";
			}
			
			//結果登録用の変数を宣言
			int rate = 0;
			int calculatedChip = 0;
			
			//勝敗を出した後、結果の登録を行う
			if(endHands == playerHandList.size()) {
				
				//この処理の後は結果画面に遷移する
				nextPage = "result.jsp";
				
				//スプリット時は、特殊な勝敗が登録されるかを判定する
				if(playerHandList.get(0).isSplitA()) {
					
					//スプリット時、両方の手札がバーストした場合
					if(playerHandList.get(0).checkBust() && 
							playerHandList.get(1).checkBust()) {
						
						//手札2個分の敗北を表示するため、rateは1*2
						rate = 2;
						calculatedChip = chip * rate;
						
						//メッセージの登録・DBへの反映
						Result.splitWBust(request, calculatedChip);
						playerDao.updateRecord(player, "lose");
						playerDao.updateRecord(player, "lose");
					/* 
					 * スプリット時、ディーラーがバーストしたのに対して、
					 * プレイヤーは両方の手札をスタンドしていた場合
					 */
					}else if(dealer.checkBust() && 
							!playerHandList.get(0).checkBust() && 
							!playerHandList.get(1).checkBust()) {
						
						//この状態であるという情報を手札に記録
						playerHandList.get(0).becomeSplitWStand();
						
						//手札2個分の勝利を表示するため、rateは2*2
						rate = 4;
						calculatedChip = chip * rate;
						
						//メッセージの登録・DBへの反映
						Result.win(request, calculatedChip);
						playerDao.cashBack(player.getId(), calculatedChip);
						playerDao.updateRecord(player, "win");
						playerDao.updateRecord(player, "win");
					}
				}
					
				//特殊勝敗でなければ、手札ごとに結果の登録を行う
				//特殊勝敗時はrateの値が変化するため、条件式はrateを用いる
				if(rate == 0){
					for(PlayerInGame playerInGame : playerHandList) {
						
						//スプリットした二組目の手札が勝った場合
						if(playerInGame.getResult().equals("win") && 
								playerInGame.isSplitB()) {
							
							//勝った時の配当は賭け金の2倍
							rate = 2;
							calculatedChip = chip * rate;
							
							//メッセージの登録・DBへの反映
							Result.winOfB(request, calculatedChip);
							playerDao.cashBack(player.getId(), calculatedChip);
							playerDao.updateRecord(player, "win");
						
						//一組目の手札が勝った場合
						}else if(playerInGame.getResult().equals("win")) {
							
							//勝った時の配当は賭け金の2倍
							rate = 2;
							calculatedChip = chip * rate;
							
							//メッセージの登録・DBへの反映
							Result.win(request, calculatedChip);
							playerDao.cashBack(player.getId(), calculatedChip);
							playerDao.updateRecord(player, "win");
							
						//スプリットした二組目の手札が引き分けた場合
						}else if(playerInGame.getResult().equals("draw") && 
								playerInGame.isSplitB()) {
							
							//メッセージの登録・DBへの反映
							Result.drawOfB(request, chip);
							playerDao.cashBack(player.getId(), chip);
							playerDao.updateRecord(player, "draw");
							
						//一組目の手札が引き分けた場合
						}else if(playerInGame.getResult().equals("draw")) {
							
							//メッセージの登録・DBへの反映
							Result.draw(request, chip);
							playerDao.cashBack(player.getId(), chip);
							playerDao.updateRecord(player, "draw");
							
						//スプリットした二組目の手札が負けた場合
						}else if(playerInGame.getResult().equals("lose") && 
								playerInGame.isSplitB()) {
							
							//メッセージの登録・DBへの反映
							Result.loseOfB(request, chip);
							playerDao.updateRecord(player, "draw");
							
						//一組目の手札が負けた場合
						}else {
							
							//メッセージの登録・DBへの反映
							Result.lose(request, chip);
							playerDao.updateRecord(player, "draw");
							
						}	//手札ごとの勝敗登録ブロックここまで
					}	//手札リストを参照するforループここまで
				}	//点数比較による結果の登録ブロックここまで
			}	//結果の登録ブロックここまで
		}catch(BlackjackException e) {
			//エラーメッセージを表示させる
			request.setAttribute("message", e.getMessage());
			//遷移先はログイン画面
			nextPage = "play.jsp";
		}
		//画面を遷移
		request.getRequestDispatcher(nextPage).forward(request, response);
	}
}
