package model;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public class Action {
	
	//アクションがスプリットの時のメソッド
	public static void split(List<PlayerInGame> list, Deck deck) {
		
		//スプリット時の二組の手札を準備
		PlayerInGame splitA = new PlayerInGame();
		splitA.becomeSplitA();
		PlayerInGame splitB = new PlayerInGame();
		splitB.becomeSplitB();
		PlayerInGame playerInGame = list.get(0);
		splitA.prepareSplit(playerInGame.getHandCard(0), deck);
		splitB.prepareSplit(playerInGame.getHandCard(1), deck);
		
		//Aのペアをスプリットした時はアクションを終了にさせる
		if(playerInGame.checkPairOfA()) {
			splitA.actionBecomeEnd();
			splitB.actionBecomeEnd();
		}
		
		//リスト内の手札を入れ替え
		list.add(splitA);
		list.add(splitB);
		list.remove(0);
	}
	
	
	//アクションがヒットの時のメソッド
	public static void hit
		(PlayerInGame playerInGame, Deck deck, HttpServletRequest request) {
		
		//手札を1枚増やす
		playerInGame.hit(deck);
		
		//画面表示用のメッセージを登録する
		String message = "ヒットしました";
		if(playerInGame.isSplitB()) {
			request.setAttribute("splitBHit", message);
		}else {
			request.setAttribute("hit", message);
		}
		
		//バーストした場合はアクションを終了にする
		if(playerInGame.checkBust()) {
			playerInGame.actionBecomeEnd();
		}
	}
	
	
	//アクションがスタンドの時のメソッド
	public static void stand(PlayerInGame playerInGame) {
		playerInGame.actionBecomeEnd();
	}
	
	
	//アクション終了時のメッセージを登録するメソッド(スプリット時のみ使用)
	public static void setActionEndMessage
		(PlayerInGame playerInGame, HttpServletRequest request) {
		
		//アクションが終了している(bust or stand)手札に対して処理を実行
		if(playerInGame.actionIsEnd()) {
			String message;
			
			//バーストしている場合
			if(playerInGame.checkBust()) {
				message = "この手札はバーストしています";
				
				//バーストしている手札を判定してメッセージを登録
				if(playerInGame.isSplitA()) {
					request.setAttribute("actionAisEnd", message);
				}else if(playerInGame.isSplitB()) {
					request.setAttribute("actionBisEnd", message);
				}
			
			//バーストしていなかった場合
			}else {
				
				//スタンドしている手札を判定してメッセージを登録
				message = "この手札でスタンドしています";
				if(playerInGame.isSplitA()) {
					request.setAttribute("actionAisEnd", message);
				}else if(playerInGame.isSplitB()) {
					request.setAttribute("actionBisEnd", message);
				}
			}
		}
	}
}
