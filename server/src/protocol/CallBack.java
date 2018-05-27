package protocol;
import java.io.IOException;
import java.io.PrintWriter;

import tokenizer.StringMessage;
/**
 * the call back of a player ,saving the Printwriter of the client 
 * use to send message from the server to the client
 * @author omribenm
 *
 */
public class CallBack implements ProtocolCallback<StringMessage> {
	private PrintWriter client;
	/**
	 * constractur
	 * @param client
	 */
	public CallBack(PrintWriter client){
		this.client=client;
	}
	@Override
	/**
	 * sending the message to the client from the server.
	 */
	public void sendMessage(StringMessage msg) throws IOException {
		client.println(((msg.getMessage()))); 
	
	}
	

}
