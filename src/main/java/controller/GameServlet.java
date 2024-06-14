package controller;

import java.io.IOException;
import java.util.ArrayList;
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
       
	
    //ゲーム開始時はdoGetメソッドを用いる
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		//カードを準備し、ディーラーとプレイヤーに手札を配る
		Deck deck = new Deck();
		PlayerInGame playerInGame = new PlayerInGame();
		Dealer dealer = new Dealer();
		playerInGame.prepareHand(deck);
		dealer.prepareHand(deck);
		
		//スプリット対応のため、プレイヤーの手札はリスト化する
		List<PlayerInGame> playerHandList = new ArrayList<>();
		playerHandList.add(playerInGame);
		
		//カード状況は複数の画面で共有するため、セッションに保持
		HttpSession session = request.getSession();
		session.setAttribute("deck", deck);
		session.setAttribute("playerHandList", playerHandList);
		session.setAttribute("dealer", dealer);
		
		//チップ状況はプレイヤーの手札に情報を保持
		int chip = Integer.valueOf(request.getParameter("bet"));
		playerInGame.setChip(chip);
		
		// 画面遷移用の変数を宣言
		String nextPage = null;
		
		//DBを利用する処理を行う
		try {
			//DB接続用に、プレイヤーのDaoインスタンスを取得
			PlayerDao playerDao = new PlayerDao();
			
			//賭けられたチップ枚数分、DBに登録されているチップ総量を減らす
			Player player = (Player) session.getAttribute("player");
			playerDao.bet(player.getId(), chip);
			
			//スプリット可能なら、プレイヤーの保有チップ枚数をリクエストに保持
			if(playerInGame.checkSplittable()) {
				int totalChips = playerDao.getChip(player.getId());
				request.setAttribute("totalChips", totalChips);
			}
			
			//ブラックジャック判定
			//勝った場合
			if(playerInGame.checkBlackjack() && !dealer.checkBlackjack()) {
				
				//配当されるチップ枚数を計算
				//ブラックジャックで勝った時の配当は2.5倍
				double winRate = 2.5; 
				double calculatedChip = (chip * winRate);
				int cashBackedChip = (int)calculatedChip;
				
				//メッセージを登録
				Result.winOfBlackjack(request);
				Result.win(request, cashBackedChip);
				
				//DBに配当チップと戦績を反映
				playerDao.cashBack(player.getId(), cashBackedChip);
				playerDao.updateRecord(player, "win");
				
				//遷移先は結果画面
				nextPage = "result.jsp";
				
			//引き分けた場合
			}else if(playerInGame.checkBlackjack() && dealer.checkBlackjack()) {
				
				//メッセージを登録
				Result.drawOfBlackjack(request);
				Result.draw(request, chip);
				
				//DBに引き分け回収のチップと戦績を反映
				playerDao.cashBack(player.getId(), chip);
				playerDao.updateRecord(player, "draw");
				
				//遷移先は結果画面
				nextPage = "result.jsp";
				
			//負けた場合
			}else if(!(playerInGame.checkBlackjack()) && dealer.checkBlackjack()) {
				
				//メッセージの登録
				Result.loseOfBlackjack(request);
				Result.lose(request, chip);
				
				/*
				 * DBに戦績の反映
				 * 試合に負けたため、キャッシュバックされるチップはない
				 */
				playerDao.updateRecord(player, "lose");
				
				//遷移先は結果画面
				nextPage = "result.jsp";
				
			//ブラックジャックが完成していなければ、ゲーム画面に遷移する
			}else {
				nextPage = "in-game.jsp";
			}
		}catch(BlackjackException e) {
			//エラーメッセージが出た時は取得してリクエストに保持
			request.setAttribute("message", e.getMessage());
			//遷移先はゲーム開始画面
			nextPage = "play.jsp";
		}
		//try処理が終わったら画面遷移を行う
		request.getRequestDispatcher(nextPage).forward(request, response);
	}	//doGetメソッドここまで
	
	
	//ゲーム進行中はdoPostメソッドを用いる
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
			
			
			
