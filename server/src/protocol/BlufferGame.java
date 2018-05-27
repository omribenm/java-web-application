package protocol;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.gson.Gson;

import protocol.readFromJson.question;
import tokenizer.StringMessage;

public class BlufferGame implements TBGPgame {
	private BufferedReader br;
	private ArrayList<question> questions;
	private GameBoard board;
	private GameRoom room;
	private ArrayList<Player> players;
	private ConcurrentHashMap<Answer, Player> playersAns;
	private int numOfPlayers;
	private ConcurrentHashMap <Player,int[]> playersscore;
	private ArrayList<Answer> answers;
	private int Round=-1;
	private question q1;
	private int choises;
	private ConcurrentHashMap<Player,Boolean> Isans;
	/**
	 * constractur -setting up the maps.
	 */
public BlufferGame(){
	board=board.getInstance();
	questions=new ArrayList<question>();
	playersscore=new ConcurrentHashMap <Player,int[]>();
	playersAns= new ConcurrentHashMap <Answer,Player>();
	Isans=new ConcurrentHashMap <Player,Boolean>();
	players=new ArrayList<Player>();
	
}
/**
 * setting a new game in a room.
 * taking the players list to the game
 * loading the question from the jason file.
 * 
 */
public void initilaized(GameRoom room){
	players= board.getPlayers(room.getName());
	this.room=room;
	try {
		br=	new BufferedReader (new FileReader("/users/studs/bsc/2016/omribenm/assaigment3.1/server/target/bluffer.json"));
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson=new Gson();
		readFromJson readJson=gson.fromJson(br, readFromJson.class);
		for(int i=0;i<readJson.qustions.length;i++){
			questions.add(readJson.qustions[i]);
		}
		playGame();
	}
/**
 * setting the room to active.
 * starting the game
 */
	public void playGame(){
		 Round=-1;
		room.setIsPlayingTrue();
		// maybe to put all in  a for loop - this is only for 1 
		Iterator iter=players.iterator();
		while (iter.hasNext()){
			Isans.put((Player)iter.next(),false);
		}
		this.numOfPlayers=players.size();
		answers=new ArrayList<Answer>();
		playround();
		
	// in the end of the round we need to erase the Q from the arrayList (remove)
	}
	/**
	 * if round< 3 plying anew round and asking a Question 
	 * else ending the game and printing the points summery.
	 */
	public void playround(){
		Iterator iter=players.iterator();
		while (iter.hasNext()){
			Isans.put((Player)iter.next(),false);
		}
		choises=0;
		Round=Round+1;
		answers.clear();
		if(Round==3){
			playersscore.forEach((k,v)->{
				int score= 0;
				String summery="GAMEMSG: summery: ";
				 for(int i=0;i<v.length;i++){
						 score=score+v[i];
						 
				 }
				 summery=k.getName()+","+score+" points";
				 board.SendMessegetoRoom(room.getName(), summery);
				 
			});
			room.setIsPlayingFalse();
		}
		else{
		askQuestion();
		}
	}
	/**
	 * asking a question from the list. 
	 */
	public void askQuestion(){
		int num= (int) (Math.random()*(questions.size()));
		q1= questions.get(num);
		answers.add(new Answer(null,q1.realAnswer));
		board.ASKTXT(q1.questionText, room.getName());
	}
	/**
	 * getting the false answers from the players.
	 */
	public boolean getResponse(String Resp, Player player) {
		if(Isans.containsKey(player)){
			if (Isans.get(player)==false){
				Isans.put(player, true);
		Answer ans= new Answer (player,Resp);
		playersAns.put(ans, player);
		answers.add(ans);
		Isans.put(player, true);
		if (answers.size()==numOfPlayers+1){
			long seed = System.nanoTime();
			Collections.shuffle(answers, new Random(seed));
			Isans.clear();
			Iterator iter=players.iterator();
			while (iter.hasNext()){
				Isans.put((Player)iter.next(),false);
			}
			sendChoises();
			return true;
		}
		else
			return true;
		} 
	
		}
		return false;
		}
	/**
	 * sending the players the answers choises.
	 */
			public void sendChoises(){
				
				String answerstxt="";
				int i=0;
				Iterator<Answer> iter=answers.iterator();
				while (iter.hasNext()){
					
					answerstxt=answerstxt+i+"."+iter.next().getAns();	
					i++;
				}
				board.ASKchoises(answerstxt, room.getName());
				}
			@Override
			/**
			 * gets the choises of the players and adding the points.
			 */
			public boolean getchoises(int choise, Player player) {
				if (choise>=0 && choise<answers.size()){
				if(Isans.containsKey(player)){
					if (Isans.get(player)==false){
						Isans.put(player, true);
				choises++;
				playersscore.putIfAbsent(player, new int[3]);
				Answer ans=answers.get(choise);
				if(ans.getAns().equals(q1.realAnswer.toLowerCase())){
					playersscore.get(player)[Round]=playersscore.get(player)[Round]+10;
					player.TrueIsanswer();
				}
				else{
					playersscore.get(playersAns.get(ans))[Round]+=5;
				}
				if (choises==numOfPlayers){
					playersscore.forEach((k,v)->{
					 if(k.Isanswer()==true){
						 try {
							k.getCallback().sendMessage(new StringMessage("GAMEMSG:CORRECT!+"+v[Round]));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 
				}
					 else{
						 try {
							k.getCallback().sendMessage(new StringMessage("GAMEMSG:WORNG!, the real answer is: "+q1.realAnswer+"+ "+v[Round]));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				k.FalseIsanswer();
	
				});
					Isans.clear();
					playround();
}
				
					}
					else{
						return false;}
				return true;
			}
				
			}	
				return false;
}
			
}
