package protocol;
/**
 * object the represent answers in the game.
 * @author omribenm
 *
 */
public class Answer {
	
	private String ans;
	private Player player;
	/**
	 * o
	 * @param player that gave the answer
	 * @param ans string that the player gave.
	 */
	public Answer(Player player, String ans){
		ans=ans.toLowerCase();
		this.player= player;
		this.ans=ans;
	}
	/**
	 * 
	 * @param ans setting an answer
	 */
	public void setAnswer(String ans){
		
		this.ans= ans;
	}
	/**
	 * setting the answer to null
	 */
	public void setAnswerToNull(){
		this.ans= null;
	}

	/**
	 * 
	 * @return the player
	 */
	public Player getPlayer(){
		return this.player;
	}
	/**
	 * 
	 * @return the answer(string)
	 */
	public String getAns(){
		return ans;
	}

}
