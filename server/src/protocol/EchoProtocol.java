package protocol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import tokenizer.StringMessage;

/**
 * TBGP PROTCOL
 */

public class EchoProtocol implements AsyncServerProtocol <StringMessage> {
	

	private HashMap<String,? extends TBGPgame> GamesLIst; 
	private boolean _shouldClose = false;
	private boolean _connectionTerminated = false;
	private GameBoard gameboard =GameBoard.getInstance();
	/**
	 * processes a message<BR>
	 * this simple interface prints the message to the screen, then composes a simple
	 * reply and sends it back to the client
	 *
	 * @param msg the message to process
	 * @return the reply that should be sent to the client, or null if no reply needed
	 */
	/**
	 * empty constractur
	 */
	public EchoProtocol(){};
	
	public void create(){
		GamesLIst=new HashMap<String,TBGPgame>();
		
	}
	/**
	 * process the messages from the client by the protocol game.
	 */
	public void processMessage(StringMessage msg,ProtocolCallback<StringMessage> call) {
		String mes=msg.getMessage();
		if(msg!=null){
		if (mes.startsWith("NICK ")&& mes.length()>4 ){
			String PlayerName=mes.substring(5);
			if (gameboard.addPlayer(new Player(PlayerName, call), call)){
				try {
					call.sendMessage(new StringMessage("SYSMSG: NICK ACCEPTED"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
				else{
					try {
						call.sendMessage(new StringMessage("SYSMSG: NICK REJECTED"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
	}
		
		else if(mes.startsWith("JOIN ")&& gameboard.hasNICK(call)){
				String roomName=mes.substring(5);
				String playerName= gameboard.getCallBack(call).getName();
				Player player= gameboard.getPlayer(playerName);
				if (gameboard.joinPlayer(roomName, player)){
					try {
						call.sendMessage(new StringMessage("SYSMSG: JOIN ACCEPTED " ));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
						else{
						try {
							call.sendMessage(new StringMessage("SYSMSG: JOIN  REJECTED " ));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
			}
			
			
		else if (mes.startsWith("STARTGAME ")&& gameboard.hasNICK(call)){
				String gameName =mes.substring(10);
				String playerName= gameboard.getCallBack(call).getName();
				Player player= gameboard.getPlayer(playerName);
				//String roomName = gameboard.getCallbackGameRoom(call);
				if(player.getCurrentRoom()!=null){
				if(gameboard.startGame(gameName, call)){
				try {
					
					call.sendMessage(new StringMessage("SYSMSG: STARTGAME ACCEPTED "));
				}
			
				 catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
				else{
					try {
						call.sendMessage(new StringMessage("SYSMSG: STARTGAME REJECTED"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
				}
				else{
				try {
					call.sendMessage(new StringMessage("SYSMSG: STARTGAME REJECTED"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
			
	
				
		else if (mes.startsWith("MSG ")&&gameboard.hasNICK(call)){
					String message =mes.substring(4);
					if(gameboard.USRMSG(message, call)){
					
					try {
						call.sendMessage(new StringMessage("SYSMSG: MSG ACCEPTED"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					else{
					try {
						call.sendMessage(new StringMessage("SYSMSG: MSG REJECTED"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}
		
		else if(mes.startsWith("TXTRESP ")&& gameboard.hasNICK(call)){
					String message =mes.substring(8);
					if(gameboard.TXTRESP(message, call)){
						try {
							call.sendMessage(new StringMessage("SYSMSG:TXTRESP ACCEPTD"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						try {
							call.sendMessage(new StringMessage("SYSMSG:TXTRESP REJECTED"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
		else if(mes.startsWith("SELECTRESP ")&& gameboard.hasNICK(call)){
					String message =mes.substring(11);
				    int num=Integer.parseInt(message);
				if (gameboard.SELECTRESP(num, call))
					try {
						call.sendMessage(new StringMessage("SYSMSG:SELECTRESP ACCEPTED"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					try {
						call.sendMessage(new StringMessage("SYSMSG:SELECTRESP REJECTED"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		else if (mes.equals("QUIT") && gameboard.hasNICK(call)){
					if (gameboard.QUIT(call)==true){
						try {
							call.sendMessage(new StringMessage("SYSMSG:QUIT ACCEPTED"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						_shouldClose=true;
					}
					else{
						try {
							call.sendMessage(new StringMessage("SYSMSG:QUIT REJECTED"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
		else if(mes.equals("LISTGAMES") && gameboard.hasNICK(call)){
			try {
				call.sendMessage(new StringMessage("SYSMSG:LISTGAMES ACCEPTED"+gameboard.GamesList()));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		else{
			try {
				call.sendMessage(new StringMessage("SYSMSG:COMMAND UNDFINED, TRY TO GET NICK FIRST!"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	}
	@Override
	/**
	 * checking if the client disconnected from the server
	 */
	public boolean isEnd(StringMessage msg) {
		if(msg.getMessage().equals("QUIT")&& _shouldClose){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public boolean shouldClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connectionTerminated() {
		// TODO Auto-generated method stub
		
	}
	
}
