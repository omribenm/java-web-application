package protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import tokenizer.FixedSeparatorMessageTokenizer;
import tokenizer.StringMessage;

public class ConnectionHandler implements Runnable {

	private CallBack callback;
	private BufferedReader in;
	private PrintWriter out;
	private Socket clientSocket;
	private ServerProtocol protocol;
	FixedSeparatorMessageTokenizer token;
	Charset charset;

	public ConnectionHandler(Socket acceptedSocket, ServerProtocol p) {
		in = null;
		out = null;
		clientSocket = acceptedSocket;
		protocol = p;
		charset = charset.defaultCharset();
		System.out.println("Accepted connection from client!");
		System.out.println("The client is from: " + acceptedSocket.getInetAddress() + ":" + acceptedSocket.getPort());

	}

	public void run() {

		String msg;

		try {
			initialize();
		} catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}

		try {
			process();
		} catch (IOException e) {
			System.out.println("Error in I/O");
		}

		System.out.println("Connection closed - bye bye...");
		close();

	}

	public void process() throws IOException
	{
		String msg;
		while ((msg = in.readLine()) != null){
			token.addBytes(token.getBytesForMessage(new StringMessage(msg)));// adding the msg to the tokenizer
			
			if(token.hasMessage()){
				
			System.out.println("Received \"" + msg+ "\" from client");
			
			protocol.processMessage(token.nextMessage(),(ProtocolCallback<StringMessage>)callback);// sending the next msg in the tokenizer to the protocol
			if (protocol.isEnd(new StringMessage(msg))) {
				break;
			}
	 	}
		}
		}

	// Starts listening
	public void initialize() throws IOException {
		// Initialize I/O
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
		System.out.println("I/O initialized");
		callback = new CallBack(out);
		token = new FixedSeparatorMessageTokenizer("/n", charset);
	}

	// Closes the connection
	public void close() {
		try {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}

			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Exception in closing I/O");
		}
	}

}