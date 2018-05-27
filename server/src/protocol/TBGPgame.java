package protocol;
import java.io.IOException;
/**
 * interface of TBGP game
 */
import java.util.ArrayList;
import java.util.Iterator;

import tokenizer.StringMessage;
public interface TBGPgame {
	/**
	 * 
	 * @param room to insilaized
	 */
	public  void initilaized(GameRoom room);
	/**
	 * 
	 * @param Resp to take from the player
	 * @param player that gave the response
	 */
	public boolean getResponse(String Resp, Player player);
/**
 * 
 * @param choise to take from the player
 * @param player that sent the choise.
 * @return
 */
	public boolean getchoises(int choise, Player player);
	

}