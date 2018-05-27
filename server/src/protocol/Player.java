package protocol;

import tokenizer.StringMessage;
/**
 * object representing a player in a game.
 * @author omribenm
 *
 */
public class Player {
	private String currentRoom=null;
	private String name;
	private ProtocolCallback<StringMessage> callback;
	private Boolean Isanswer=false;

	/**
	 * cunstractur
	 * @param name of the player
	 * @param call call back of the player.
	 */
	public Player(String name,ProtocolCallback<StringMessage> call ){
		this.name=name;
		callback=call;
	}
	/**
	 * 
	 * @return if the answer of the player is true during a game.
	 */
	public boolean Isanswer(){
		return Isanswer;
	}
	/**
	 * setting the answer to false;
	 */
	public void FalseIsanswer(){
		Isanswer=false;
	}
	/**
	 * setting the answer to true
	 */
	public void TrueIsanswer(){
		Isanswer=true;
	}
	/**
	 *  
	 * @return the player name
	 */
	public String getName(){
		return name;
	}
	/**
	 * 
	 * @param setting the player current room
	 */
	public void SetCurrentRoom(String room){
		this.currentRoom=room;
	}
	/**
	 * 
	 * @return the current room of the player.
	 */
	public String getCurrentRoom(){
		return currentRoom;
	}
	/*public void SetCallback(ProtocolCallback<StringMessage> callback){
		this.callback=callback;
		
	}*/
	/**
	 * 
	 * @return the player callback.
	 */
	public ProtocolCallback<StringMessage> getCallback(){
		return callback;
		
	}
}