//			//スプリット時は2回、通常時は1回アクション処理の判定を行う
//			for(int i = 0; i < 2; i++) {
//				//JSPで入力されたアクションを取得する
//				action = request.getParameter("clicked");
//				if(splitting != null) {
//					if(pairOfA != null &&  i == 0) {
//						action = "stand";
//						playerInGame = splitA;
//					}else if(pairOfA != null && i == 1) {
//						action = "stand";
//						playerInGame = splitB;
//					}else if(i == 0) {
//						action = request.getParameter("actionA");
//						playerInGame = splitA;
//					}else {
//						action = request.getParameter("actionB");
//						playerInGame = splitB;
//					}
//				}else {
//					i++;
//				}
//				//アクションがスプリットのとき
//				if(action != null && action.equals("split")) {
//					playerDao.bet(player.getId(), chip);
//					splitA = new PlayerInGame();
//					splitB = new PlayerInGame();
//					splitA.prepareSplit(playerInGame.getHandCard(0), deck);
//					splitB.prepareSplit(playerInGame.getHandCard(1), deck);
//					session.setAttribute("splitA", splitA);
//					session.setAttribute("splitB", splitB);
//					splitting = true;
//					session.setAttribute("splitting", splitting);
//				//アクションがヒットのとき
//				}else if(action != null && action.equals("hit")) {
//					playerInGame.hit(deck);
//					message = "ヒットしました";
//					//スプリットした二つ目の手札をヒットした時だけ、それ専用の場所にメッセージを保持
//					if(splitting != null && i == 1){
//						request.setAttribute("splitBHit", message);
//					}else {
//						request.setAttribute("hit", message);
//					}
//					//バーストしたかを判定する
//					if(playerInGame.checkBust()) {
//						//バーストしていた場合
//						//スプリットしている場合、その手札にこれ以上アクションを行えないようにする
//						//敗北したという属性もセッションに付与
//						if(splitting != null) {
//							message = "この手札はバーストしています";
//							if(i == 0) {
//								session.setAttribute("actionAisEnd", message);
//								message = "21点を超えてバーストしているため、あなたの負けです";
//								request.setAttribute("situationMessage", message);
//								actionAisEnd = true;
//								gameResult = "lose";
//								session.setAttribute("result", "lose");
//							}else {
//								session.setAttribute("actionBisEnd", message);
//								message = "21点を超えてバーストしているため、あなたの負けです";
//								request.setAttribute("situationMessageOfB", message);
//								actionBisEnd = true;
//								gameResultOfB = "loseOfB";
//								session.setAttribute("resultOfB", "loseOfB");
//							}
//							if(gameResult != null && gameResultOfB != null) {
//								message = "両方の手札がバーストしてしまいました。あなたの負けです";
//								request.setAttribute("bustingPlayer", message);
//								gameResult = "splitWBust";
//								gameResultOfB = "splitWBust";
//							}
//						//通常プレイ時はゲーム終了
//						}else {
//							message = "21点を超えたのでバーストしてしまいました。あなたの負けです";
//							request.setAttribute("bustingPlayer", message);
//							gameEnd = true;
//							gameResult = "lose";
//						}
//					}
//				//アクションがスタンドのとき、または、スプリットしていて、既にバーストしていたとき
//				}else {
//					//スプリット時は、その手札にこれ以上アクションできないようにする
//					if(splitting != null) {
//						message = "この手札でスタンドしています";
//						if(i == 0) {
//							if(gameResult != null) {
//								message = "21点を超えてバーストしているため、あなたの負けです";
//								request.setAttribute("situationMessage", message);
//								message = "この手札はバーストしています";
//							}
//							session.setAttribute("actionAisEnd", message);
//							actionAisEnd = true;
//						}else {
//							if(gameResultOfB != null) {
//								message = "21点を超えてバーストしているため、あなたの負けです";
//								request.setAttribute("situationMessageOfB", message);
//								message = "この手札はバーストしています";
//							}
//							session.setAttribute("actionBisEnd", message);
//							actionBisEnd = true;
//						}
//					}
//					//通常プレイ時、またはスプリットした手札が両方アクション不可の時はゲーム終了
//					if(splitting == null || (actionAisEnd && actionBisEnd)) {
//						dealer.hit(deck);
//						request.setAttribute("countHit", dealer.countHit());
//						do {
//							if(splitting != null && gameResult != null && playerInGame.getPoint() > 21) {
//								message = "21点を超えてバーストしているため、あなたの負けです";
//								request.setAttribute("situationMessage", message);
//							}else if(splitting != null && gameResultOfB != null && playerInGame.getPoint() > 21){
//								message = "21点を超えてバーストしているため、あなたの負けです";
//								request.setAttribute("situationMessageOfB", message);
//							}else if(dealer.checkBust()) {
//								message = "21点を超えたので、ディーラーはバーストしました！";
//								request.setAttribute("bustingDealer", message);
//								//スプリットした両方の手札をスタンドしていた場合
//								if(splitting != null && gameResult == null && gameResultOfB == null) {	
//									gameResult = "splitWStand";
//									gameResultOfB = "splitWStand";
//									splitWStand = true;
//									request.setAttribute("splitWStand", splitWStand);
//								//スプリットした二つ目の手札をスタンドしていた場合
//								}else if(splitting != null && gameResultOfB == null) {
//									gameResultOfB = "winOfB";
//									message = "スタンドしていたため、あなたの勝利です！";
//									request.setAttribute("situationMessageOfB", message);
//								//スプリットした一つ目の手札、もしくは、通常プレイでスタンドしていた場合
//								}else if(splitting != null && gameResult == null){
//									gameResult = "win";
//									message = "スタンドしていたため、あなたの勝利です！";
//									request.setAttribute("situationMessage", message);
//								}else {
//									gameResult = "win";
//									gameEnd = true;
//								}
//							}else {
//								if(dealer.getPoint() < playerInGame.getPoint()) {
//									message = "あなたの方が21点に近いため、あなたの勝利です！";
//									if(splitting != null && gameResultOfB == null) {
//										request.setAttribute("situationMessageOfB", message);
//										gameResultOfB = "winOfB";
//									}else {
//										request.setAttribute("situationMessage", message);
//										gameResult = "win";		
//										gameEnd = true;
//									}
//								}else if(dealer.getPoint() == playerInGame.getPoint()) {
//									message = "合計点数が同じなので、このゲームは引き分けです";
//									if(splitting != null && gameResultOfB == null) {
//										request.setAttribute("situationMessageOfB", message);
//										gameResultOfB = "drawOfB";
//									}else {
//										request.setAttribute("situationMessage", message);
//										gameResult = "draw";
//										gameEnd = true;
//									}
//								}else {
//									message = "ディーラーの方が21点に近いため、ディーラーの勝利です";
//									if(splitting != null && gameResultOfB == null) {
//										request.setAttribute("situationMessageOfB", message);
//										gameResultOfB = "loseOfB";
//									}else {
//										request.setAttribute("situationMessage", message);
//										gameResult = "lose";
//										gameEnd = true;
//									}
//								}
//							}
//							//スプリット時で、片方のゲーム結果がまだ登録されていない場合
//							//参照ハンドを変更して、もう一度勝敗判別を行う
//							//通常プレイ時、架空のスプリット手札に結果を登録
//							if(splitting == null) {
//								gameResultOfB = "notSplitting";
//							}else if(splitting != null && gameResult == null) {
//								playerInGame = splitA;
//							}else if(splitting != null && gameResultOfB == null) {
//								playerInGame = splitB;
//							}
//						//ゲーム結果が両方とも登録されていればDBへの登録へ
//						}while(gameResult == null || gameResultOfB == null);			
//					}
//				}
//				if((splitting == null && gameEnd) || (actionAisEnd && actionBisEnd)) {
//					//スプリットしている場合、DBへの勝敗登録を2回行う
//					if(splitting != null) {
//						i = 0;
//					}
//					do {
//						String result;
//						if(splitting != null && splitting && i == 1) {
//							result = gameResultOfB;
//						}else {
//							result = gameResult;
//						}
//						switch(result) {
//						case "win":
//							Result.win(request, (chip * 2));
//							playerDao.cashBack(player.getId(), (chip * 2));
//							playerDao.updateRecord(player, "win");
//							break;
//						case "draw":
//							Result.draw(request, chip);
//							playerDao.cashBack(player.getId(), chip);
//							playerDao.updateRecord(player, "draw");
//							break;
//						case "lose":
//							Result.lose(request, chip);
//							playerDao.updateRecord(player, "lose");
//							break;
//						case "winOfB":
//							Result.winOfB(request, (chip * 2));
//							playerDao.cashBack(player.getId(), (chip * 2));
//							playerDao.updateRecord(player, "win");
//							break;
//						case "drawOfB":
//							Result.drawOfB(request, chip);
//							playerDao.cashBack(player.getId(), chip);
//							playerDao.updateRecord(player, "draw");
//							break;
//						case "loseOfB":
//							Result.loseOfB(request, chip);
//							playerDao.updateRecord(player, "lose");
//							break;
//						case "splitWBust":
//							Result.lose(request, (chip * 2));
//							playerDao.updateRecord(player, "lose");
//							playerDao.updateRecord(player, "lose");
//							nextPage = "result.jsp";
//							//2回分の登録が済んでいるため、ループを抜ける
//							if(i == 0) {
//								i++;
//							}
//							break;
//						case "splitWStand":
//							Result.win(request, (chip * 4));
//							playerDao.cashBack(player.getId(), (chip * 4));
//							playerDao.updateRecord(player, "win");
//							playerDao.updateRecord(player, "win");
//							nextPage = "result.jsp";
//							//2回分の登録が済んでいるため、ループを抜ける
//							if(i == 0) {
//								i++;
//							}
//							break;
//						default:
//							break;
//						}
//						//繰り返し用にiを加算
//						i++;
//					}while(i < 2);
//					nextPage = "result.jsp";
//				}else if(splitting != null && splitting && pairOfA != null && pairOfA) {
//					nextPage = "GameServlet";
//				}else {
//					nextPage = "in-game.jsp";
//				}
//			}
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
