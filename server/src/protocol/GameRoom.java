package protocol;

import java.util.HashMap;
/**
 * object representing a room in the game board
 * @author omribenm
 *
 */
public class GameRoom {


	private String Roomname;
	private boolean isPlaying=false;
	private TBGPgame game=null;
	
	/**
	 * constractur
	 * @param Roomname string name of the room
	 */
	public GameRoom(String Roomname){
	this.Roomname=Roomname;	
	}
	/**
	 * 
	 * @return the room name
	 */
	public String getName(){
		return Roomname;
	}
	
	/**
	 * 
	 * @return checking if the room is active.
	 */
	public boolean getIsPlaying(){
		return isPlaying;
	}
	/**
	 * setting the room to active.
	 */
	public void setIsPlayingTrue(){
	this.isPlaying=true;
	}
	/**
	 * setting the room to non active.
	 */
	public void setIsPlayingFalse(){
		this.isPlaying=false;
		}
	/**
	 * setting room with a game.
	 * @param Game to set
	 */
	public void setGame(TBGPgame Game){
		this.game=Game;
	}
	/**
	 * 
	 * @return the game
	 */
	public TBGPgame getGame(){
		return game;
	}
}
