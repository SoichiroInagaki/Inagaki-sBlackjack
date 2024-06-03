package model;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

//結果の定型文をリクエストスコープにセットするstaticメソッド群
public class Result {
	
	//実際に処理を行うメソッド
	private static void setResult(HttpServletRequest request, int chip, Map <String, String> map) {
		
		request.setAttribute("resultMessage", map.get("resultMessage"));
		request.setAttribute("chipMessage", map.get("chipMessage"));
		
	}
	
	//スプリットした二つ目の手札について、実際に処理を行うメソッド
	private static void setResultOfB(HttpServletRequest request, int chip, Map<String, String> map){
		request.setAttribute("resultMessageOfB", map.get("resultMessageOfB"));
		request.setAttribute("chipMessageOfB", map.get("chipMessageOfB"));
	}
	
	//勝った時の定型文をセットするメソッド
	public static void win(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessage", "You Win!!");
		map.put("chipMessage", "配当として、" + chip + "枚のチップを獲得しました！");
		setResult(request, chip, map);
	}
	
	//引き分けの時の定型文をセットするメソッド
	public static void draw(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessage", "Draw game!");
		map.put("chipMessage", "引き分けなので、賭けていた" + chip + "枚のチップが手元に戻りました");
		setResult(request, chip, map);
	}
	
	//負けた時の定型文をセットするメソッド
	public static void lose(HttpServletRequest request, int chip) {
		
		Map<String, String> map = new HashMap<>();
		map.put("resultMessage", "You lose......");
		map.put("chipMessage", "賭けていたチップ" + chip + "枚が没収されました");
		setResult(request, chip, map);
	}
	
	//スプリットした二つ目の手札について、勝った時の定型文をセットするメソッド
		public static void winOfB(HttpServletRequest request, int chip) {
			
			Map<String, String> map = new HashMap<>();
			map.put("resultMessageOfB", "You Win!!");
			map.put("chipMessageOfB", "配当として、" + chip + "枚のチップを獲得しました！");
			setResultOfB(request, chip, map);
		}
		
		//スプリットした二つ目の手札について、引き分けの時の定型文をセットするメソッド
		public static void drawOfB(HttpServletRequest request, int chip) {
			
			Map<String, String> map = new HashMap<>();
			map.put("resultMessageOfB", "Draw game!");
			map.put("chipMessageOfB", "引き分けなので、賭けていた" + chip + "枚のチップが手元に戻りました");
			setResultOfB(request, chip, map);
		}
		
		//スプリットした二つ目の手札について、負けた時の定型文をセットするメソッド
		public static void loseOfB(HttpServletRequest request, int chip) {
			
			Map<String, String> map = new HashMap<>();
			map.put("resultMessageOfB", "You lose......");
			map.put("chipMessageOfB", "賭けていたチップ" + chip + "枚が没収されました");
			setResultOfB(request, chip, map);
		}
}
