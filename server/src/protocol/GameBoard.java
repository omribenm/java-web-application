package protocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import tokenizer.StringMessage;

import java.io.IOException;
import java.util.ArrayList;
/**
 * the game board contains all the games data,rooms ,players,games list.
 * @author omribenm
 *
 */
public class GameBoard {
	
	private ConcurrentHashMap <String,Player> players; // playerName+ player object
	private ConcurrentHashMap<String,GameRoom> Rooms;// roomname+ room object
	private ConcurrentHashMap<ProtocolCallback<StringMessage>,Player> callbacksMap;// callback+  PlayerObi
	private ConcurrentHashMap<String, ArrayList<Player>> RoomPlayers;// roomname+aaray list of players
	private Class <? extends TBGPgame> game;
	private ConcurrentHashMap<String, Class <? extends TBGPgame>> gamesList;
	private ArrayList<String> games;
	
	private static class SingeltonHolder{
		private static GameBoard instance = new GameBoard();
	}

	/**
	 * constractur - intilized all the maps.
	 */
	private GameBoard(){
		players= new ConcurrentHashMap<String,Player>();
		Rooms= new  ConcurrentHashMap<String,GameRoom>();
		callbacksMap= new ConcurrentHashMap<ProtocolCallback<StringMessage>,Player>();
		RoomPlayers= new ConcurrentHashMap<String, ArrayList<Player>>();
		gamesList= new ConcurrentHashMap<String, Class <? extends TBGPgame>>();
		game= BlufferGame.class;
		games=new ArrayList<String>();
		gamesList.put("BLUFFER",game);
		games.add("BLUFFER");
	}
	/**
	 * checking if the the client has nick name in the data 
	 * @param call -the client callback
	 * @return true or false
	 */
	public boolean hasNICK(ProtocolCallback<StringMessage> call){
		return callbacksMap.containsKey(call);
	}
	/**
	 * adding player to game board
	 * @param player to add
	 * @param call of the player
	 * @return if the process sucsses(true/false)
	 */
	public boolean addPlayer(Player player,ProtocolCallback<StringMessage> call){
		if (!players.containsKey(player.getName())&& (!callbacksMap.containsKey(call))){
		players.put(player.getName(),player);
		callbacksMap.put(player.getCallback(), player);
		return true;
		}
		else{
			return false;
		}
		
	}
	/*
	 * returning the available games in the game board
	 */
	public String GamesList(){
		String list=" ";
		Iterator<String> iter=games.iterator();
		while(iter.hasNext()){
			list=list+","+iter.next(); 
		}
		return list;
	}
	/**
	 * adding player to a room
	 * @param roomName to add the player to
	 * @param player to add
	 * @return if the process sucsses(true/false)
	 */
	public boolean joinPlayer (String roomName, Player player){
		//if(roomName!=null&& roomName!=""){
		boolean PlayerIsPlay=false;
		if(!(player.getCurrentRoom()==null)){
			PlayerIsPlay=Rooms.get(player.getCurrentRoom()).getIsPlaying();
		}
			if (Rooms.containsKey(roomName)){
				if (Rooms.get(roomName).getIsPlaying())
					return false;
				else{
					if(player.getCurrentRoom()==null){
					//Rooms.get(roomName).addPlayer(player);// i dont knoe if it neccesry
					RoomPlayers.get(roomName).add(player);// I added a map of roomName+playerObj so that we have information about a room and all his player
					player.SetCurrentRoom(roomName);
					return true;
					}
					else if(!PlayerIsPlay){
						//lock on t he roomplayers while deleting and adding
						// the player is in another room but the room is not active yet
						RoomPlayers.get( player.getCurrentRoom()).remove(player);// takes it out from his curr room
						player.SetCurrentRoom(roomName);
						//Rooms.get(roomName).addPlayer(player);// i dont knoe if it neccesry
						RoomPlayers.get(roomName).add(player);
						return true;
					}
				}
		}
		else if(!PlayerIsPlay){
			GameRoom room = new GameRoom(roomName);
			Rooms.put(roomName, room);
			RoomPlayers.put(roomName, new ArrayList<Player>());
			RoomPlayers.get(roomName).add(player);
			player.SetCurrentRoom(roomName);
			return true;
	}
	
		return false;
	}
		/**
		 * 
		 * @param call -the call back of the player
		 * @return the player with this call back
		 */
	public Player getCallBack(ProtocolCallback<StringMessage> call){
		return callbacksMap.get(call);
	}
	/**
	 * 
	 * @param name of the player
	 * @return the player
	 */
	public Player getPlayer(String name){
		return players.get(name);
	}
	/**
	 * 
	 * @param Room to send the message to
	 * @param msg to send
	 */
	public void SendMessegetoRoom(String Room,String msg){
		Iterator iter=RoomPlayers.get(Room).iterator();
		while(iter.hasNext()){
			try {
				((Player)iter.next()).getCallback().sendMessage(new StringMessage(msg));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}		
	/**
	 * 
	 * @param gameName the game to start
	 * @param call the call back of the player starting the game
	 * @return  if the process sucsses(true/false)
	 */
	public boolean startGame(String gameName,ProtocolCallback<StringMessage> call){
		if (gamesList.containsKey(gameName)){
		Player player= callbacksMap.get(call);
		GameRoom room = Rooms.get(player.getCurrentRoom());
		if(room!=null){
			if (room.getIsPlaying()){
				return false;
			}
			else{
			//	game=new BlufferGame(Rooms.get(room));
				try {
					room.setGame(gamesList.get(gameName).newInstance());
					room.getGame().initilaized(room);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//RoomPlayers.get(room).add(player);
				return true;
			}
		}
		}
		return false;	
		}
	/**\
	 * 
	 * @param room the room
	 * @return the array list of the players in the room
	 */
	public ArrayList<Player> getPlayers(String room){
		return RoomPlayers.get(room);
		
	}
	
	/**
	 * 
	 * @return instance of the game board
	 */
	public static GameBoard getInstance(){
		return SingeltonHolder.instance;
	}
	/**
	 * 
	 * @param msg to send
	 * @param call -the call back of the player sending the the msg
	 * @return if the process sucsses(true/false)
	 */
	public boolean USRMSG (String msg,ProtocolCallback<StringMessage> call){
		Player player= callbacksMap.get(call);
		String room = player.getCurrentRoom();
		if(room==null){
			return false;
		}
		else{
		ArrayList<Player> list= RoomPlayers.get(room);
		Iterator<Player> iter= list.iterator();
		while(iter.hasNext()){
			Player playerit=iter.next();
			try {	
				if (!(playerit.getCallback()==call))
				playerit.getCallback().sendMessage(new StringMessage("USRMSG: "+player.getName()+": "+ msg));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		return true;
	}
	}
		/**
		 * asking a question during a game
		 * @param question to ask
		 * @param room to ask
		 */
	public void ASKTXT(String question,String room ){
		
		Iterator<Player> it= RoomPlayers.get(room).iterator();
		while(it.hasNext()){
			try {
				it.next().getCallback().sendMessage(new StringMessage("ASKTXT: "+question ));
			} catch (IOException e) {	
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * sending answers options to the players in the room.
	 * @param choises to send
	 * @param room to send to
	 */
		public void ASKchoises(String choises,String room){
			
			Iterator<Player> iter= RoomPlayers.get(room).iterator();
			while(iter.hasNext()){
				try {
					iter.next().getCallback().sendMessage(new StringMessage(choises));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/**
		 * 
		 * @param messege that the player gave.
		 * @param call call back of the player
		 * @return if the process sucsses(true/false)
		 */
	public boolean TXTRESP (String messege,ProtocolCallback<StringMessage> call){
		Player player= callbacksMap.get(call);
		if(player.getCurrentRoom()!=null){
			if(Rooms.get(player.getCurrentRoom()).getIsPlaying()){
				Answer ans= new Answer(player ,messege);
				player.getCurrentRoom();
				if (Rooms.get(player.getCurrentRoom()).getGame().getResponse(messege,callbacksMap.get(call)))
				return true;
				else
					return false;
		}
		}
		
		return false;
	}
	/**
	 * 
	 * @param number that the player selected.
	 * @param call- call back of the player.
	 * @return if the process sucsses(true/false)
	 */
	public boolean SELECTRESP (int number,ProtocolCallback<StringMessage> call){
		String room = callbacksMap.get(call).getCurrentRoom();
		Player player= callbacksMap.get(call);
		player.getCurrentRoom();
		if(Rooms.get(player.getCurrentRoom()).getGame().getchoises(number,callbacksMap.get(call)))
				return true;
				else
					return false;
}
	/**
	 * getting the player out of the server-deleting him from all the maps
	 * @param call back of the player
	 * @return  if the process sucsses(true/false)
	 */
	public boolean QUIT (ProtocolCallback<StringMessage> call){
		Player player=callbacksMap.get(call);
		if(player.getCurrentRoom()==null){
			players.remove(player.getName());
			callbacksMap.remove(player.getCallback());
			return true;}
			else if (Rooms.get(player.getCurrentRoom()).getIsPlaying()==false){
				players.remove(player.getName());
				RoomPlayers.get(player.getCurrentRoom()).remove(player);
				callbacksMap.remove(player.getCallback());
				return true;
			}
			else{
				return false;
			}
		
	}
}

	


		
	

